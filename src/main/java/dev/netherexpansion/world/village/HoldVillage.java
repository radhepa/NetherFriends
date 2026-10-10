package dev.netherexpansion.world.village;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.netherexpansion.entity.People;
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
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jspecify.annotations.Nullable;

/**
 * A Piglin Hold, in the Nether Wastes and Crimson Forest: crimson-timber longhouses on blackstone plinths
 * around a barter ring, the Hoard (a gold vault a Brute guards), a horn tower with a second Brute on top,
 * a forge, a few huts and hoglin pens. Its people are vanilla Piglins: show gold and they'll talk, show
 * none and they attack.
 */
public class HoldVillage extends NetherVillage {
    public static final MapCodec<HoldVillage> CODEC = simpleCodec(HoldVillage::new);

    static final boolean DEBUG = Boolean.getBoolean("netherexpansion.debugSites");

    public HoldVillage(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return NetherStructures.HOLD;
    }

    @Override
    public String kind() {
        return "hold";
    }

    @Override
    public People people() {
        return People.PIGLIN;
    }

    // -- site and layout -------------------------------------------------------------------------------------

    @Override
    protected @Nullable BlockPos site(Terrain t, ChunkPos chunk, GenerationContext ctx) {
        for (int[] o : new int[][]{{0, 0}, {16, 0}, {-16, 0}, {0, 16}, {0, -16}}) {
            int x = chunk.getMiddleBlockX() + o[0], z = chunk.getMiddleBlockZ() + o[1];
            int y = t.lowestFloor(x, z, 12);
            if (DEBUG) System.out.println("[hold-site] " + x + "," + z + " y=" + y);
            if (y == Terrain.NONE || y > 96) continue;
            BlockPos c = new BlockPos(x, y, z);
            int near = countRing(c, 14, 12, (px, pz) -> t.floorNear(px, pz, y, 5, 5) != Terrain.NONE);
            int far = countRing(c, 26, 12, (px, pz) -> t.floorNear(px, pz, y, 7, 5) != Terrain.NONE);
            if (DEBUG) System.out.println("[hold-site]   near=" + near + " far=" + far);
            if (near >= 9 && far >= 7 && levelCore(t, c, 8, 2)) return c;
        }
        return null;
    }

    record Wanted(String kind, int hx, int hz, int height, double rMin, double rMax) {}

    @Override
    protected void layout(Plan plan) {
        BlockPos c = plan.centre;
        plan.take(c.getX() - 9, c.getZ() - 9, c.getX() + 9, c.getZ() + 9);
        List<Wanted> wanted = new ArrayList<>(List.of(
                new Wanted("hold/hoard", 4, 4, 9, 13, 20),
                new Wanted("hold/tower", 2, 2, 17, 14, 26),
                new Wanted("hold/longhouse", 4, 7, 11, 15, 28),
                new Wanted("hold/longhouse", 4, 7, 11, 15, 30),
                new Wanted("hold/longhouse", 4, 7, 11, 15, 32),
                new Wanted("hold/forge", 3, 3, 7, 13, 28),
                new Wanted("hold/hut", 3, 3, 7, 13, 32),
                new Wanted("hold/hut", 3, 3, 7, 13, 34),
                new Wanted("hold/pen", 5, 4, 3, 22, 36)));
        if (plan.random.nextBoolean()) wanted.add(new Wanted("hold/pen", 5, 4, 3, 22, 38));
        if (plan.random.nextBoolean()) wanted.add(new Wanted("hold/hut", 3, 3, 7, 14, 36));

        List<Plan.Lot> lots = new ArrayList<>();
        List<Wanted> kinds = new ArrayList<>();
        for (Wanted w : wanted) {
            Plan.Lot lot = plan.ringLot(w.rMin, w.rMax, w.hx, w.hz, 2, true, Plan.floorGround(6, Math.min(w.height, 8), 6, w.hx, w.hz), 40);
            if (lot != null) {
                lots.add(lot);
                kinds.add(w);
            }
        }
        // ways first (buildings overwrite them where they meet)
        for (Plan.Lot lot : lots) {
            BlockPos door = lot.door();
            Ways.path(plan, Ways.HOLD, 3, door, c);
        }
        plan.build("hold/ring", c, net.minecraft.world.level.block.Rotation.NONE, -8, -10, -8, 8, 6, 8);
        for (int i = 0; i < lots.size(); i++) {
            plan.build(kinds.get(i).kind, lots.get(i), 12, kinds.get(i).height + 1);
        }
    }

    // -- the buildings -------------------------------------------------------------------------------------------

    static final BlockState PLANKS = Blocks.CRIMSON_PLANKS.defaultBlockState();
    static final BlockState POST = Kit.axis(Blocks.STRIPPED_CRIMSON_STEM, Direction.Axis.Y);
    static final BlockState BRICKS = Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
    static final BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();

    public static void register() {
        BuildingPiece.kind("hold/ring", HoldVillage::ring);
        BuildingPiece.kind("hold/longhouse", HoldVillage::longhouse);
        BuildingPiece.kind("hold/hut", HoldVillage::hut);
        BuildingPiece.kind("hold/hoard", HoldVillage::hoard);
        BuildingPiece.kind("hold/tower", HoldVillage::tower);
        BuildingPiece.kind("hold/forge", HoldVillage::forge);
        BuildingPiece.kind("hold/pen", HoldVillage::pen);
    }

    /** Blackstone brick with a few cracked and gilded stones. */
    static BlockState stone(Builder b, int x, int y, int z) {
        float n = b.noise(x, y, z);
        if (n < 0.05F) return Blocks.GILDED_BLACKSTONE.defaultBlockState();
        if (n < 0.22F) return Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
        return BRICKS;
    }

    /** A piglin of the Hold: armed with a crossbow or a golden sword (vanilla gives structure piglins nothing). */
    static void piglin(Builder b, double x, double y, double z, boolean baby) {
        b.spawn(EntityTypes.PIGLIN, x, y, z, (Piglin p) -> {
            if (baby) {
                p.setBaby(true);
            } else {
                boolean bow = b.chance((int) x, 3, (int) z, 0.5F);
                p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(bow ? Items.CROSSBOW : Items.GOLDEN_SWORD));
            }
        });
    }

    static void brute(Builder b, double x, double y, double z) {
        b.spawn(EntityTypes.PIGLIN_BRUTE, x, y, z, p -> {});
    }

    /** Level the ground for a building: clear the hill above, fill the hollow below, lay a floor. */
    static void groundwork(Builder b, int hx, int hz, int height, BlockState floor) {
        b.clear(-hx - 1, 0, -hz - 1, hx + 1, height + 5, hz + 1);
        b.foundation(-hx, -hz, hx, hz, -1, BLACKSTONE, 12);
        b.fill(-hx, -1, -hz, hx, -1, hz, floor);
    }

    /** The barter ring: a round paved square with a gold pedestal where Piglins trade. */
    static void ring(Builder b) {
        b.clear(-8, 0, -8, 8, 5, 8);
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > 8.2) continue;
                b.foundation(x, -1, z, BLACKSTONE, 10);
                BlockState top = d > 7.2 ? BRICKS : d > 6.2 ? Blocks.POLISHED_BLACKSTONE.defaultBlockState()
                        : b.chance(x, -1, z, 0.08F) ? Blocks.GILDED_BLACKSTONE.defaultBlockState() : stone(b, x, -1, z);
                b.set(x, -1, z, top);
            }
        }
        // the pedestal: polished blackstone, a gold block, four lamp posts
        b.fill(-1, 0, -1, 1, 0, 1, Blocks.POLISHED_BLACKSTONE.defaultBlockState());
        b.set(0, 1, 0, Blocks.GOLD_BLOCK);
        for (int[] p : new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}}) b.set(p[0], 1, p[1], Kit.slab(Blocks.POLISHED_BLACKSTONE_SLAB));
        for (int[] p : new int[][]{{-6, 0}, {6, 0}, {0, -6}, {0, 6}}) {
            Kit.lampPost(b, p[0], 0, p[1], 2, Blocks.CRIMSON_FENCE.defaultBlockState(), Kit.lantern(false));
        }
        piglin(b, -3, 0, 2, false);
        piglin(b, 3, 0, -2, false);
        piglin(b, 2, 0, 4, true);
    }

    /** A crimson longhouse, 9 by 15, on a blackstone plinth, with a steep gable roof. */
    static void longhouse(Builder b) {
        int hx = 4, hz = 7;
        groundwork(b, hx, hz, 11, PLANKS);
        b.walls(-hx, -1, -hz, hx, -1, hz, BRICKS);
        b.walls(-hx, 0, -hz, hx, 0, hz, BRICKS);
        b.walls(-hx, 1, -hz, hx, 4, hz, PLANKS);
        for (int z : new int[]{-7, -3, 0, 3, 7}) {
            for (int x : new int[]{-hx, hx}) b.fill(x, 0, z, x, 4, z, POST);
        }
        // windows between the posts, barred with fence
        for (int z : new int[]{-5, -2, 2, 5}) {
            for (int x : new int[]{-hx, hx}) b.set(x, 2, z, Blocks.CRIMSON_FENCE);
        }
        // roof: stairs stepping up to a slab ridge, overhanging a block all round
        for (int k = 0; k <= 4; k++) {
            int y = 5 + k, x = 5 - k;
            for (int z = -hz - 1; z <= hz + 1; z++) {
                b.set(x, y, z, Kit.stairs(Blocks.CRIMSON_STAIRS, Direction.WEST));
                b.set(-x, y, z, Kit.stairs(Blocks.CRIMSON_STAIRS, Direction.EAST));
            }
        }
        for (int z = -hz - 1; z <= hz + 1; z++) b.set(0, 10, z, Kit.slab(Blocks.CRIMSON_SLAB));
        for (int y = 5; y <= 9; y++) {
            for (int x = -(9 - y); x <= 9 - y; x++) {
                b.set(x, y, -hz, PLANKS);
                b.set(x, y, hz, PLANKS);
            }
        }
        b.set(0, 7, -hz, Blocks.SHROOMLIGHT);
        b.set(0, 7, hz, Blocks.SHROOMLIGHT);
        b.fill(-hx, 5, -hz + 1, hx, 5, hz - 1, Blocks.AIR.defaultBlockState());
        // a crossbeam under the ridge, lanterns hanging from it
        for (int z = -hz + 1; z < hz; z++) b.set(0, 9, z, Kit.axis(Blocks.STRIPPED_CRIMSON_STEM, Direction.Axis.Z));
        Kit.hangingLantern(b, 0, 8, -3, 2, Kit.lantern(true));
        Kit.hangingLantern(b, 0, 8, 4, 2, Kit.lantern(true));
        // door, lintel, lamps
        Kit.door(b, 0, 0, -hz, Blocks.CRIMSON_DOOR, Direction.NORTH);
        b.set(0, 2, -hz, PLANKS);
        b.fill(-1, 3, -hz, 1, 3, -hz, Kit.axis(Blocks.STRIPPED_CRIMSON_STEM, Direction.Axis.X));
        b.set(0, 4, -hz - 1, Blocks.GOLD_BLOCK);
        for (int x : new int[]{-2, 2}) Kit.lampPost(b, x, 0, -hz - 1, 1, Blocks.CRIMSON_FENCE.defaultBlockState(), Kit.lantern(false));
        // inside: a hearth, benches, the house's gold and stores
        b.set(0, -1, 1, Blocks.MAGMA_BLOCK);
        b.set(0, 0, 1, Kit.campfire(Blocks.CAMPFIRE, true));
        for (int z = -5; z <= -2; z++) {
            b.set(-3, 0, z, Kit.stairs(Blocks.CRIMSON_STAIRS, Direction.WEST));
            b.set(3, 0, z, Kit.stairs(Blocks.CRIMSON_STAIRS, Direction.EAST));
        }
        b.fill(-1, 0, -4, 1, 0, -4, Blocks.CARPET.red().defaultBlockState());
        b.fill(-1, 0, -5, 1, 0, -5, Blocks.CARPET.black().defaultBlockState());
        b.set(-3, 0, 6, Kit.facingAny(Blocks.BARREL, Direction.UP));
        b.set(-3, 1, 6, Kit.facingAny(Blocks.BARREL, Direction.UP));
        b.barrel(3, 0, 6, Direction.UP, Loot.HOLD_HOUSE);
        b.chest(0, 0, 6, Direction.NORTH, Loot.HOLD_HOUSE);
        b.set(-1, 0, 6, Blocks.POLISHED_BLACKSTONE);
        b.set(-1, 1, 6, Blocks.GOLD_BLOCK);
        b.set(1, 0, 6, Kit.stairs(Blocks.CRIMSON_STAIRS, Direction.SOUTH));
        b.set(-3, 0, 3, Blocks.CRIMSON_FENCE);
        b.set(-3, 1, 3, Blocks.CRIMSON_PRESSURE_PLATE);
        piglin(b, -2, 0, -1, false);
        piglin(b, 2, 0, 3, false);
        if (b.random.nextBoolean()) piglin(b, 1, 0, -3, true);
    }

    /** A small crimson hut, 7 by 7, with a hipped slab roof. */
    static void hut(Builder b) {
        int h = 3;
        groundwork(b, h, h, 7, PLANKS);
        b.walls(-h, 0, -h, h, 0, h, BRICKS);
        b.walls(-h, 1, -h, h, 3, h, PLANKS);
        for (int[] p : new int[][]{{-h, -h}, {h, -h}, {-h, h}, {h, h}}) b.fill(p[0], 0, p[1], p[0], 3, p[1], POST);
        for (int x = -h - 1; x <= h + 1; x++) {
            for (int z = -h - 1; z <= h + 1; z++) {
                int ring = Math.max(Math.abs(x), Math.abs(z));
                int y = 4 + (h + 1 - ring) / 2;
                BlockState s = (h + 1 - ring) % 2 == 0 ? Kit.slab(Blocks.CRIMSON_SLAB) : Kit.slabTop(Blocks.CRIMSON_SLAB);
                b.set(x, y, z, s);
                if (ring <= h) b.set(x, 4, z, ring == h ? PLANKS : Blocks.AIR.defaultBlockState());
            }
        }
        b.set(0, 6, 0, Blocks.SHROOMLIGHT);
        b.set(0, 5, 0, Blocks.AIR);
        Kit.door(b, 0, 0, -h, Blocks.CRIMSON_DOOR, Direction.NORTH);
        b.set(-h, 2, 0, Blocks.CRIMSON_FENCE);
        b.set(h, 2, 0, Blocks.CRIMSON_FENCE);
        b.chest(-2, 0, 2, Direction.EAST, Loot.HOLD_HOUSE);
        b.set(2, 0, 2, Kit.facingAny(Blocks.BARREL, Direction.UP));
        b.set(2, 0, -2, Kit.campfire(Blocks.CAMPFIRE, false));
        Kit.hangingLantern(b, 0, 4, 0, 1, Kit.lantern(true));
        piglin(b, 0, 0, 1, false);
        if (b.random.nextInt(3) == 0) piglin(b, -1, 0, -1, true);
    }

    /** The Hoard: a squat vault of blackstone and gold, guarded by a Brute. */
    static void hoard(Builder b) {
        int h = 4;
        groundwork(b, h, h, 9, BRICKS);
        for (int y = 0; y <= 6; y++) {
            for (int x = -h; x <= h; x++) {
                for (int z = -h; z <= h; z++) {
                    if (Math.abs(x) == h || Math.abs(z) == h) b.set(x, y, z, stone(b, x, y, z));
                }
            }
        }
        // buttresses and crenellations
        for (int[] p : new int[][]{{-h, -h}, {h, -h}, {-h, h}, {h, h}}) {
            b.fill(p[0], 0, p[1], p[0], 7, p[1], Blocks.POLISHED_BLACKSTONE.defaultBlockState());
        }
        b.fill(-h + 1, 7, -h + 1, h - 1, 7, h - 1, BRICKS);
        for (int x = -h; x <= h; x++) {
            for (int z = -h; z <= h; z++) {
                if ((Math.abs(x) == h || Math.abs(z) == h) && (x + z) % 2 == 0) b.set(x, 8, z, Blocks.POLISHED_BLACKSTONE_WALL);
            }
        }
        b.fill(-h + 1, 0, -h + 1, h - 1, 6, h - 1, Blocks.AIR.defaultBlockState());
        b.fill(-h + 1, 6, -h + 1, h - 1, 6, h - 1, Blocks.POLISHED_BLACKSTONE.defaultBlockState());
        // the gate: a tall opening with chains hanging in it
        b.fill(-1, 0, -h, 1, 3, -h, Blocks.AIR.defaultBlockState());
        b.fill(-1, 4, -h, 1, 4, -h, Blocks.GILDED_BLACKSTONE.defaultBlockState());
        b.set(-1, 3, -h, Kit.chainY());
        b.set(1, 3, -h, Kit.chainY());
        // inside: the gold
        b.fill(-3, 0, 2, -2, 1, 3, Blocks.GOLD_BLOCK.defaultBlockState());
        b.set(-3, 2, 3, Blocks.GOLD_BLOCK);
        b.fill(2, 0, 2, 3, 0, 3, Blocks.RAW_GOLD_BLOCK.defaultBlockState());
        b.set(3, 1, 3, Blocks.GOLD_BLOCK);
        b.chest(0, 0, 3, Direction.NORTH, Loot.HOLD_HOARD);
        b.chest(-3, 0, -1, Direction.EAST, Loot.HOLD_HOARD);
        b.set(3, 0, -1, Blocks.GILDED_BLACKSTONE);
        b.set(3, 1, -1, Kit.lantern(false));
        Kit.hangingLantern(b, 0, 5, 0, 1, Kit.lantern(true));
        brute(b, 0, 0, -1);
    }

    /** The horn tower: a blackstone tower with a crimson lookout and the Hold's war bell. */
    static void tower(Builder b) {
        int h = 2, top = 13;
        groundwork(b, h, h, top + 4, BRICKS);
        for (int y = 0; y < top; y++) {
            for (int x = -h; x <= h; x++) {
                for (int z = -h; z <= h; z++) {
                    if (Math.abs(x) == h || Math.abs(z) == h) b.set(x, y, z, stone(b, x, y, z));
                }
            }
        }
        b.fill(-1, 0, -1, 1, top - 1, 1, Blocks.AIR.defaultBlockState());
        for (int y = 0; y < top; y++) b.set(0, y, 1, Kit.facing(Blocks.LADDER, Direction.NORTH));
        Kit.door(b, 0, 0, -h, Blocks.CRIMSON_DOOR, Direction.NORTH);
        for (int y : new int[]{4, 8}) {
            b.set(-h, y, 0, Blocks.CRIMSON_FENCE);
            b.set(h, y, 0, Blocks.CRIMSON_FENCE);
        }
        // the lookout: a crimson deck on corbels, a fence rail, a roof on four posts
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                b.set(x, top, z, PLANKS);
                if (Math.abs(x) == 3 || Math.abs(z) == 3) b.set(x, top + 1, z, Blocks.CRIMSON_FENCE);
            }
        }
        b.set(0, top, 1, Kit.trapdoor(Blocks.CRIMSON_TRAPDOOR, Direction.SOUTH, true, false));
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int i = -1; i <= 1; i++) {
                int x = d.getStepX() * 3 + (d.getAxis() == Direction.Axis.Z ? i : 0);
                int z = d.getStepZ() * 3 + (d.getAxis() == Direction.Axis.X ? i : 0);
                b.set(x, top - 1, z, Kit.stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, d.getOpposite()));
            }
        }
        for (int[] p : new int[][]{{-3, -3}, {3, -3}, {-3, 3}, {3, 3}}) b.fill(p[0], top + 1, p[1], p[0], top + 3, p[1], POST);
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                int ring = Math.max(Math.abs(x), Math.abs(z));
                b.set(x, top + 4 + (4 - ring) / 2, z, (4 - ring) % 2 == 0 ? Kit.slab(Blocks.CRIMSON_SLAB) : Kit.slabTop(Blocks.CRIMSON_SLAB));
            }
        }
        b.set(0, top + 5, 0, Blocks.GOLD_BLOCK);
        b.set(0, top + 4, 0, Blocks.CRIMSON_FENCE);
        b.set(0, top + 3, 0, Kit.facing(Blocks.BELL, Direction.NORTH).setValue(net.minecraft.world.level.block.BellBlock.ATTACHMENT,
                net.minecraft.world.level.block.state.properties.BellAttachType.CEILING));
        Kit.hangingLantern(b, 2, top + 3, 2, 1, Kit.lantern(true));
        brute(b, -1.5, top + 1, -1.5);
    }

    /** The forge: an open-fronted blackstone smithy with a lava trough. */
    static void forge(Builder b) {
        int h = 3;
        groundwork(b, h, h, 7, BLACKSTONE);
        b.walls(-h, 0, -h + 1, h, 3, h, Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
        b.fill(-h + 1, 0, -h + 1, h - 1, 3, h - 1, Blocks.AIR.defaultBlockState());
        b.fill(-h, 0, -h, h, 3, -h, Blocks.AIR.defaultBlockState());
        for (int x : new int[]{-h, h}) b.fill(x, 0, -h, x, 3, -h, POST);
        b.fill(-h, 4, -h, h, 4, h, Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
        b.fill(-h, 4, -h, h, 4, -h, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.SOUTH));
        // chimney over the trough
        b.fill(2, 4, 2, 2, 7, 2, BRICKS);
        b.set(2, 8, 2, Kit.campfire(Blocks.CAMPFIRE, false));
        b.set(2, -1, 2, Blocks.MAGMA_BLOCK);
        b.set(2, 0, 2, Blocks.LAVA_CAULDRON);
        b.set(1, 0, 2, Kit.facing(Blocks.BLAST_FURNACE, Direction.NORTH));
        b.set(-2, 0, 2, Blocks.SMITHING_TABLE);
        b.set(-2, 0, 0, Kit.facing(Blocks.ANVIL, Direction.EAST));
        b.chest(-1, 0, 2, Direction.NORTH, Loot.HOLD_FORGE);
        b.set(2, 0, -1, Blocks.GOLD_BLOCK);
        Kit.hangingLantern(b, 0, 3, 0, 1, Kit.lantern(true));
        piglin(b, 0, 0, 0, false);
    }

    /** A hoglin pen: a crimson fence round a patch of nylium and fungus, with penned hoglins the Piglins won't hunt. */
    static void pen(Builder b) {
        int hx = 5, hz = 4;
        b.clear(-hx, 0, -hz, hx, 3, hz);
        b.foundation(-hx, -hz, hx, hz, -1, Blocks.NETHERRACK.defaultBlockState(), 8);
        for (int x = -hx; x <= hx; x++) {
            for (int z = -hz; z <= hz; z++) {
                b.set(x, -1, z, b.chance(x, -1, z, 0.25F) ? Blocks.NETHER_WART_BLOCK.defaultBlockState() : Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean edge = Math.abs(x) == hx || Math.abs(z) == hz;
                if (edge) b.set(x, 0, z, Blocks.CRIMSON_FENCE);
                else if (b.chance(x, 0, z, 0.12F)) b.set(x, 0, z, b.pick(x, 1, z, Blocks.CRIMSON_FUNGUS, Blocks.CRIMSON_ROOTS));
            }
        }
        b.set(0, 0, -hz, Kit.gate(Blocks.CRIMSON_FENCE_GATE, Direction.NORTH, false));
        for (int[] p : new int[][]{{-hx, -hz}, {hx, -hz}, {-hx, hz}, {hx, hz}}) b.set(p[0], 1, p[1], Kit.lantern(false));
        b.set(hx - 1, 0, hz - 1, Blocks.COMPOSTER);
        // the herder waits by the gate
        piglin(b, 2, 0, -hz - 1, false);
        int n = 2 + b.random.nextInt(2);
        for (int i = 0; i < n; i++) {
            boolean young = i == n - 1 && b.chance(i, 9, 9, 0.5F);
            b.spawn(EntityTypes.HOGLIN, -3 + i * 3, 0, 1, hoglin -> {
                ((dev.netherexpansion.mixin.HoglinAccess) hoglin).netherexpansion$setCannotBeHunted(true);
                if (young) hoglin.setBaby(true);
            });
        }
    }
}
