package dev.netherexpansion.world.village;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.netherexpansion.entity.People;
import dev.netherexpansion.entity.Ghastkin;
import dev.netherexpansion.entity.NetherEntities;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jspecify.annotations.Nullable;

/**
 * A Ghastkin Eave, in the Basalt Deltas: great basalt pillars with arms, and from the arms hang the Ghastkin's
 * homes like lanterns: pale glass pods lit blue by soul fire. A gallery runs round each pillar at pod height,
 * reached by a ladder; weeping vines trail from the pods to the ground. In the middle stands the Weeping Hall,
 * round a pool of the only water in the Nether: the Ghastkin's tears.
 */
public class EaveVillage extends NetherVillage {
    public static final MapCodec<EaveVillage> CODEC = simpleCodec(EaveVillage::new);
    static final int HOME = 0, TEARS = 1, GARDEN = 2;
    /** Pods hang with their floors this far above the pillar's foot; the pillar rises this much higher. */
    static final int GALLERY = 8, ABOVE = 10;

    public EaveVillage(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return NetherStructures.EAVE;
    }

    @Override
    public String kind() {
        return "eave";
    }

    @Override
    public People people() {
        return People.GHASTKIN;
    }

    @Override
    protected int homeRadius() {
        return 34;
    }

    @Override
    protected @Nullable BlockPos site(Terrain t, ChunkPos chunk, GenerationContext ctx) {
        for (int[] o : new int[][]{{0, 0}, {16, 0}, {-16, 0}, {0, 16}, {0, -16}}) {
            int x = chunk.getMiddleBlockX() + o[0], z = chunk.getMiddleBlockZ() + o[1];
            int y = t.lowestFloor(x, z, 14);
            if (y == Terrain.NONE || y > 84 || t.headroom(x, y, z, 30) < 20) continue;
            BlockPos c = new BlockPos(x, y, z);
            int near = countRing(c, 16, 12, (px, pz) -> t.floorNear(px, pz, y, 8, 12) != Terrain.NONE);
            if (near >= 8 && levelCore(t, c, 9, 3)) return c;
        }
        return null;
    }

    @Override
    protected void layout(Plan plan) {
        BlockPos c = plan.centre;
        plan.take(c.getX() - 8, c.getZ() - 8, c.getX() + 8, c.getZ() + 8);
        int pillars = 3 + plan.random.nextInt(2);
        List<Plan.Lot> lots = new ArrayList<>();
        Plan.Ground tall = (p, x, z) -> {
            int y = p.t.floorNear(x, z, c.getY(), 8, 4);
            if (y == Terrain.NONE || !p.t.biome(x, y, z) || p.t.headroom(x, y, z, GALLERY + ABOVE + 3) < GALLERY + ABOVE + 2) return null;
            return y;
        };
        for (int i = 0; i < pillars; i++) {
            Plan.Lot lot = plan.ringLot(15, 23, 9, 3, 2, true, tall, 40);
            if (lot != null) lots.add(lot);
        }
        for (Plan.Lot lot : lots) Ways.path(plan, Ways.EAVE, 3, lot.door(), c);
        plan.build("eave/hall", c, Rotation.NONE, -7, -12, -7, 7, 15, 7);
        for (Plan.Lot lot : lots) {
            int left = plan.random.nextInt(4) == 0 ? GARDEN : HOME;
            int right = plan.random.nextInt(3) == 0 ? TEARS : HOME;
            plan.build("eave/pillar", lot, 14, GALLERY + ABOVE + 3, left, right);
        }
    }

    // -- drawing ------------------------------------------------------------------------------------------------

    static final BlockState CORE = Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Y);
    static final BlockState BASALT = Kit.axis(Blocks.BASALT, Direction.Axis.Y);
    static final BlockState QUARTZ = Blocks.SMOOTH_QUARTZ.defaultBlockState();
    static final BlockState FRAME = Blocks.POLISHED_BLACKSTONE.defaultBlockState();

    public static void register() {
        BuildingPiece.kind("eave/hall", EaveVillage::hall);
        BuildingPiece.kind("eave/pillar", EaveVillage::pillar);
    }

    static void ghastkin(Builder b, double x, double y, double z, boolean child) {
        ghastkin(b, x, y, z, child, null);
    }

    /** A Ghastkin of this Eave; {@code job} null takes the building's (see Folk.jobAt). */
    static void ghastkin(Builder b, double x, double y, double z, boolean child, @Nullable String job) {
        b.spawn(NetherEntities.GHASTKIN, x, y + 0.4, z, job, (Ghastkin g) -> {
            if (child) g.setBaby(true);
        });
    }

    static BlockState candles(Block candle, int n) {
        return candle.defaultBlockState().setValue(CandleBlock.CANDLES, n).setValue(CandleBlock.LIT, true);
    }

    /**
     * A great pillar of basalt with a gallery round it at pod height, reached by a ladder up its front, and two
     * arms at the top from which hang a pod on each side ({@code params}: what each pod is).
     */
    static void pillar(Builder b) {
        int g = GALLERY, top = GALLERY + ABOVE;
        b.clear(-9, 0, -3, 9, top + 2, 3);
        // the pillar: a polished core in rough basalt, with a few shorter columns round its foot
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockState s = x == 0 && z == 0 ? CORE : BASALT;
                b.foundation(x, 0, z, s, 14);
                b.fill(x, 0, z, x, top, z, s);
            }
        }
        for (int[] p : new int[][]{{-2, 1}, {2, -1}, {1, 2}, {-1, 2}, {2, 1}}) {
            int h = 1 + (int) (b.noise(p[0], 0, p[1]) * 4);
            b.foundation(p[0], 0, p[1], BASALT, 10);
            b.fill(p[0], 0, p[1], p[0], h, p[1], BASALT);
        }
        // the ladder up the front
        for (int y = 0; y < g; y++) b.set(0, y, -2, Kit.facing(Blocks.LADDER, Direction.NORTH));
        // the gallery: a ring of polished basalt slabs, a low wall for a rail, openings to the pods
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) <= 1 && Math.abs(z) <= 1) continue;
                b.set(x, g - 1, z, Kit.slabTop(Blocks.POLISHED_BLACKSTONE_SLAB));
                boolean edge = Math.abs(x) == 3 || Math.abs(z) == 3;
                if (edge && !(Math.abs(x) == 3 && Math.abs(z) <= 1)) b.set(x, g, z, Blocks.POLISHED_BLACKSTONE_WALL);
            }
        }
        b.set(0, g - 1, -2, Kit.facing(Blocks.LADDER, Direction.NORTH));
        b.set(0, g, -3, Blocks.AIR);
        for (int[] p : new int[][]{{-3, -3}, {3, -3}, {-3, 3}, {3, 3}}) b.set(p[0], g + 1, p[1], Kit.soulLantern(false));
        // the crown and the arms
        b.fill(-2, top, -2, 2, top, 2, Kit.slab(Blocks.POLISHED_BLACKSTONE_SLAB));
        b.set(0, top, 0, CORE);
        for (int[] p : new int[][]{{-2, -2}, {2, -2}, {-2, 2}, {2, 2}}) b.set(p[0], top + 1, p[1], Kit.soulLantern(false));
        for (int x = 2; x <= 7; x++) {
            b.set(x, top - 1, 0, Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.X));
            b.set(-x, top - 1, 0, Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.X));
        }
        pod(b, -7, g, b.param(0, HOME), Direction.EAST, top - 2);
        pod(b, 7, g, b.param(1, HOME), Direction.WEST, top - 2);
    }

    /**
     * A lantern pod centred at x, floor walking height g, door toward {@code door} (the pillar), hung by a chain up
     * to {@code hangFrom}; weeping vines trail from its floor.
     */
    static void pod(Builder b, int cx, int g, int kind, Direction door, int hangFrom) {
        int roof = g + 4;
        // the step from the gallery
        int step = cx > 0 ? cx - 3 : cx + 3;
        b.fill(step, g - 1, -1, step, g - 1, 1, Kit.slabTop(Blocks.POLISHED_BLACKSTONE_SLAB));
        for (int x = cx - 2; x <= cx + 2; x++) {
            for (int z = -2; z <= 2; z++) {
                boolean edgeX = Math.abs(x - cx) == 2, edgeZ = Math.abs(z) == 2;
                b.set(x, g - 1, z, edgeX || edgeZ ? FRAME : kind == GARDEN ? Blocks.SOUL_SOIL.defaultBlockState() : QUARTZ);
                b.set(x, g - 2, z, Kit.slabTop(Blocks.POLISHED_BLACKSTONE_SLAB));
                for (int y = g; y < roof; y++) {
                    BlockState wall;
                    if (edgeX && edgeZ) wall = FRAME;
                    else if (!edgeX && !edgeZ) wall = Blocks.AIR.defaultBlockState();
                    else if (kind == GARDEN) wall = y == g ? Blocks.POLISHED_BLACKSTONE_WALL.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    else wall = y == g ? QUARTZ : Blocks.STAINED_GLASS_PANE.white().defaultBlockState();
                    b.set(x, y, z, wall);
                }
                // roof: a low two-step cap
                int ring = Math.max(Math.abs(x - cx), Math.abs(z));
                b.set(x, roof, z, ring == 0 ? FRAME : Kit.slabTop(Blocks.POLISHED_BLACKSTONE_SLAB));
                if (ring <= 1) b.set(x, roof + 1, z, ring == 0 ? FRAME : Kit.slab(Blocks.POLISHED_BLACKSTONE_SLAB));
            }
        }
        // the doorway toward the pillar, three high (Ghastkin are tall)
        int dx = cx + door.getStepX() * 2;
        b.fill(dx, g, 0, dx, g + 2, 0, Blocks.AIR.defaultBlockState());
        // the chain it hangs by
        for (int y = roof + 2; y <= hangFrom; y++) b.set(cx, y, 0, Kit.chainY());
        // vines trailing to the ground (climbable)
        for (int[] v : new int[][]{{-1, -1}, {1, 1}, {1, -1}}) {
            int len = 3 + (int) (b.noise(cx + v[0], g, v[1]) * 5);
            for (int i = 1; i <= len; i++) {
                b.set(cx + v[0], g - 2 - i, v[1], i == len ? Blocks.WEEPING_VINES.defaultBlockState() : Blocks.WEEPING_VINES_PLANT.defaultBlockState());
            }
        }
        // inside
        Kit.hangingLantern(b, cx, roof - 1, 0, 0, Kit.soulLantern(true));
        int side = -door.getStepX();
        switch (kind) {
            case TEARS -> {
                b.set(cx + side, g, -1, Blocks.BREWING_STAND);
                b.set(cx + side, g, 1, Blocks.BREWING_STAND);
                b.chest(cx, g, 1, Direction.NORTH, Loot.EAVE_TEARS);
                b.set(cx, g, -1, candles(Blocks.DYED_CANDLE.lightBlue(), 3));
                ghastkin(b, cx, g, 0, false, "Tearkeeper");
            }
            case GARDEN -> {
                for (int x = cx - 1; x <= cx + 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (b.chance(x, g, z, 0.6F)) b.set(x, g, z, b.pick(x, g, z, Blocks.CRIMSON_ROOTS, Blocks.WARPED_ROOTS, Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS));
                    }
                }
                b.set(cx, g, 0, Blocks.AIR);
            }
            default -> {
                b.fill(cx - 1, g, -1, cx + 1, g, 1, Blocks.CARPET.white().defaultBlockState());
                b.chest(cx + side, g, -1, door, Loot.EAVE_HOUSE);
                b.set(cx + side, g, 1, candles(Blocks.DYED_CANDLE.white(), 2));
                ghastkin(b, cx, g, 0, false);
                if (b.random.nextInt(3) == 0) ghastkin(b, cx, g, 1, true);
            }
        }
    }

    /** The Weeping Hall: a tall pale hall shaped like a lantern, round the pool of tears. */
    static void hall(Builder b) {
        int h = 5, wall = 8;
        b.clear(-h - 1, 0, -h - 1, h + 1, wall + 6, h + 1);
        b.foundation(-h - 1, -h - 1, h + 1, h + 1, -1, Blocks.BLACKSTONE.defaultBlockState(), 14);
        for (int x = -h - 1; x <= h + 1; x++) {
            for (int z = -h - 1; z <= h + 1; z++) {
                boolean rim = Math.abs(x) == h + 1 || Math.abs(z) == h + 1;
                b.set(x, -1, z, rim ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState() : QUARTZ);
            }
        }
        for (int y = 0; y < wall; y++) {
            for (int x = -h; x <= h; x++) {
                for (int z = -h; z <= h; z++) {
                    boolean ex = Math.abs(x) == h, ez = Math.abs(z) == h;
                    if (!ex && !ez) continue;
                    BlockState s;
                    if (ex && ez) s = CORE;
                    else if (y == 0 || y == wall - 1) s = QUARTZ;
                    else if (y >= 2 && y <= wall - 3 && (ex ? z : x) % 2 != 0) s = Blocks.STAINED_GLASS_PANE.lightBlue().defaultBlockState();
                    else s = Blocks.QUARTZ_BRICKS.defaultBlockState();
                    b.set(x, y, z, s);
                }
            }
        }
        // doorways front and back
        for (int z : new int[]{-h, h}) b.fill(-1, 0, z, 1, 2, z, Blocks.AIR.defaultBlockState());
        // the roof: a stepped pyramid of blackstone, a lantern-cap at the top
        for (int k = 0; k <= h + 1; k++) {
            int ring = h + 1 - k;
            for (int x = -ring; x <= ring; x++) {
                for (int z = -ring; z <= ring; z++) {
                    if (Math.max(Math.abs(x), Math.abs(z)) == ring) b.set(x, wall + k, z, k % 2 == 0
                            ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState() : Blocks.POLISHED_BLACKSTONE.defaultBlockState());
                }
            }
        }
        b.set(0, wall + h + 2, 0, Blocks.POLISHED_BLACKSTONE_WALL);
        b.set(0, wall + h + 3, 0, Kit.soulLantern(false));
        Kit.hangingLantern(b, 0, wall + h, 0, 4, Kit.soulLantern(true));
        // the pool of tears: water sunk in the floor on blue glass, a quartz rim, candles
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                boolean rim = Math.abs(x) == 2 || Math.abs(z) == 2;
                if (rim) {
                    b.set(x, 0, z, Kit.slab(Blocks.SMOOTH_QUARTZ_SLAB));
                } else {
                    b.set(x, -2, z, Blocks.STAINED_GLASS.lightBlue());
                    b.set(x, -1, z, Blocks.WATER);
                }
            }
        }
        for (int[] p : new int[][]{{-2, -2}, {2, -2}, {-2, 2}, {2, 2}}) b.set(p[0], 1, p[1], candles(Blocks.DYED_CANDLE.lightBlue(), 1 + (int) (b.noise(p[0], 1, p[1]) * 4)));
        for (int[] p : new int[][]{{-4, -3}, {-4, 3}, {4, -3}, {4, 3}}) b.set(p[0], 0, p[1], candles(Blocks.DYED_CANDLE.white(), 1 + (int) (b.noise(p[0], 0, p[1]) * 4)));
        for (int z = -1; z <= 1; z++) {
            b.set(-4, 0, z, Kit.stairs(Blocks.QUARTZ_STAIRS, Direction.WEST));
            b.set(4, 0, z, Kit.stairs(Blocks.QUARTZ_STAIRS, Direction.EAST));
        }
        b.chest(-4, 0, -4, Direction.EAST, Loot.EAVE_TEARS);
        ghastkin(b, -3, 0, -2, false);
        ghastkin(b, 3, 0, 2, false);
        if (b.random.nextBoolean()) ghastkin(b, 0, 0, -3, true);
    }
}
