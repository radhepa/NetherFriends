package dev.netherfriends.world;

import java.util.List;
import java.util.Optional;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.entity.People;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.jspecify.annotations.Nullable;

/**
 * A village of one of the Nether's peoples. Each kind finds its own sort of site (a cave floor, the open
 * lava sea, a warped forest tall enough for fungus trees) and lays itself out there, sampling the ground
 * from the noise as it goes. Only one people ever lives in a village.
 */
public abstract class NetherVillage extends Structure {
    protected NetherVillage(StructureSettings settings) {
        super(settings);
    }

    /**
     * The village kinds in order of precedence. Where two could overlap (near a biome border, or a Mooring off a
     * shore), the later one gives way, so villages of different peoples never generate into each other.
     */
    public static final List<String> KINDS = List.of("hold", "vigil", "loom", "eave", "mooring");
    /** How near (in chunks) another people's village may be before this one gives way. */
    static final int YIELD_CHUNKS = 7;

    /** Which kind of village this is ("hold", "vigil", ...): its structure and structure set share the name. */
    public abstract String kind();

    /** The people who live here. */
    public abstract People people();

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        if (this.crowded(ctx)) return Optional.empty();
        Terrain t = new Terrain(ctx);
        BlockPos centre = this.site(t, ctx.chunkPos(), ctx);
        if (centre == null) return Optional.empty();
        return Optional.of(new GenerationStub(centre, builder -> {
            Plan plan = new Plan(t, ctx.random(), centre, this.homeRadius());
            this.layout(plan);
            plan.addTo(builder);
        }));
    }

    /**
     * True when a village of a kind that comes before this one would be placed within {@link #YIELD_CHUNKS} of here
     * in a biome it accepts. Cheap: it asks each structure set where its one spot per region is, then samples the
     * biome there, without generating anything.
     */
    private boolean crowded(GenerationContext ctx) {
        int rank = KINDS.indexOf(this.kind());
        var sets = ctx.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET);
        ChunkPos here = ctx.chunkPos();
        for (int k = 0; k < rank; k++) {
            Optional<Holder.Reference<StructureSet>> set = sets.get(ResourceKey.create(Registries.STRUCTURE_SET, NetherFriends.id(KINDS.get(k))));
            if (set.isEmpty() || !(set.get().value().placement() instanceof RandomSpreadStructurePlacement spread)) continue;
            List<StructureSet.StructureSelectionEntry> entries = set.get().value().structures();
            if (entries.isEmpty()) continue;
            Structure other = entries.getFirst().structure().value();
            int spacing = spread.spacing();
            for (int rx = Math.floorDiv(here.x() - YIELD_CHUNKS, spacing); rx <= Math.floorDiv(here.x() + YIELD_CHUNKS, spacing); rx++) {
                for (int rz = Math.floorDiv(here.z() - YIELD_CHUNKS, spacing); rz <= Math.floorDiv(here.z() + YIELD_CHUNKS, spacing); rz++) {
                    ChunkPos spot = spread.getPotentialStructureChunk(ctx.seed(), rx * spacing, rz * spacing);
                    if (Math.abs(spot.x() - here.x()) > YIELD_CHUNKS || Math.abs(spot.z() - here.z()) > YIELD_CHUNKS) continue;
                    if (accepts(ctx, other, spot)) return true;
                }
            }
        }
        return false;
    }

    /** Whether {@code structure}'s biomes include the biome at the middle of {@code chunk}, at any of a few heights. */
    private static boolean accepts(GenerationContext ctx, Structure structure, ChunkPos chunk) {
        for (int y : new int[]{36, 64, 92}) {
            Holder<Biome> biome = ctx.biomeResolver().getNoiseBiome(QuartPos.fromBlock(chunk.getMiddleBlockX()), QuartPos.fromBlock(y),
                    QuartPos.fromBlock(chunk.getMiddleBlockZ()));
            if (structure.biomes().contains(biome)) return true;
        }
        return false;
    }

    /** Where in (or near) this chunk the village's centre goes, at the height people stand; null for nowhere. */
    protected abstract @Nullable BlockPos site(Terrain t, ChunkPos chunk, GenerationContext ctx);

    /** Place the village's pieces. Ways (paths, bridges) go in before buildings so buildings win where they meet. */
    protected abstract void layout(Plan plan);

    /** How far from the centre the residents wander. */
    protected int homeRadius() {
        return 32;
    }

    /** Count the probes around a centre where {@code ok} holds: a cheap measure of how open the ground is. */
    protected static int countRing(BlockPos centre, int radius, int probes, Probe ok) {
        int n = 0;
        for (int i = 0; i < probes; i++) {
            double a = Math.PI * 2 * i / probes;
            if (ok.test(centre.getX() + (int) Math.round(Math.cos(a) * radius), centre.getZ() + (int) Math.round(Math.sin(a) * radius))) n++;
        }
        return n;
    }

    /**
     * True when the ground round the centre is level enough for the middle of the village: at least seven of eight
     * probes {@code radius} out find a floor within {@code tolerance} of the centre's height. Keeps a village's
     * heart off the lip of a drop (to the lava sea or a lower cave floor).
     */
    protected static boolean levelCore(Terrain t, BlockPos centre, int radius, int tolerance) {
        return countRing(centre, radius, 8, (px, pz) -> t.floorNear(px, pz, centre.getY(), tolerance, 4) != Terrain.NONE) >= 7;
    }

    @FunctionalInterface
    protected interface Probe {
        boolean test(int x, int z);
    }
}
