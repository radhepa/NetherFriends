package dev.netherfriends.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

/**
 * Sailing on the lava sea, for the Magmari's strider barges: which cells a barge can float on, where it ties up at
 * a Mooring's ferry dock, and a way between the dock and open lava. Only loaded chunks are read: an unloaded cell
 * counts as closed, so working out a way never loads or generates anything.
 */
public final class Sea {
    private Sea() {}

    /** A barge's speed, in blocks a tick. */
    public static final double SPEED = 0.25;

    /** The height of the top lava block of the sea. */
    public static int surface(Level level) {
        return level.getSeaLevel() - 1;
    }

    /** Where a barge floats: a little below the top of the surface lava. */
    public static double floatY(Level level) {
        return surface(level) + 0.82;
    }

    /** Open lava: a lava surface with room above it for a barge and whoever rides it. */
    public static boolean open(Level level, int x, int z) {
        if (!level.hasChunk(x >> 4, z >> 4)) return false;
        int y = surface(level);
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, y, z);
        if (!level.getFluidState(p).is(FluidTags.LAVA)) return false;
        for (int dy = 1; dy <= 3; dy++) {
            p.setY(y + dy);
            if (!level.getBlockState(p).getCollisionShape(level, p).isEmpty()) return false;
        }
        return true;
    }

    // -- the dock ------------------------------------------------------------------------------------------------

    /**
     * A Mooring's ferry dock (MooringVillage.dock): {@code berth} is the lava a barge ties up on, alongside the
     * lander's sea side; {@code landing} is where you stand when you step ashore; {@code ferrier} is the
     * Ferrier's spot on the lander; {@code alongYaw} is a heading along the lander's long side.
     */
    public record Dock(BlockPos berth, BlockPos landing, BlockPos ferrier, float alongYaw, Direction seaward) {
        public static Dock of(BlockPos origin, Rotation rotation) {
            Direction along = rotation.rotate(Direction.EAST);
            return new Dock(Builder.at(origin, rotation, 0, -2, 4), Builder.at(origin, rotation, 0, 0, 1),
                    Builder.at(origin, rotation, -2, 0, 0), along.toYRot(), rotation.rotate(Direction.SOUTH));
        }
    }

    // -- ways ----------------------------------------------------------------------------------------------------

    /**
     * A way from the berth out to open lava about {@code reach} blocks off, heading as near the compass bearing
     * ({@code dx}, {@code dz}) as the sea allows: waypoints at float height, starting from the open lava nearest the
     * berth (where a barge ties up). Empty when the sea round the berth is closed in (or not loaded).
     */
    public static List<Vec3> wayOut(Level level, BlockPos berth, double dx, double dz, int reach) {
        int r = reach + 12, size = 2 * r + 1;
        byte[] cells = new byte[size * size];          // 0 unknown, 1 open, 2 closed
        int[] parent = new int[size * size];
        java.util.Arrays.fill(parent, -2);
        int bx = berth.getX(), bz = berth.getZ();
        // the berth itself, or the nearest open lava to it (land or a neighbouring deck can crowd a dock's sea side)
        int start = -1;
        for (int d = 0; d <= 6 && start < 0; d++) {
            for (int ox = -d; ox <= d && start < 0; ox++) {
                for (int oz = -d; oz <= d && start < 0; oz++) {
                    if (open(level, bx + ox, bz + oz)) start = (ox + r) * size + (oz + r);
                }
            }
        }
        if (start < 0) return List.of();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 1e-6) {
            dx = 1;
            dz = 0;
            len = 1;
        }
        dx /= len;
        dz /= len;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        parent[start] = -1;
        int best = -1, far = start;
        double bestScore = Double.NEGATIVE_INFINITY, farDist = 0;
        int sx = start / size - r, sz = start % size - r;
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int cell = queue.poll();
            int cx = cell / size - r, cz = cell % size - r;
            double dist = Math.sqrt((cx - sx) * (cx - sx) + (cz - sz) * (cz - sz));
            if (dist > farDist) {
                farDist = dist;
                far = cell;
            }
            if (dist >= reach - 6 && dist <= reach + 6) {
                double score = ((cx - sx) * dx + (cz - sz) * dz) / dist;
                if (score > bestScore) {
                    bestScore = score;
                    best = cell;
                }
            }
            for (int[] s : steps) {
                int nx = cx + s[0], nz = cz + s[1];
                if (Math.abs(nx) > r || Math.abs(nz) > r) continue;
                int next = (nx + r) * size + (nz + r);
                if (parent[next] != -2) continue;
                boolean nearBerth = Math.abs(nx - sx) <= 2 && Math.abs(nz - sz) <= 2;
                if (!(nearBerth ? cell(level, cells, size, r, bx, bz, nx, nz) : wide(level, cells, size, r, bx, bz, nx, nz))) continue;
                parent[next] = cell;
                queue.add(next);
            }
        }
        int target = best >= 0 ? best : farDist >= 12 ? far : -1;
        if (target < 0) return List.of();
        List<int[]> path = new ArrayList<>();
        for (int c = target; c >= 0; c = parent[c]) path.addFirst(new int[]{c / size - r, c % size - r});
        // pull the path straight wherever the barge can cut across
        List<int[]> straight = new ArrayList<>();
        straight.add(path.getFirst());
        int i = 0;
        while (i < path.size() - 1) {
            int j = path.size() - 1;
            while (j > i + 1 && !clear(level, cells, size, r, bx, bz, path.get(i), path.get(j))) j--;
            straight.add(path.get(j));
            i = j;
        }
        double y = floatY(level);
        List<Vec3> out = new ArrayList<>();
        for (int[] p : straight) out.add(new Vec3(bx + p[0] + 0.5, y, bz + p[1] + 0.5));
        return out;
    }

    private static boolean cell(Level level, byte[] cells, int size, int r, int bx, int bz, int x, int z) {
        if (Math.abs(x) > r || Math.abs(z) > r) return false;
        int i = (x + r) * size + (z + r);
        if (cells[i] == 0) cells[i] = (byte) (open(level, bx + x, bz + z) ? 1 : 2);
        return cells[i] == 1;
    }

    /** Open with open lava all round it: room for a barge three blocks wide. */
    private static boolean wide(Level level, byte[] cells, int size, int r, int bx, int bz, int x, int z) {
        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                if (!cell(level, cells, size, r, bx, bz, x + ox, z + oz)) return false;
            }
        }
        return true;
    }

    /** A straight run from a to b stays on open water, with room either side. */
    private static boolean clear(Level level, byte[] cells, int size, int r, int bx, int bz, int[] a, int[] b) {
        double dx = b[0] - a[0], dz = b[1] - a[1];
        int samples = (int) Math.ceil(Math.sqrt(dx * dx + dz * dz) * 3);
        for (int k = 0; k <= samples; k++) {
            double t = samples == 0 ? 0 : k / (double) samples;
            int x = (int) Math.floor(a[0] + dx * t + 0.5), z = (int) Math.floor(a[1] + dz * t + 0.5);
            boolean nearEnd = Math.abs(x - a[0]) + Math.abs(z - a[1]) <= 2 && k < 4;
            if (!(nearEnd ? cell(level, cells, size, r, bx, bz, x, z) : wide(level, cells, size, r, bx, bz, x, z))) return false;
        }
        return true;
    }
}
