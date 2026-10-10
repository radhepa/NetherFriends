package dev.netherfriends.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Paths that follow the ground and bridges that cross gaps and lava. Both are pieces of their own, laid
 * before the buildings so a building always wins where they meet.
 *
 * <p>A path is a polyline of points relative to its first point; at each step it looks for the real ground
 * near the planned height and paves it (a gap or lava gets a plank of bridge instead). A bridge is a straight,
 * level, axis-aligned deck with rails and posts.
 */
public final class Ways {
    private Ways() {}

    public static final int HOLD = 0, VIGIL = 1, EAVE = 2, LOOM = 3, MOORING = 4;

    static void register() {
        BuildingPiece.kind("path", Ways::drawPath);
        BuildingPiece.kind("bridge", Ways::drawBridge);
    }

    // -- planning ----------------------------------------------------------------------------------------------

    /** A path from {@code a} to {@code b} going along x first, then z (an L), {@code width} blocks wide. */
    public static void path(Plan plan, int style, int width, BlockPos a, BlockPos b) {
        BlockPos corner = new BlockPos(b.getX(), (a.getY() + b.getY()) / 2, a.getZ());
        path(plan, style, width, a, corner, b);
    }

    public static void path(Plan plan, int style, int width, BlockPos... points) {
        int[] params = new int[3 + points.length * 3];
        params[0] = style;
        params[1] = width;
        params[2] = points.length;
        BlockPos o = points[0];
        int x0 = 0, y0 = 0, z0 = 0, x1 = 0, y1 = 0, z1 = 0;
        for (int i = 0; i < points.length; i++) {
            BlockPos p = points[i].subtract(o);
            params[3 + i * 3] = p.getX();
            params[4 + i * 3] = p.getY();
            params[5 + i * 3] = p.getZ();
            x0 = Math.min(x0, p.getX()); y0 = Math.min(y0, p.getY()); z0 = Math.min(z0, p.getZ());
            x1 = Math.max(x1, p.getX()); y1 = Math.max(y1, p.getY()); z1 = Math.max(z1, p.getZ());
        }
        int w = width / 2 + 1;
        plan.build("path", o, Rotation.NONE, x0 - w, y0 - 9, z0 - w, x1 + w, y1 + 5, z1 + w, params);
    }

    /**
     * A level bridge from {@code a} to {@code b} at {@code a}'s height (the walking level). When they don't
     * line up it turns once, at a landing.
     */
    public static void bridge(Plan plan, int style, int width, BlockPos a, BlockPos b, int postDepth) {
        if (a.getX() == b.getX() || a.getZ() == b.getZ()) {
            straightBridge(plan, style, width, a, b, postDepth);
            return;
        }
        BlockPos corner = new BlockPos(b.getX(), a.getY(), a.getZ());
        straightBridge(plan, style, width, a, corner, postDepth);
        straightBridge(plan, style, width, corner, new BlockPos(b.getX(), a.getY(), b.getZ()), postDepth);
    }

    static void straightBridge(Plan plan, int style, int width, BlockPos a, BlockPos b, int postDepth) {
        int dx = b.getX() - a.getX(), dz = b.getZ() - a.getZ();
        if (dx == 0 && dz == 0) return;
        int len = Math.abs(dx) + Math.abs(dz);
        Direction along = dx > 0 ? Direction.EAST : dx < 0 ? Direction.WEST : dz > 0 ? Direction.SOUTH : Direction.NORTH;
        // building space: the bridge runs from z = 0 toward -z (north), so turn north onto 'along'
        Rotation rot = Plan.facing(along);
        int h = width / 2 + 1;
        plan.build("bridge", a, rot, -h, -postDepth, -len - h, h, 4, h, style, width, len, postDepth);
    }

    // -- drawing -------------------------------------------------------------------------------------------------

    /** Ground a path may pave: the Nether's own stone, soil and nylium, and earlier paving. */
    static boolean natural(BlockState s) {
        return s.is(BlockTags.BASE_STONE_NETHER) || s.is(BlockTags.NYLIUM) || s.is(Blocks.SOUL_SAND) || s.is(Blocks.SOUL_SOIL)
                || s.is(Blocks.GRAVEL) || s.is(Blocks.MAGMA_BLOCK) || s.is(Blocks.SMOOTH_BASALT) || s.is(Blocks.NETHER_WART_BLOCK)
                || s.is(Blocks.WARPED_WART_BLOCK) || s.is(Blocks.BONE_BLOCK) || s.is(Blocks.POLISHED_BLACKSTONE_BRICKS)
                || s.is(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS) || s.is(Blocks.POLISHED_BASALT) || s.is(Blocks.GILDED_BLACKSTONE)
                || s.is(Blocks.POLISHED_BLACKSTONE) || s.is(Blocks.GLOWSTONE);
    }

    /** Small things on the ground a path clears away. */
    static boolean litter(BlockState s) {
        return s.canBeReplaced() && s.getFluidState().isEmpty() || s.is(BlockTags.FIRE) || s.is(Blocks.CRIMSON_FUNGUS)
                || s.is(Blocks.WARPED_FUNGUS) || s.is(Blocks.CRIMSON_ROOTS) || s.is(Blocks.WARPED_ROOTS) || s.is(Blocks.NETHER_SPROUTS)
                || s.is(Blocks.BROWN_MUSHROOM) || s.is(Blocks.RED_MUSHROOM) || s.is(Blocks.TWISTING_VINES) || s.is(Blocks.TWISTING_VINES_PLANT);
    }

    static BlockState paving(Builder b, int style, int x, int z) {
        return switch (style) {
            case HOLD -> b.pick(x, 0, z, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS,
                    Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE, Blocks.POLISHED_BLACKSTONE).defaultBlockState();
            case VIGIL -> b.pick(x, 0, z, Blocks.SOUL_SOIL, Blocks.SOUL_SOIL, Blocks.SMOOTH_BASALT, Blocks.POLISHED_BASALT).defaultBlockState()
                    .trySetValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, Direction.Axis.Y);
            case EAVE -> b.pick(x, 0, z, Blocks.POLISHED_BASALT, Blocks.SMOOTH_BASALT, Blocks.SMOOTH_BASALT, Blocks.POLISHED_BLACKSTONE).defaultBlockState()
                    .trySetValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS, Direction.Axis.Y);
            case LOOM -> b.pick(x, 0, z, Blocks.WARPED_NYLIUM, Blocks.WARPED_WART_BLOCK, Blocks.WARPED_NYLIUM).defaultBlockState();
            default -> b.pick(x, 0, z, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE).defaultBlockState();
        };
    }

    static BlockState plank(int style) {
        return switch (style) {
            case HOLD -> Blocks.CRIMSON_PLANKS.defaultBlockState();
            case LOOM -> Blocks.WARPED_PLANKS.defaultBlockState();
            case VIGIL -> Blocks.BONE_BLOCK.defaultBlockState();
            default -> Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
        };
    }

    static void drawPath(Builder b) {
        int style = b.param(0, HOLD), width = b.param(1, 3), n = b.param(2, 0);
        int lo = -(width - 1) / 2, hi = width / 2;
        for (int i = 0; i + 1 < n; i++) {
            int ax = b.param(3 + i * 3, 0), ay = b.param(4 + i * 3, 0), az = b.param(5 + i * 3, 0);
            int bx = b.param(6 + i * 3, 0), by = b.param(7 + i * 3, 0), bz = b.param(8 + i * 3, 0);
            int steps = Math.max(Math.abs(bx - ax), Math.abs(bz - az));
            boolean alongX = Math.abs(bx - ax) >= Math.abs(bz - az);
            for (int s = 0; s <= steps; s++) {
                double f = steps == 0 ? 0 : (double) s / steps;
                int x = (int) Math.round(ax + (bx - ax) * f), z = (int) Math.round(az + (bz - az) * f);
                int y = (int) Math.round(ay + (by - ay) * f);
                for (int w = lo; w <= hi; w++) {
                    pave(b, style, alongX ? x : x + w, y, alongX ? z + w : z);
                }
                // a lamp every few steps along the right-hand edge
                if (s % 8 == 4 && steps > 6) lamp(b, style, alongX ? x : x + hi + 1, y, alongX ? z + hi + 1 : z);
            }
        }
    }

    /** Pave one column near height y (path coordinates are world offsets: paths are never turned). */
    static void pave(Builder b, int style, int x, int y, int z) {
        if (!b.inChunk(x, y, z)) return;
        for (int gy = y + 4; gy >= y - 6; gy--) {
            BlockState ground = b.get(x, gy, z);
            BlockState above = b.get(x, gy + 1, z);
            if (!ground.isAir() && ground.getFluidState().isEmpty() && !litter(ground) && (above.isAir() || litter(above))) {
                if (!natural(ground)) return;   // a wall, a roof or a fence: not ours to pave
                b.set(x, gy, z, paving(b, style, x, z));
                for (int c = 1; c <= 2; c++) {
                    if (litter(b.get(x, gy + c, z)) && !b.get(x, gy + c, z).isAir()) b.set(x, gy + c, z, Blocks.AIR);
                }
                return;
            }
        }
        // no ground near: a gap or lava. Lay a plank at the planned height so the way stays walkable.
        BlockState here = b.get(x, y - 1, z);
        if (here.isAir() || !here.getFluidState().isEmpty() || litter(here)) b.set(x, y - 1, z, plank(style));
    }

    /** A lamp post standing on the ground near height y beside a path. */
    static void lamp(Builder b, int style, int x, int y, int z) {
        if (!b.inChunk(x, y, z)) return;
        for (int gy = y + 3; gy >= y - 4; gy--) {
            BlockState ground = b.get(x, gy, z);
            if (!ground.isAir() && ground.getFluidState().isEmpty() && !litter(ground)) {
                if (!natural(ground)) return;
                for (int c = 1; c <= 3; c++) if (!b.get(x, gy + c, z).isAir() && !litter(b.get(x, gy + c, z))) return;
                BlockState post = switch (style) {
                    case VIGIL -> Kit.axis(Blocks.BONE_BLOCK, Direction.Axis.Y);
                    case EAVE -> Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Y);
                    case LOOM -> Kit.axis(Blocks.WARPED_STEM, Direction.Axis.Y);
                    default -> Blocks.CRIMSON_FENCE.defaultBlockState();
                };
                BlockState light = switch (style) {
                    case VIGIL, EAVE -> Kit.soulLantern(false);
                    case LOOM -> Blocks.SHROOMLIGHT.defaultBlockState();
                    default -> Kit.lantern(false);
                };
                Kit.lampPost(b, x, gy + 1, z, 2, post, light);
                return;
            }
        }
    }

    static void drawBridge(Builder b) {
        int style = b.param(0, MOORING), width = b.param(1, 3), len = b.param(2, 1), postDepth = b.param(3, 16);
        int h = width / 2 + 1;   // rails stand at x = -h and x = h; the deck runs -h..h
        BlockState deck, rail, post, light;
        switch (style) {
            case LOOM -> {
                deck = Blocks.WARPED_PLANKS.defaultBlockState();
                rail = Blocks.WARPED_FENCE.defaultBlockState();
                post = Kit.axis(Blocks.WARPED_STEM, Direction.Axis.Y);
                light = Blocks.SHROOMLIGHT.defaultBlockState();
            }
            case EAVE -> {
                deck = Blocks.POLISHED_BASALT.defaultBlockState();
                rail = Blocks.POLISHED_BLACKSTONE_WALL.defaultBlockState();
                post = Blocks.POLISHED_BASALT.defaultBlockState();
                light = Kit.soulLantern(false);
            }
            case HOLD -> {
                deck = Blocks.CRIMSON_PLANKS.defaultBlockState();
                rail = Blocks.CRIMSON_FENCE.defaultBlockState();
                post = Kit.axis(Blocks.STRIPPED_CRIMSON_STEM, Direction.Axis.Y);
                light = Kit.lantern(false);
            }
            default -> {
                deck = Kit.slabTop(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
                rail = Blocks.NETHER_BRICK_FENCE.defaultBlockState();
                post = Kit.axis(Blocks.BASALT, Direction.Axis.Y);
                light = Kit.lantern(false);
            }
        }
        for (int s = 0; s <= len; s++) {
            int z = -s;
            boolean end = s == 0 || s == len;
            for (int x = -h; x <= h; x++) {
                b.set(x, -1, z, deck);
                for (int c = 0; c <= 2; c++) {
                    if (!b.get(x, c, z).isAir() && !end) b.set(x, c, z, Blocks.AIR);
                }
            }
            if (end) continue;
            boolean postHere = s % 4 == 0;
            for (int side : new int[]{-h, h}) {
                if (postHere) {
                    b.set(side, 0, z, post);
                    b.set(side, 1, z, light);
                    if (style == MOORING || style == EAVE) {
                        b.foundation(side, -1, z, post, postDepth);
                    } else if (style == LOOM) {
                        // vine nets hang under the rope bridges of a Loom
                        for (int d = 2; d <= 4; d++) b.set(side, -d, z, b.chance(side, d, z, 0.6F) ? Blocks.COBWEB.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    }
                } else {
                    b.set(side, 0, z, rail);
                }
            }
            if (style == MOORING && s % 4 == 2) {
                // chains swing down from the deck toward the lava
                for (int d = 2; d <= 3; d++) b.set(0, -d, z, Kit.chainY());
            }
        }
    }
}
