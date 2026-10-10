package dev.netherexpansion.world.village;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.netherexpansion.entity.People;
import dev.netherexpansion.entity.NetherEntities;
import dev.netherexpansion.entity.Weft;
import dev.netherexpansion.world.Builder;
import dev.netherexpansion.world.BuildingPiece;
import dev.netherexpansion.world.Kit;
import dev.netherexpansion.world.Loot;
import dev.netherexpansion.world.NetherStructures;
import dev.netherexpansion.world.NetherVillage;
import dev.netherexpansion.world.Plan;
import dev.netherexpansion.world.Terrain;
import dev.netherexpansion.world.Ways;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jspecify.annotations.Nullable;

/**
 * A Weft Loom, in the Warped Forest: the Weft grow their own giant warped fungi and live on decks laid over the
 * caps, high above the forest floor, joined by rope bridges hung with vine nets. Woven huts, a dye house, a
 * net-hung lookout, and in the middle the Great Loom, where the Long Cloth (their history, a row a year)
 * hangs down through the deck toward the ground. Ladders run up the stalks for those who can't climb.
 */
public class LoomVillage extends NetherVillage {
    public static final MapCodec<LoomVillage> CODEC = simpleCodec(LoomVillage::new);
    static final int HOME = 0, DYES = 1, LOOKOUT = 2;

    public LoomVillage(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return NetherStructures.LOOM;
    }

    @Override
    public String kind() {
        return "loom";
    }

    @Override
    public People people() {
        return People.WEFT;
    }

    @Override
    protected int homeRadius() {
        return 40;
    }

    @Override
    protected @Nullable BlockPos site(Terrain t, ChunkPos chunk, GenerationContext ctx) {
        for (int[] o : new int[][]{{0, 0}, {16, 0}, {-16, 0}, {0, 16}, {0, -16}}) {
            int x = chunk.getMiddleBlockX() + o[0], z = chunk.getMiddleBlockZ() + o[1];
            int y = t.lowestFloor(x, z, 16);
            if (y == Terrain.NONE || y > 72) continue;
            if (t.headroom(x, y, z, 40) < 25) continue;
            BlockPos c = new BlockPos(x, y, z);
            int near = countRing(c, 18, 12, (px, pz) -> t.floorNear(px, pz, y, 9, 14) != Terrain.NONE);
            if (near >= 8 && levelCore(t, c, 8, 3)) return c;
        }
        return null;
    }

    @Override
    protected void layout(Plan plan) {
        Terrain t = plan.t;
        BlockPos c = plan.centre;
        int floor = c.getY();
        int lift = Mth.clamp(t.headroom(c.getX(), floor, c.getZ(), 40) - 12, 11, 15);
        int deckY = floor + lift;
        plan.take(c.getX() - 10, c.getZ() - 10, c.getX() + 10, c.getZ() + 10);

        List<Tree> trees = new ArrayList<>();
        List<Direction> arms = new ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
        int dyes = plan.random.nextInt(4);
        for (int i = 0; i < arms.size(); i++) {
            Direction arm = arms.get(i);
            Tree first = null;
            for (int d = 16; d <= 23 && first == null; d++) {
                first = this.tree(plan, c.relative(arm, d), deckY, 6, i == dyes ? DYES : HOME, arm);
            }
            if (first == null) continue;
            trees.add(first);
            if (plan.random.nextInt(3) > 0) {
                Tree second = null;
                int out = Math.abs(first.base.getX() - c.getX()) + Math.abs(first.base.getZ() - c.getZ());
                for (int d = out + 12; d <= out + 16 && second == null; d++) {
                    second = this.tree(plan, c.relative(arm, d), deckY, plan.random.nextBoolean() ? 4 : 6,
                            plan.random.nextBoolean() ? LOOKOUT : HOME, arm);
                }
                if (second != null) trees.add(second);
            }
        }
        // bridges first, all straight along the arms at deck height
        BlockPos hub = new BlockPos(c.getX(), deckY, c.getZ());
        for (Direction arm : arms) {
            BlockPos last = hub;
            for (Tree tree : trees) {
                BlockPos at = new BlockPos(tree.base.getX(), deckY, tree.base.getZ());
                if (Plan.toward(c.getX(), c.getZ(), at.getX(), at.getZ()) != arm) continue;
                Ways.bridge(plan, Ways.LOOM, 3, last, at, 0);
                last = at;
            }
        }
        plan.build("loom/great", c, Rotation.NONE, -11, -8, -11, 11, lift + 12, 11, lift);
        for (Tree tree : trees) {
            plan.build("loom/tree", tree.base, tree.rot, -tree.radius - 1, -8, -tree.radius - 1, tree.radius + 1, tree.stalk + 9,
                    tree.radius + 1, tree.stalk, tree.radius, tree.variant);
        }
    }

    /** A fungus tree standing at {@code spot} whose deck reaches deckY, if the ground there allows one. */
    private @Nullable Tree tree(Plan plan, BlockPos spot, int deckY, int radius, int variant, Direction arm) {
        Terrain t = plan.t;
        int x = spot.getX(), z = spot.getZ();
        int fy = t.floorNear(x, z, plan.centre.getY(), 10, 4);
        if (fy == Terrain.NONE || fy > deckY - 8 || !t.biome(x, fy, z)) return null;
        if (t.headroom(x, fy, z, deckY - fy + 10) < deckY - fy + 8) return null;
        if (!plan.free(x - radius - 1, z - radius - 1, x + radius + 1, z + radius + 1)) return null;
        plan.take(x - radius - 1, z - radius - 1, x + radius + 1, z + radius + 1);
        return new Tree(new BlockPos(x, fy, z), Plan.facing(arm.getOpposite()), deckY - fy, radius, variant);
    }

    record Tree(BlockPos base, Rotation rot, int stalk, int radius, int variant) {}

    // -- drawing ------------------------------------------------------------------------------------------------

    static final BlockState STEM = Kit.axis(Blocks.WARPED_HYPHAE, Direction.Axis.Y);
    static final BlockState WART = Blocks.WARPED_WART_BLOCK.defaultBlockState();
    static final BlockState PLANKS = Blocks.WARPED_PLANKS.defaultBlockState();
    static final BlockState FENCE = Blocks.WARPED_FENCE.defaultBlockState();

    public static void register() {
        BuildingPiece.kind("loom/great", LoomVillage::great);
        BuildingPiece.kind("loom/tree", LoomVillage::tree);
    }

    static void weft(Builder b, double x, double y, double z, boolean child) {
        weft(b, x, y, z, child, null);
    }

    /** A Weft of this Loom; {@code job} null takes the building's (see Folk.jobAt). */
    static void weft(Builder b, double x, double y, double z, boolean child, @Nullable String job) {
        b.spawn(NetherEntities.WEFT, x, y, z, job, (Weft w) -> {
            if (child) w.setBaby(true);
        });
    }

    /**
     * A giant warped fungus grown for living on: a stalk {@code half} blocks from its middle, a cap of radius r whose
     * flat top is a deck at y = s (deck blocks at s - 1), a wart curtain round the rim, a ladder up the front of
     * the stalk through a hatch in the deck, and a rail with gaps at the four compass points for the bridges.
     */
    static void fungus(Builder b, int s, double r, int half) {
        int ri = (int) Math.ceil(r);
        b.clear(-ri - 1, s - 5, -ri - 1, ri + 1, s + 7, ri + 1);
        b.clear(-half - 1, 0, -half - 1, half + 1, s - 5, half + 1);
        // stalk, flaring at the foot
        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                b.foundation(x, 0, z, STEM, 8);
                b.fill(x, 0, z, x, s - 2, z, STEM);
            }
        }
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int i = -half; i <= half; i += Math.max(1, half)) {
                int x = d.getStepX() * (half + 1) + (d.getAxis() == Direction.Axis.Z ? i : 0);
                int z = d.getStepZ() * (half + 1) + (d.getAxis() == Direction.Axis.X ? i : 0);
                if (b.chance(x, 0, z, 0.7F)) b.fill(x, 0, z, x, b.chance(x, 1, z, 0.5F) ? 1 : 0, z, STEM);
            }
        }
        // the cap: a flat deck over a thin wart dome with a hanging curtain of wart and shroomlight round the rim
        for (int x = -ri; x <= ri; x++) {
            for (int z = -ri; z <= ri; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r + 0.5) continue;
                if (d > r - 0.5) {
                    int drop = 2 + (b.chance(x, 0, z, 0.5F) ? 1 : 0) + (b.chance(x, 1, z, 0.3F) ? 1 : 0);
                    for (int y = s - 1; y >= s - 1 - drop; y--) {
                        b.set(x, y, z, b.chance(x, y, z, 0.12F) ? Blocks.SHROOMLIGHT.defaultBlockState() : WART);
                    }
                } else {
                    b.set(x, s - 1, z, PLANKS);
                    if (d > half + 0.5) b.set(x, s - 2, z, b.chance(x, s, z, 0.06F) ? Blocks.SHROOMLIGHT.defaultBlockState() : WART);
                    if (d > r - 1.5 && Math.abs(x) > 1 && Math.abs(z) > 1) b.set(x, s, z, FENCE);
                }
            }
        }
        // ladder up the front of the stalk and out through a hatch
        int lz = -half - 1;
        for (int y = 0; y < s; y++) b.set(0, y, lz, Kit.facing(Blocks.LADDER, Direction.NORTH));
        b.set(0, s - 2, lz, Kit.facing(Blocks.LADDER, Direction.NORTH));
        b.set(0, s - 1, lz, Kit.facing(Blocks.LADDER, Direction.NORTH));
        // roots and sprouts round the foot
        for (int x = -half - 3; x <= half + 3; x++) {
            for (int z = -half - 3; z <= half + 3; z++) {
                if (Math.abs(x) <= half + 1 && Math.abs(z) <= half + 1) continue;
                if (b.chance(x, -7, z, 0.18F) && b.get(x, -1, z).is(Blocks.WARPED_NYLIUM) && b.get(x, 0, z).isAir()) {
                    b.set(x, 0, z, b.pick(x, 0, z, Blocks.WARPED_ROOTS, Blocks.NETHER_SPROUTS, Blocks.WARPED_FUNGUS));
                }
            }
        }
    }

    /** Woven cloth: teal weave in a twill, orange stitches now and then. */
    static BlockState weave(Builder b, int x, int y, int z) {
        if (b.chance(x, y, z, 0.1F)) return Blocks.WOOL.orange().defaultBlockState();
        return (x + y + z) % 2 == 0 ? Blocks.WOOL.cyan().defaultBlockState() : PLANKS;
    }

    /** A round woven hut of radius 3 on the deck at y = s, door to the front, cone roof of wool. */
    static void hut(Builder b, int s) {
        int r = 3;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r + 0.4) continue;
                if (d > r - 0.6) for (int y = s; y <= s + 2; y++) b.set(x, y, z, weave(b, x, y, z));
                // cone roof: rings stepping in
                for (int k = 0; k <= 3; k++) {
                    double ring = r + 0.6 - k * 1.1;
                    if (d <= ring + 0.5 && d > ring - 0.6) {
                        b.set(x, s + 3 + k, z, k == 3 ? Blocks.SHROOMLIGHT.defaultBlockState()
                                : Kit.stairs(Blocks.WOOL_STAIRS.cyan(), Plan.toward(x, z, 0, 0)));
                    }
                }
            }
        }
        b.set(0, s + 6, 0, Blocks.SHROOMLIGHT);
        b.fill(0, s, -r, 0, s + 2, -r, Blocks.AIR.defaultBlockState());
        b.fill(-1, s, -r, 1, s + 1, -r, Blocks.AIR.defaultBlockState());
        b.fill(-2, s, -1, 2, s, 2, Blocks.CARPET.cyan().defaultBlockState());
        b.set(-2, s, 1, Kit.facing(Blocks.LOOM, Direction.EAST));
        b.chest(2, s, 1, Direction.WEST, Loot.LOOM_HOUSE);
        b.set(1, s, 2, Kit.facingAny(Blocks.BARREL, Direction.UP));
        Kit.hangingLantern(b, 0, s + 4, 0, 1, Kit.soulLantern(true));
    }

    static void tree(Builder b) {
        int s = b.param(0, 12), r = b.param(1, 6), variant = b.param(2, HOME);
        fungus(b, s, r + 0.4, 1);
        switch (variant) {
            case DYES -> {
                for (int[] p : new int[][]{{-3, -3}, {3, -3}, {-3, 3}, {3, 3}}) b.fill(p[0], s, p[1], p[0], s + 3, p[1], Kit.axis(Blocks.WARPED_STEM, Direction.Axis.Y));
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        b.set(x, s + 4, z, (x + z) % 2 == 0 ? Blocks.WOOL.orange().defaultBlockState() : Blocks.WOOL.cyan().defaultBlockState());
                    }
                }
                b.set(-2, s, 2, Blocks.CAULDRON);
                b.set(0, s, 2, Blocks.CAULDRON);
                b.set(2, s, 2, Blocks.SHROOMLIGHT);
                b.barrel(-2, s, -2, Direction.UP, Loot.LOOM_DYES);
                b.barrel(2, s, -2, Direction.UP, Loot.LOOM_DYES);
                b.set(2, s, 0, Blocks.WOOL.purple());
                b.set(-2, s, 0, Blocks.WOOL.lightBlue());
                b.set(-2, s + 1, 0, Blocks.WOOL.lightBlue());
                weft(b, 0, s, 0, false, "Dyer");
            }
            case LOOKOUT -> {
                b.fill(0, s, 0, 0, s + 4, 0, Kit.axis(Blocks.WARPED_STEM, Direction.Axis.Y));
                b.set(0, s + 5, 0, Blocks.SHROOMLIGHT);
                for (int x = -r; x <= r; x++) {
                    for (int z = -r; z <= r; z++) {
                        double d = Math.sqrt(x * x + z * z);
                        if (d > r + 0.4 || d <= r - 0.6 || Math.abs(x) <= 1 || Math.abs(z) <= 1) continue;
                        for (int y = s - 5; y <= s - 3; y++) if (b.chance(x, y, z, 0.55F)) b.set(x, y, z, Blocks.COBWEB);
                    }
                }
                weft(b, 1, s, 1, false, "Netcaster");
            }
            default -> {
                hut(b, s);
                weft(b, 0, s, 0, false);
                if (b.random.nextInt(3) > 0) weft(b, -1, s, -r + 1, false);
                if (b.random.nextInt(5) < 2) weft(b, 1, s, 1, true);
            }
        }
    }

    /** The Great Loom: the biggest tree, its loom frame, and the Long Cloth hanging down through the deck. */
    static void great(Builder b) {
        int s = b.param(0, 13), r = 9;
        fungus(b, s, r + 0.4, 2);
        // the frame: two tall stems and a crossbeam
        for (int x : new int[]{-5, 5}) b.fill(x, s, 4, x, s + 7, 4, Kit.axis(Blocks.WARPED_STEM, Direction.Axis.Y));
        b.fill(-5, s + 8, 4, 5, s + 8, 4, Kit.axis(Blocks.STRIPPED_WARPED_STEM, Direction.Axis.X));
        b.set(-5, s + 9, 4, Blocks.SHROOMLIGHT);
        b.set(5, s + 9, 4, Blocks.SHROOMLIGHT);
        // the Long Cloth: a row a year, top to bottom; sixty years of black (here five rows) for the Wither
        Block[] colours = {Blocks.WOOL.cyan(), Blocks.WOOL.lightBlue(), Blocks.WOOL.orange(), Blocks.WOOL.purple(), Blocks.WOOL.white(),
                Blocks.WOOL.blue()};
        int bottom = Math.max(2, s - 12);
        int blackFrom = s - 2 - b.random.nextInt(Math.max(1, s - 8));
        for (int y = s + 7; y >= bottom; y--) {
            Block row = y <= blackFrom && y > blackFrom - 5 ? Blocks.WOOL.black() : colours[b.random.nextInt(colours.length)];
            boolean fringe = y == bottom;
            for (int x = -4; x <= 4; x++) {
                if (fringe && x % 2 != 0) continue;
                b.set(x, y, 4, row.defaultBlockState());
            }
        }
        // around the deck: looms, the cloth-keepers' stores, a dye vat
        b.set(-3, s, -4, Kit.facing(Blocks.LOOM, Direction.SOUTH));
        b.set(3, s, -4, Kit.facing(Blocks.LOOM, Direction.SOUTH));
        b.set(-6, s, 0, Kit.facing(Blocks.LOOM, Direction.EAST));
        b.chest(6, s, 1, Direction.WEST, Loot.LOOM_HOUSE);
        b.barrel(6, s, -1, Direction.UP, Loot.LOOM_DYES);
        b.set(-6, s, 2, Blocks.CAULDRON);
        b.set(-6, s, 3, Blocks.SHROOMLIGHT);
        for (int[] p : new int[][]{{-4, -6}, {4, -6}}) Kit.lampPost(b, p[0], s, p[1], 2, Kit.axis(Blocks.WARPED_STEM, Direction.Axis.Y), Blocks.SHROOMLIGHT.defaultBlockState());
        weft(b, -2, s, 2, false, "Clothkeeper");
        weft(b, 2, s, -2, false, "Weaver");
        weft(b, 0, s, -5, false, "Weaver|Spinner");
        if (b.random.nextBoolean()) weft(b, 3, s, 2, true);
    }
}
