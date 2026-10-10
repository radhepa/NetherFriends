package dev.netherexpansion.world.village;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.netherexpansion.entity.People;
import dev.netherexpansion.entity.NetherEntities;
import dev.netherexpansion.entity.Withren;
import dev.netherexpansion.world.Builder;
import dev.netherexpansion.world.BuildingPiece;
import dev.netherexpansion.world.Kit;
import dev.netherexpansion.world.Loot;
import dev.netherexpansion.world.Names;
import dev.netherexpansion.world.NetherStructures;
import dev.netherexpansion.world.NetherVillage;
import dev.netherexpansion.world.Plan;
import dev.netherexpansion.world.Terrain;
import dev.netherexpansion.world.Ways;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jspecify.annotations.Nullable;

/**
 * A Withren Vigil, in the Soul Sand Valleys: halls of basalt and bone along soul-lantern lanes. At its heart the
 * Hall of the Three, where three empty plinths remember the three who gave themselves for the Last Answer.
 * Around it homes, the Name Wall (every Withren who ever died, by name), wart terraces, a brewery, the
 * Remembrancer's tower and the Ashwardens' posts.
 */
public class VigilVillage extends NetherVillage {
    public static final MapCodec<VigilVillage> CODEC = simpleCodec(VigilVillage::new);

    public VigilVillage(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return NetherStructures.VIGIL;
    }

    @Override
    public String kind() {
        return "vigil";
    }

    @Override
    public People people() {
        return People.WITHREN;
    }

    @Override
    protected @Nullable BlockPos site(Terrain t, ChunkPos chunk, GenerationContext ctx) {
        for (int[] o : new int[][]{{0, 0}, {16, 0}, {-16, 0}, {0, 16}, {0, -16}}) {
            int x = chunk.getMiddleBlockX() + o[0], z = chunk.getMiddleBlockZ() + o[1];
            int y = t.lowestFloor(x, z, 14);
            if (y == Terrain.NONE || y > 96) continue;
            BlockPos c = new BlockPos(x, y, z);
            int near = countRing(c, 14, 12, (px, pz) -> t.floorNear(px, pz, y, 6, 5) != Terrain.NONE);
            int far = countRing(c, 26, 12, (px, pz) -> t.floorNear(px, pz, y, 8, 5) != Terrain.NONE);
            if (near >= 8 && far >= 6 && levelCore(t, c, 8, 2)) return c;
        }
        return null;
    }

    record Wanted(String kind, int hx, int hz, int height, double rMin, double rMax) {}

    @Override
    protected void layout(Plan plan) {
        BlockPos c = plan.centre;
        plan.take(c.getX() - 9, c.getZ() - 9, c.getX() + 9, c.getZ() + 9);
        List<Wanted> wanted = new ArrayList<>(List.of(
                new Wanted("vigil/name_wall", 7, 1, 6, 13, 18),
                new Wanted("vigil/remembrancer", 3, 3, 13, 15, 26),
                new Wanted("vigil/home", 3, 4, 9, 14, 30),
                new Wanted("vigil/home", 3, 4, 9, 14, 30),
                new Wanted("vigil/home", 3, 4, 9, 14, 32),
                new Wanted("vigil/home", 3, 4, 9, 16, 34),
                new Wanted("vigil/brewery", 3, 3, 8, 14, 30),
                new Wanted("vigil/terrace", 4, 4, 4, 18, 34),
                new Wanted("vigil/post", 2, 2, 10, 24, 36),
                new Wanted("vigil/post", 2, 2, 10, 24, 36)));
        if (plan.random.nextBoolean()) wanted.add(new Wanted("vigil/home", 3, 4, 9, 18, 36));
        if (plan.random.nextBoolean()) wanted.add(new Wanted("vigil/terrace", 4, 4, 4, 20, 36));

        List<Plan.Lot> lots = new ArrayList<>();
        List<Wanted> kinds = new ArrayList<>();
        for (Wanted w : wanted) {
            Plan.Lot lot = plan.ringLot(w.rMin, w.rMax, w.hx, w.hz, 2, true, Plan.floorGround(7, Math.min(w.height, 8), 7, w.hx, w.hz), 40);
            if (lot != null) {
                lots.add(lot);
                kinds.add(w);
            }
        }
        for (Plan.Lot lot : lots) Ways.path(plan, Ways.VIGIL, 3, lot.door(), c);
        plan.build("vigil/hall", c, Rotation.NONE, -9, -12, -9, 9, 14, 9);
        for (int i = 0; i < lots.size(); i++) plan.build(kinds.get(i).kind, lots.get(i), 12, kinds.get(i).height + 2);
    }

    // -- the buildings ------------------------------------------------------------------------------------------

    static final BlockState SMOOTH = Blocks.SMOOTH_BASALT.defaultBlockState();
    static final BlockState PILLAR = Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Y);
    static final BlockState BONE = Kit.axis(Blocks.BONE_BLOCK, Direction.Axis.Y);
    static final BlockState BONE_X = Kit.axis(Blocks.BONE_BLOCK, Direction.Axis.X);
    static final BlockState BRICK = Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
    static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.defaultBlockState();

    public static void register() {
        BuildingPiece.kind("vigil/hall", VigilVillage::hall);
        BuildingPiece.kind("vigil/home", VigilVillage::home);
        BuildingPiece.kind("vigil/name_wall", VigilVillage::nameWall);
        BuildingPiece.kind("vigil/remembrancer", VigilVillage::remembrancer);
        BuildingPiece.kind("vigil/brewery", VigilVillage::brewery);
        BuildingPiece.kind("vigil/terrace", VigilVillage::terrace);
        BuildingPiece.kind("vigil/post", VigilVillage::post);
    }

    static void withren(Builder b, double x, double y, double z, int role, boolean child) {
        b.spawn(NetherEntities.WITHREN, x, y, z, (Withren w) -> {
            w.setRole(role);
            if (child) w.setBaby(true);
        });
    }

    static void groundwork(Builder b, int hx, int hz, int height, BlockState floor) {
        b.clear(-hx - 1, 0, -hz - 1, hx + 1, height + 5, hz + 1);
        b.foundation(-hx, -hz, hx, hz, -1, Blocks.BLACKSTONE.defaultBlockState(), 14);
        b.fill(-hx, -1, -hz, hx, -1, hz, floor);
    }

    /** A gabled roof of blackstone-brick stairs along z over a box hx wide, starting at y. */
    static void gable(Builder b, int hx, int hz, int y) {
        for (int k = 0; k <= hx + 1; k++) {
            int x = hx + 1 - k;
            for (int z = -hz - 1; z <= hz + 1; z++) {
                if (x == 0) {
                    b.set(0, y + k, z, Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
                } else {
                    b.set(x, y + k, z, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.WEST));
                    b.set(-x, y + k, z, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Direction.EAST));
                }
            }
            for (int xx = -(hx - k); xx <= hx - k; xx++) {
                b.set(xx, y + k, -hz, SMOOTH);
                b.set(xx, y + k, hz, SMOOTH);
            }
        }
    }

    /** The Hall of the Three: a round hall of basalt and bone, three empty plinths and the soul fire of the Last Answer. */
    static void hall(Builder b) {
        int r = 7, wall = 6;
        b.clear(-r - 1, 0, -r - 1, r + 1, 13, r + 1);
        for (int x = -r - 1; x <= r + 1; x++) {
            for (int z = -r - 1; z <= r + 1; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r + 0.5) continue;
                b.foundation(x, -1, z, Blocks.BLACKSTONE.defaultBlockState(), 14);
                b.set(x, -1, z, d > r - 0.5 ? BRICK : d < 2.5 ? Blocks.SOUL_SOIL.defaultBlockState()
                        : b.chance(x, -1, z, 0.3F) ? Blocks.POLISHED_BLACKSTONE.defaultBlockState() : Blocks.SMOOTH_BASALT.defaultBlockState());
                if (d > r - 0.5) {
                    boolean column = (x + z) % 2 == 0;
                    for (int y = 0; y < wall; y++) b.set(x, y, z, column ? PILLAR : y == 0 || y == wall - 1 ? BONE : SMOOTH);
                }
            }
        }
        // four doorways, three wide and three high, with bone lintels
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int i = -1; i <= 1; i++) {
                for (int y = 0; y <= 2; y++) {
                    int x = d.getStepX() * r + (d.getAxis() == Direction.Axis.Z ? i : 0);
                    int z = d.getStepZ() * r + (d.getAxis() == Direction.Axis.X ? i : 0);
                    b.set(x, y, z, Blocks.AIR);
                    b.set(x + d.getStepX(), y, z + d.getStepZ(), Blocks.AIR);
                }
            }
        }
        // stepped dome of blackstone brick, open at the top over the fire
        for (int y = wall; y <= wall + 5; y++) {
            double rr = r - (y - wall) * 1.25;
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    double d = Math.sqrt(x * x + z * z);
                    if (d <= rr + 0.5 && d > rr - 0.9 && d > 1.2) b.set(x, y, z, y == wall ? BONE : BRICK);
                }
            }
        }
        // the soul fire and the three plinths
        b.set(0, -1, 0, Blocks.SOUL_SAND);
        b.set(0, 0, 0, Blocks.SOUL_FIRE);
        for (int i = 0; i < 3; i++) {
            double a = Math.PI * 2 * i / 3 + Math.PI / 2;
            int x = (int) Math.round(Math.cos(a) * 3.6), z = (int) Math.round(Math.sin(a) * 3.6);
            b.set(x, 0, z, BONE);
            b.set(x, 1, z, BONE);
            int rot = Math.floorMod(Math.round((float) (Math.atan2(-x, z) / (Math.PI * 2) * 16)) + 8, 16);
            b.set(x, 2, z, Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, rot));
            Direction toCentre = Plan.toward(x, z, 0, 0);
            int fx = x + toCentre.getStepX(), fz = z + toCentre.getStepZ();
            b.set(fx, 0, fz, Kit.campfire(Blocks.SOUL_CAMPFIRE, true));
            b.sign(fx, 1, fz, Blocks.PALE_OAK_WALL_SIGN.defaultBlockState(), toCentre, true, "", Names.THE_THREE[i], "gave themselves", "");
        }
        // benches round the wall, soul lanterns hung from the dome, the hall's chest and lectern
        for (int x = -r + 1; x <= r - 1; x++) {
            for (int z = -r + 1; z <= r - 1; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r - 1.5 && d <= r - 0.5 && Math.abs(x) > 1 && Math.abs(z) > 1 && b.chance(x, 0, z, 0.6F)) {
                    b.set(x, 0, z, Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Plan.toward(0, 0, x, z)));
                }
            }
        }
        for (int[] p : new int[][]{{-4, -4}, {4, -4}, {-4, 4}, {4, 4}}) Kit.hangingLantern(b, p[0], wall + 2, p[1], 3, Kit.soulLantern(true));
        b.chest(-5, 0, 2, Direction.EAST, Loot.VIGIL_HALL);
        b.set(5, 0, -2, Kit.facing(Blocks.LECTERN, Direction.WEST));
        withren(b, -2, 0, -4, Withren.REMEMBRANCER, false);
        withren(b, 3, 0, 4, Withren.VILLAGER, false);
        // the Hall's own Cairnbacks, keeping their fires beside the Three
        cairnback(b, -4, 0, 3, false);
        cairnback(b, 5, 0, 1, false);
    }

    /** A home: basalt walls on a bone plinth, polished basalt corners, a dark gable roof and a warped door. */
    static void home(Builder b) {
        int hx = 3, hz = 4;
        groundwork(b, hx, hz, 9, SOUL_SOIL);
        b.walls(-hx, 0, -hz, hx, 0, hz, BONE_X);
        b.walls(-hx, 1, -hz, hx, 3, hz, SMOOTH);
        b.walls(-hx, 4, -hz, hx, 4, hz, BONE_X);
        for (int[] p : new int[][]{{-hx, -hz}, {hx, -hz}, {-hx, hz}, {hx, hz}, {-hx, 0}, {hx, 0}}) b.fill(p[0], 0, p[1], p[0], 4, p[1], PILLAR);
        gable(b, hx, hz, 5);
        b.fill(-hx + 1, 5, -hz + 1, hx - 1, 5, hz - 1, Blocks.AIR.defaultBlockState());
        Kit.door(b, 0, 0, -hz, Blocks.WARPED_DOOR, Direction.NORTH);
        b.set(0, 2, -hz, SMOOTH);
        for (int z : new int[]{-2, 2}) {
            b.set(-hx, 2, z, Blocks.IRON_BARS);
            b.set(hx, 2, z, Blocks.IRON_BARS);
        }
        b.set(-2, 2, hz, Blocks.IRON_BARS);
        b.set(2, 2, hz, Blocks.IRON_BARS);
        Kit.lampPost(b, -2, 0, -hz - 1, 1, BONE, Kit.soulLantern(false));
        // inside: a soul-fire hearth, bedrolls, a shelf of names, the house's chest
        b.set(0, 0, 3, Kit.campfire(Blocks.SOUL_CAMPFIRE, true));
        b.fill(-2, 0, 1, -2, 0, 2, Blocks.CARPET.gray().defaultBlockState());
        b.fill(2, 0, 1, 2, 0, 2, Blocks.CARPET.cyan().defaultBlockState());
        b.set(-2, 0, 3, Blocks.BOOKSHELF);
        b.set(-2, 1, 3, Blocks.BOOKSHELF);
        b.chest(2, 0, 3, Direction.WEST, Loot.VIGIL_HOUSE);
        b.set(2, 0, -3, Blocks.POTTED_WITHER_ROSE);
        b.set(-2, 0, -3, Kit.facingAny(Blocks.BARREL, Direction.UP));
        Kit.hangingLantern(b, 0, 6, 0, 2, Kit.soulLantern(true));
        withren(b, 0, 0, 0, Withren.VILLAGER, false);
        if (b.random.nextInt(3) > 0) withren(b, 1, 0, 2, Withren.VILLAGER, false);
        if (b.random.nextInt(5) < 2) withren(b, -1, 0, -1, Withren.VILLAGER, true);
    }

    /** The Name Wall: the Withren dead its people keep, by name, on a long wall of blackstone and bone. */
    static void nameWall(Builder b) {
        int hx = 7;
        b.clear(-hx - 1, 0, -2, hx + 1, 6, 2);
        b.foundation(-hx, -1, hx, 1, -1, Blocks.BLACKSTONE.defaultBlockState(), 12);
        b.fill(-hx, -1, -1, hx, -1, 1, BRICK);
        b.fill(-hx, 0, 0, hx, 4, 0, BRICK);
        b.fill(-hx, 5, 0, hx, 5, 0, Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
        for (int x : new int[]{-hx, hx}) {
            b.fill(x, 0, 0, x, 5, 0, BONE);
            b.set(x, 6, 0, Kit.soulLantern(false));
        }
        for (int x = -hx + 1; x < hx; x++) {
            for (int y = 1; y <= 3; y++) {
                // the same names the Vigil's residents keep (Names.wallName), so a keeper can point theirs out
                b.sign(x, y, -1, Blocks.PALE_OAK_WALL_SIGN.defaultBlockState(), Direction.NORTH, false, "",
                        Names.wallName(b.village(), (x + hx - 1) * 3 + (y - 1)), "", "");
            }
            if (x % 3 == 0) b.set(x, 0, -1, Blocks.POTTED_WITHER_ROSE);
        }
    }

    /** The Remembrancer's tower: two floors of books under a steep roof. */
    static void remembrancer(Builder b) {
        int h = 3;
        groundwork(b, h, h, 13, SOUL_SOIL);
        b.walls(-h, 0, -h, h, 8, h, SMOOTH);
        for (int[] p : new int[][]{{-h, -h}, {h, -h}, {-h, h}, {h, h}}) b.fill(p[0], 0, p[1], p[0], 8, p[1], PILLAR);
        b.walls(-h, 4, -h, h, 4, h, BONE_X);
        b.fill(-h + 1, 4, -h + 1, h - 1, 4, h - 1, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.defaultBlockState()
                .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.TOP));
        b.set(2, 4, 2, Blocks.AIR);
        for (int y = 0; y <= 4; y++) b.set(2, y, 2, Kit.facing(Blocks.LADDER, Direction.NORTH));
        for (int k = 0; k <= h + 1; k++) {
            int ring = h + 1 - k;
            for (int x = -ring; x <= ring; x++) {
                for (int z = -ring; z <= ring; z++) {
                    if (Math.max(Math.abs(x), Math.abs(z)) == ring) b.set(x, 8 + k, z, BRICK);
                }
            }
        }
        b.set(0, 13, 0, BONE);
        b.set(0, 14, 0, Kit.soulLantern(false));
        Kit.door(b, 0, 0, -h, Blocks.WARPED_DOOR, Direction.NORTH);
        for (int y : new int[]{2, 6}) {
            b.set(-h, y, 0, Blocks.IRON_BARS);
            b.set(h, y, 0, Blocks.IRON_BARS);
            b.set(0, y, h, Blocks.IRON_BARS);
        }
        for (int z = -2; z <= 2; z++) {
            b.set(-2, 0, z, Blocks.BOOKSHELF);
            b.set(-2, 1, z, Blocks.BOOKSHELF);
            b.set(-2, 5, z, Blocks.CHISELED_BOOKSHELF.defaultBlockState().setValue(net.minecraft.world.level.block.ChiseledBookShelfBlock.FACING, Direction.EAST));
        }
        b.set(1, 0, 0, Kit.facing(Blocks.LECTERN, Direction.WEST));
        b.set(1, 5, -1, Kit.facing(Blocks.LECTERN, Direction.WEST));
        b.chest(2, 5, 2, Direction.WEST, Loot.VIGIL_HALL);
        Kit.hangingLantern(b, 0, 3, 0, 1, Kit.soulLantern(true));
        Kit.hangingLantern(b, 0, 8, 0, 1, Kit.soulLantern(true));
        withren(b, 0, 0, -1, Withren.REMEMBRANCER, false);
    }

    /** The brewery: a squat hut of brewing stands and cauldrons, smoke from a soul-fire chimney. */
    static void brewery(Builder b) {
        int h = 3;
        groundwork(b, h, h, 8, Blocks.POLISHED_BLACKSTONE.defaultBlockState());
        b.walls(-h, 0, -h, h, 3, h, SMOOTH);
        for (int[] p : new int[][]{{-h, -h}, {h, -h}, {-h, h}, {h, h}}) b.fill(p[0], 0, p[1], p[0], 3, p[1], PILLAR);
        b.fill(-h, 4, -h, h, 4, h, Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
        for (int x = -h; x <= h; x++) {
            b.set(x, 4, -h, BONE_X);
            b.set(x, 4, h, BONE_X);
        }
        b.fill(2, 4, 2, 2, 6, 2, BRICK);
        b.set(2, 7, 2, Kit.campfire(Blocks.SOUL_CAMPFIRE, true));
        b.fill(-1, 0, -h, 1, 2, -h, Blocks.AIR.defaultBlockState());
        b.set(-2, 0, 2, Blocks.BREWING_STAND);
        b.set(0, 0, 2, Blocks.BREWING_STAND);
        b.set(1, 0, 2, Blocks.WATER_CAULDRON.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
        b.set(2, 0, 2, Blocks.MAGMA_BLOCK);
        b.chest(-2, 0, 0, Direction.EAST, Loot.VIGIL_BREWERY);
        b.set(2, 0, -1, Kit.facingAny(Blocks.BARREL, Direction.UP));
        b.set(2, 1, -1, Blocks.BREWING_STAND);
        Kit.hangingLantern(b, 0, 3, 0, 1, Kit.soulLantern(true));
        withren(b, 0, 0, 0, Withren.VILLAGER, false);
    }

    /** Wart terraces: two tiers of soul sand beds full of nether wart, edged with bone. */
    static void terrace(Builder b) {
        int h = 4;
        b.clear(-h, 0, -h, h, 4, h);
        b.foundation(-h, -h, h, h, -1, Blocks.SOUL_SOIL.defaultBlockState(), 10);
        BlockState wart = Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3);
        for (int x = -h; x <= h; x++) {
            for (int z = -h; z <= h; z++) {
                boolean edge = Math.abs(x) == h || Math.abs(z) == h;
                boolean upper = Math.abs(x) <= 2 && Math.abs(z) <= 2;
                int top = upper ? 0 : -1;
                if (upper) b.set(x, -1, z, Blocks.SOUL_SOIL);
                if (edge) {
                    b.set(x, top, z, BONE_X);
                } else if (upper && (Math.abs(x) == 2 || Math.abs(z) == 2)) {
                    b.set(x, top, z, Kit.slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB));
                } else {
                    b.set(x, top, z, Blocks.SOUL_SAND);
                    b.set(x, top + 1, z, x % 2 == 0 && !b.chance(x, top, z, 0.15F) ? wart : Blocks.AIR.defaultBlockState());
                }
            }
        }
        b.set(0, 0, 0, Blocks.SOUL_SOIL);
        Kit.lampPost(b, 0, 1, 0, 2, BONE, Kit.soulLantern(false));
        if (b.random.nextBoolean()) withren(b, -3, 0, -3, Withren.VILLAGER, true);
        // a Cairnback grazing the lower ring, sometimes with its young
        cairnback(b, 3, 0, 0, false);
        if (b.random.nextBoolean()) cairnback(b, -3, 0, 1, true);
    }

    /** One of the Vigil's Cairnbacks, the tortoises the Withren walk in their processions. */
    static void cairnback(Builder b, double x, double y, double z, boolean young) {
        b.spawn(NetherEntities.CAIRNBACK, x, y, z, c -> {
            if (young) c.setAge(-24000);
        });
    }

    /** An Ashwardens' post: a small bone-and-basalt watchtower with a warden at its foot. */
    static void post(Builder b) {
        int h = 2, top = 7;
        groundwork(b, h, h, 10, Blocks.POLISHED_BLACKSTONE.defaultBlockState());
        for (int y = 0; y < top; y++) {
            for (int x = -h; x <= h; x++) {
                for (int z = -h; z <= h; z++) {
                    if (Math.abs(x) == h || Math.abs(z) == h) {
                        boolean corner = Math.abs(x) == h && Math.abs(z) == h;
                        b.set(x, y, z, corner ? PILLAR : y % 3 == 2 && (x == 0 || z == 0) ? Blocks.IRON_BARS.defaultBlockState() : BONE);
                    }
                }
            }
        }
        b.fill(-1, 0, -1, 1, top - 1, 1, Blocks.AIR.defaultBlockState());
        for (int y = 0; y < top; y++) b.set(0, y, 1, Kit.facing(Blocks.LADDER, Direction.NORTH));
        b.fill(0, 0, -h, 0, 1, -h, Blocks.AIR.defaultBlockState());
        b.fill(-h - 1, top, -h - 1, h + 1, top, h + 1, BRICK);
        b.set(0, top, 1, Kit.trapdoor(Blocks.WARPED_TRAPDOOR, Direction.SOUTH, true, false));
        for (int x = -h - 1; x <= h + 1; x++) {
            for (int z = -h - 1; z <= h + 1; z++) {
                if (Math.abs(x) == h + 1 || Math.abs(z) == h + 1) b.set(x, top + 1, z, Blocks.NETHER_BRICK_FENCE);
            }
        }
        b.set(-h - 1, top + 2, -h - 1, Kit.soulLantern(false));
        b.set(h + 1, top + 2, h + 1, Kit.soulLantern(false));
        withren(b, 1, 0, -h - 1, Withren.ASHWARDEN, false);
    }
}
