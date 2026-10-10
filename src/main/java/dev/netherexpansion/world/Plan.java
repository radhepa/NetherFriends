package dev.netherexpansion.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jspecify.annotations.Nullable;

/**
 * A village being laid out: where its centre is, which ground is taken, and the pieces placed so far.
 * Plans are made from {@link Terrain} only, before any block exists, and must be deterministic.
 */
public final class Plan {
    public final Terrain t;
    public final RandomSource random;
    public final BlockPos centre;
    /** How far from the centre residents wander. */
    public final int radius;
    private final List<BuildingPiece> pieces = new ArrayList<>();
    private final List<int[]> taken = new ArrayList<>();

    public Plan(Terrain t, RandomSource random, BlockPos centre, int radius) {
        this.t = t;
        this.random = random;
        this.centre = centre;
        this.radius = radius;
    }

    /** A spot for a building: its centre at floor level, its turn, and its half-size in building space. */
    public record Lot(BlockPos pos, Rotation rot, int hx, int hz) {
        /** The block just outside the front door (building-space z = -hz - 1). */
        public BlockPos door() {
            return this.pos.offset(Builder.rotX(this.rot, 0, -this.hz - 1), 0, Builder.rotZ(this.rot, 0, -this.hz - 1));
        }

        public BlockPos local(int x, int y, int z) {
            return this.pos.offset(Builder.rotX(this.rot, x, z), y, Builder.rotZ(this.rot, x, z));
        }

        int ax() { return isQuarterTurn(this.rot) ? this.hz : this.hx; }
        int az() { return isQuarterTurn(this.rot) ? this.hx : this.hz; }
    }

    static boolean isQuarterTurn(Rotation r) {
        return r == Rotation.CLOCKWISE_90 || r == Rotation.COUNTERCLOCKWISE_90;
    }

    /** The turn that makes a building's front (building-space north) face {@code facing}. */
    public static Rotation facing(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    /** The horizontal direction pointing most nearly from (fromX, fromZ) toward (toX, toZ). */
    public static Direction toward(int fromX, int fromZ, int toX, int toZ) {
        int dx = toX - fromX, dz = toZ - fromZ;
        if (Math.abs(dx) > Math.abs(dz)) return dx > 0 ? Direction.EAST : Direction.WEST;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    // -- ground ----------------------------------------------------------------------------------------------

    public boolean free(int x0, int z0, int x1, int z1) {
        for (int[] r : this.taken) {
            if (x0 <= r[2] && x1 >= r[0] && z0 <= r[3] && z1 >= r[1]) return false;
        }
        return true;
    }

    public void take(int x0, int z0, int x1, int z1) {
        this.taken.add(new int[]{Math.min(x0, x1), Math.min(z0, z1), Math.max(x0, x1), Math.max(z0, z1)});
    }

    public void take(Lot lot, int margin) {
        this.take(lot.pos.getX() - lot.ax() - margin, lot.pos.getZ() - lot.az() - margin,
                lot.pos.getX() + lot.ax() + margin, lot.pos.getZ() + lot.az() + margin);
    }

    public boolean free(Lot lot, int margin) {
        return this.free(lot.pos.getX() - lot.ax() - margin, lot.pos.getZ() - lot.az() - margin,
                lot.pos.getX() + lot.ax() + margin, lot.pos.getZ() + lot.az() + margin);
    }

    /** What a lot needs from the ground; null when the ground won't do. */
    @FunctionalInterface
    public interface Ground {
        @Nullable Integer floorAt(Plan plan, int x, int z);
    }

    /**
     * Ground that's walkable near the village's floor: a standing height within {@code step} of the centre's
     * with {@code clear} air above, at the lot's centre and its four corners (corners may sit up to 2 higher
     * or {@code drop} lower; foundations fill the rest). Returns the lot's floor.
     */
    public static Ground floorGround(int step, int clear, int drop, int hx, int hz) {
        return (plan, x, z) -> {
            int y = plan.t.floorNear(x, z, plan.centre.getY(), step, clear);
            if (y == Terrain.NONE || !plan.t.biome(x, y, z)) return null;
            for (int[] c : new int[][]{{-hx, -hz}, {hx, -hz}, {-hx, hz}, {hx, hz}}) {
                int cy = plan.t.floorNear(x + c[0], z + c[1], y, Math.max(drop, 3), 2);
                if (cy == Terrain.NONE || cy > y + 2 || cy < y - drop) return null;
            }
            return y;
        };
    }

    /**
     * Find a lot on a ring around the centre whose front faces the centre (or a random way when
     * {@code faceCentre} is false). Tries {@code tries} spots; returns null when none fits.
     */
    public @Nullable Lot ringLot(double rMin, double rMax, int hx, int hz, int margin, boolean faceCentre, Ground ground, int tries) {
        for (int i = 0; i < tries; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2;
            double r = rMin + this.random.nextDouble() * (rMax - rMin);
            int x = this.centre.getX() + Mth.floor(Math.cos(angle) * r);
            int z = this.centre.getZ() + Mth.floor(Math.sin(angle) * r);
            Direction face = faceCentre ? toward(x, z, this.centre.getX(), this.centre.getZ())
                    : Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
            Rotation rot = facing(face);
            Lot probe = new Lot(new BlockPos(x, this.centre.getY(), z), rot, hx, hz);
            if (!this.free(probe, margin)) continue;
            Integer y = ground.floorAt(this, x, z);
            if (y == null) continue;
            Lot lot = new Lot(new BlockPos(x, y, z), rot, hx, hz);
            this.take(lot, margin);
            return lot;
        }
        return null;
    }

    // -- pieces -------------------------------------------------------------------------------------------------

    /** Add a building on a lot; {@code below} and {@code above} bound how far down and up it may draw. */
    public BuildingPiece build(String kind, Lot lot, int below, int above, int... params) {
        return this.build(kind, lot.pos, lot.rot, -lot.hx - 1, -below, -lot.hz - 1, lot.hx + 1, above, lot.hz + 1, params);
    }

    /** Add a piece at {@code origin}, drawing within building-space box (x0, y0, z0)-(x1, y1, z1). */
    public BuildingPiece build(String kind, BlockPos origin, Rotation rot, int x0, int y0, int z0, int x1, int y1, int z1, int... params) {
        BoundingBox box = box(origin, rot, x0, y0, z0, x1, y1, z1);
        BuildingPiece piece = new BuildingPiece(kind, this.random.nextLong(), origin, rot, box, params, this.centre, this.radius);
        this.pieces.add(piece);
        return piece;
    }

    /** The world box covering building-space corners (x0, y0, z0) to (x1, y1, z1) around {@code origin}, turned. */
    public static BoundingBox box(BlockPos origin, Rotation rotation, int x0, int y0, int z0, int x1, int y1, int z1) {
        int ax = Builder.rotX(rotation, x0, z0), az = Builder.rotZ(rotation, x0, z0);
        int bx = Builder.rotX(rotation, x1, z1), bz = Builder.rotZ(rotation, x1, z1);
        return new BoundingBox(origin.getX() + Math.min(ax, bx), origin.getY() + y0, origin.getZ() + Math.min(az, bz),
                origin.getX() + Math.max(ax, bx), origin.getY() + y1, origin.getZ() + Math.max(az, bz));
    }

    public List<BuildingPiece> pieces() {
        return this.pieces;
    }

    public void addTo(StructurePiecesBuilder builder) {
        this.pieces.forEach(builder::addPiece);
    }
}
