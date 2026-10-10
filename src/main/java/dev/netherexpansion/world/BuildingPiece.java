package dev.netherexpansion.world;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * One building (or path, bridge, platform) of a village. It's stored as a kind name, a seed, a position,
 * a rotation and a few numbers, and drawn by the kind's {@link Drawing} each time a chunk it touches is
 * generated. The village plan decides where pieces go; the drawings decide what they look like.
 */
public class BuildingPiece extends StructurePiece {
    @FunctionalInterface
    public interface Drawing {
        void draw(Builder b);
    }

    private static final Map<String, Drawing> KINDS = new HashMap<>();

    /** Register how a kind of building is drawn. */
    public static void kind(String kind, Drawing drawing) {
        if (KINDS.put(kind, drawing) != null) throw new IllegalStateException("duplicate building kind " + kind);
    }

    public static boolean known(String kind) {
        return KINDS.containsKey(kind);
    }

    private final String kind;
    private final long seed;
    private final BlockPos origin;
    private final Rotation rotation;
    private final int[] params;
    private final BlockPos village;
    private final int villageRadius;

    public BuildingPiece(String kind, long seed, BlockPos origin, Rotation rotation, BoundingBox box, int[] params,
                         BlockPos village, int villageRadius) {
        super(NetherStructures.BUILDING_PIECE, 0, box);
        if (!KINDS.containsKey(kind)) throw new IllegalArgumentException("unknown building kind " + kind);
        this.kind = kind;
        this.seed = seed;
        this.origin = origin;
        this.rotation = rotation;
        this.params = params;
        this.village = village;
        this.villageRadius = villageRadius;
        this.setOrientation(null);
    }

    public BuildingPiece(CompoundTag tag) {
        super(NetherStructures.BUILDING_PIECE, tag);
        this.kind = tag.getStringOr("Kind", "");
        this.seed = tag.getLongOr("Seed", 0L);
        this.origin = new BlockPos(tag.getIntOr("X", 0), tag.getIntOr("Y", 0), tag.getIntOr("Z", 0));
        this.rotation = Rotation.values()[Math.floorMod(tag.getIntOr("Rot", 0), 4)];
        this.params = tag.getIntArray("P").orElse(new int[0]);
        this.village = new BlockPos(tag.getIntOr("VX", this.origin.getX()), tag.getIntOr("VY", this.origin.getY()), tag.getIntOr("VZ", this.origin.getZ()));
        this.villageRadius = tag.getIntOr("VR", 32);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putString("Kind", this.kind);
        tag.putLong("Seed", this.seed);
        tag.putInt("X", this.origin.getX());
        tag.putInt("Y", this.origin.getY());
        tag.putInt("Z", this.origin.getZ());
        tag.putInt("Rot", this.rotation.ordinal());
        tag.putIntArray("P", this.params);
        tag.putInt("VX", this.village.getX());
        tag.putInt("VY", this.village.getY());
        tag.putInt("VZ", this.village.getZ());
        tag.putInt("VR", this.villageRadius);
    }

    public String kind() {
        return this.kind;
    }

    public BlockPos origin() {
        return this.origin;
    }

    /** The centre of the village this piece belongs to. */
    public BlockPos village() {
        return this.village;
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random,
                            BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos) {
        Drawing drawing = KINDS.get(this.kind);
        if (drawing == null) return;   // a kind this version no longer has: leave the chunk as it is
        drawing.draw(new Builder(level, chunkBB, this.kind, this.origin, this.rotation, this.seed, this.params, this.village, this.villageRadius));
    }
}
