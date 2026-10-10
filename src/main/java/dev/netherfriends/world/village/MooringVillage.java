package dev.netherfriends.world.village;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.netherfriends.entity.People;
import dev.netherfriends.entity.Magmari;
import dev.netherfriends.entity.NetherEntities;
import dev.netherfriends.world.Builder;
import dev.netherfriends.world.BuildingPiece;
import dev.netherfriends.world.Kit;
import dev.netherfriends.world.Loot;
import dev.netherfriends.world.NetherStructures;
import dev.netherfriends.world.NetherVillage;
import dev.netherfriends.world.Plan;
import dev.netherfriends.world.Terrain;
import dev.netherfriends.world.Ways;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jspecify.annotations.Nullable;

/**
 * A Magmari Mooring, out on the open lava sea: round platforms on basalt pillars that stand on the sea floor,
 * joined to a central hearth by chain bridges. Domed basalt huts with magma seams, a fenced strider pen of open
 * lava, a ferry dock, and when there's land in reach a causeway to the shore. The Magmari wade between
 * platforms through the lava itself.
 */
public class MooringVillage extends NetherVillage {
    public static final MapCodec<MooringVillage> CODEC = simpleCodec(MooringVillage::new);
    /** The deck is two above the lava's surface: people walk at sea level + 2. */
    static final int DECK = 2;
    /** Pillars go down through the lava to the sea floor, at most this far. */
    static final int PILLAR_DEPTH = 40;

    public MooringVillage(StructureSettings settings) {
        super(settings);
    }

    @Override
    public StructureType<?> type() {
        return NetherStructures.MOORING;
    }

    @Override
    public String kind() {
        return "mooring";
    }

    @Override
    public People people() {
        return People.MAGMARI;
    }

    @Override
    protected int homeRadius() {
        return 40;
    }

    @Override
    protected @Nullable BlockPos site(Terrain t, ChunkPos chunk, GenerationContext ctx) {
        for (int[] o : new int[][]{{0, 0}, {24, 0}, {-24, 0}, {0, 24}, {0, -24}, {24, 24}, {-24, -24}, {24, -24}, {-24, 24}}) {
            int x = chunk.getMiddleBlockX() + o[0], z = chunk.getMiddleBlockZ() + o[1];
            if (!t.openLava(x, z, 8)) continue;
            BlockPos c = new BlockPos(x, t.seaLevel + DECK, z);
            int near = countRing(c, 12, 12, (px, pz) -> t.openLava(px, pz, 6));
            int far = countRing(c, 22, 16, (px, pz) -> t.openLava(px, pz, 6));
            if (near >= 11 && far >= 12) return c;
        }
        return null;
    }

    @Override
    protected void layout(Plan plan) {
        BlockPos c = plan.centre;
        Terrain t = plan.t;
        plan.take(c.getX() - 8, c.getZ() - 8, c.getX() + 8, c.getZ() + 8);
        Plan.Ground lava = (p, x, z) -> {
            for (int[] d : new int[][]{{0, 0}, {-4, -4}, {4, -4}, {-4, 4}, {4, 4}}) {
                if (!t.openLava(x + d[0], z + d[1], 6)) return null;
            }
            return c.getY();
        };
        // homes on a ring, spaced round the hearth
        List<Plan.Lot> homes = new ArrayList<>();
        int wantHomes = 4 + plan.random.nextInt(2);
        double start = plan.random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < wantHomes; i++) {
            double a = start + Math.PI * 2 * i / (wantHomes + 1);
            Plan.Lot lot = null;
            for (int tries = 0; tries < 8 && lot == null; tries++) {
                double r = 15 + plan.random.nextDouble() * 6, aa = a + (plan.random.nextDouble() - 0.5) * 0.35;
                int x = c.getX() + (int) Math.round(Math.cos(aa) * r), z = c.getZ() + (int) Math.round(Math.sin(aa) * r);
                Rotation rot = Plan.facing(Plan.toward(x, z, c.getX(), c.getZ()));
                Plan.Lot probe = new Plan.Lot(new BlockPos(x, c.getY(), z), rot, 4, 4);
                if (plan.free(probe, 2) && lava.floorAt(plan, x, z) != null) {
                    lot = probe;
                    plan.take(lot, 2);
                }
            }
            if (lot != null) homes.add(lot);
        }
        // the strider pen sits in the gap the homes left
        double penAngle = start + Math.PI * 2 * wantHomes / (wantHomes + 1);
        BlockPos pen = null;
        for (int tries = 0; tries < 8 && pen == null; tries++) {
            double r = 15 + plan.random.nextDouble() * 4;
            int x = c.getX() + (int) Math.round(Math.cos(penAngle) * r), z = c.getZ() + (int) Math.round(Math.sin(penAngle) * r);
            if (plan.free(x - 6, z - 6, x + 6, z + 6) && lava.floorAt(plan, x, z) != null) {
                pen = new BlockPos(x, c.getY(), z);
                plan.take(x - 6, z - 6, x + 6, z + 6);
            }
        }
        // the dock runs straight out from the hearth on the side away from the pen
        Direction dockDir = pen == null ? Direction.Plane.HORIZONTAL.getRandomDirection(plan.random)
                : Plan.toward(pen.getX(), pen.getZ(), c.getX(), c.getZ());
        // a causeway to the nearest shore, if there's land within reach
        BlockPos shore = this.findShore(t, c);

        // ways first: bridges from the hearth to every platform, the dock, the causeway
        for (Plan.Lot home : homes) Ways.bridge(plan, Ways.MOORING, 3, c, home.pos(), PILLAR_DEPTH);
        if (pen != null) Ways.bridge(plan, Ways.MOORING, 3, c, pen, PILLAR_DEPTH);
        BlockPos dockEnd = c.relative(dockDir, 22);
        Ways.bridge(plan, Ways.MOORING, 3, c, dockEnd, PILLAR_DEPTH);
        if (shore != null) {
            Plan.Lot from = homes.isEmpty() ? null : homes.stream().min((a, b) -> Double.compare(a.pos().distSqr(shore), b.pos().distSqr(shore))).orElse(null);
            BlockPos start2 = from == null || c.distSqr(shore) < from.pos().distSqr(shore) ? c : from.pos();
            Ways.bridge(plan, Ways.MOORING, 3, start2, new BlockPos(shore.getX(), c.getY(), shore.getZ()), PILLAR_DEPTH);
        }

        plan.build("mooring/hearth", c, Rotation.NONE, -8, -PILLAR_DEPTH, -8, 8, 8, 8);
        for (Plan.Lot home : homes) plan.build("mooring/home", home, PILLAR_DEPTH, 9);
        if (pen != null) plan.build("mooring/pen", pen, Rotation.NONE, -6, -PILLAR_DEPTH, -6, 6, 4, 6);
        plan.build("mooring/dock", dockEnd, Plan.facing(dockDir.getOpposite()), -5, -PILLAR_DEPTH, -3, 5, 6, 3);
    }

    /** The nearest land at about deck height within 64 blocks along the eight compass lines, or null. */
    @Nullable BlockPos findShore(Terrain t, BlockPos c) {
        BlockPos best = null;
        for (int i = 0; i < 8; i++) {
            double a = Math.PI * 2 * i / 8;
            for (int r = 26; r <= 64; r += 2) {
                int x = c.getX() + (int) Math.round(Math.cos(a) * r), z = c.getZ() + (int) Math.round(Math.sin(a) * r);
                int y = t.floorNear(x, z, c.getY(), 3, 3);
                if (y != Terrain.NONE && y > t.seaLevel) {
                    if (best == null || c.distSqr(new BlockPos(x, c.getY(), z)) < c.distSqr(best)) best = new BlockPos(x, y, z);
                    break;
                }
            }
        }
        return best;
    }

    // -- the platforms ------------------------------------------------------------------------------------------

    static final BlockState BASALT = Kit.axis(Blocks.BASALT, Direction.Axis.Y);
    static final BlockState POLISHED = Kit.axis(Blocks.POLISHED_BASALT, Direction.Axis.Y);
    static final BlockState STONE = Blocks.POLISHED_BLACKSTONE.defaultBlockState();
    static final BlockState BRICKS = Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();

    public static void register() {
        BuildingPiece.kind("mooring/hearth", MooringVillage::hearth);
        BuildingPiece.kind("mooring/home", MooringVillage::home);
        BuildingPiece.kind("mooring/pen", MooringVillage::pen);
        BuildingPiece.kind("mooring/dock", MooringVillage::dock);
    }

    static void magmari(Builder b, double x, double y, double z, int role, boolean child) {
        b.spawn(NetherEntities.MAGMARI, x, y, z, (Magmari m) -> {
            m.setRole(role);
            if (child) m.setBaby(true);
        });
    }

    /** A round deck at walking height y = 0 (deck blocks at -1), standing on basalt pillars in the lava. */
    static void deck(Builder b, double r) {
        int ri = (int) Math.ceil(r);
        b.clear(-ri, 0, -ri, ri, 6, ri);
        for (int x = -ri; x <= ri; x++) {
            for (int z = -ri; z <= ri; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r) continue;
                boolean rim = d > r - 1;
                b.set(x, -1, z, rim ? BRICKS : b.chance(x, -1, z, 0.35F) ? Kit.axis(Blocks.BASALT, Direction.Axis.Y) : STONE);
                // pillars: a ring of them under the rim and one under the middle
                boolean pillar = (rim && (Math.abs(x) + Math.abs(z)) % 4 == 0) || (x == 0 && z == 0);
                if (pillar) b.foundation(x, -1, z, BASALT, PILLAR_DEPTH);
                else if (rim) b.set(x, -2, z, Kit.stairsTop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Plan.toward(x, z, 0, 0)));
            }
        }
    }

    /** A railing of nether-brick fence round the deck's edge, with openings toward the four compass points. */
    static void rail(Builder b, double r) {
        int ri = (int) Math.ceil(r);
        for (int x = -ri; x <= ri; x++) {
            for (int z = -ri; z <= ri; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r || d <= r - 1) continue;
                if (Math.abs(x) <= 1 || Math.abs(z) <= 1) continue;   // gaps where the bridges come in
                b.set(x, 0, z, Blocks.NETHER_BRICK_FENCE);
                if ((Math.abs(x) + Math.abs(z)) % 6 == 0) {
                    b.set(x, 0, z, POLISHED);
                    b.set(x, 1, z, Kit.lantern(false));
                }
            }
        }
    }

    /** The hearth: the big round platform in the middle, a pool of lava ringed by magma. */
    static void hearth(Builder b) {
        deck(b, 7.5);
        rail(b, 7.5);
        b.disc(0, -1, 0, 2.5, Blocks.MAGMA_BLOCK.defaultBlockState());
        b.set(0, -1, 0, Blocks.LAVA);
        b.foundation(0, -1, 0, BASALT, PILLAR_DEPTH);
        b.set(0, -1, 0, Blocks.LAVA);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockState seat = Kit.stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, d);
            b.set(d.getStepX() * 4 + d.getClockWise().getStepX(), 0, d.getStepZ() * 4 + d.getClockWise().getStepZ(), seat);
            b.set(d.getStepX() * 4 - d.getClockWise().getStepX(), 0, d.getStepZ() * 4 - d.getClockWise().getStepZ(), seat);
        }
        // four tall chain posts with lanterns, and a lava cauldron by the barrels
        for (int[] p : new int[][]{{-5, -5}, {5, -5}, {-5, 5}, {5, 5}}) {
            b.fill(p[0], 0, p[1], p[0], 3, p[1], POLISHED);
            Kit.hangingLantern(b, p[0] - Integer.signum(p[0]), 3, p[1], 1, Kit.lantern(true));
            b.set(p[0] - Integer.signum(p[0]), 3, p[1], Kit.chain(Direction.Axis.X));
        }
        b.set(3, 0, -5, Blocks.LAVA_CAULDRON);
        b.set(4, 0, -4, Kit.facingAny(Blocks.BARREL, Direction.UP));
        b.chest(-4, 0, 4, Direction.EAST, Loot.MOORING_HOUSE);
        magmari(b, -3, 0, -2, Magmari.RESIDENT, false);
        magmari(b, 2, 0, 3, Magmari.RESIDENT, false);
    }

    /** A home: a round platform with a domed basalt hut, magma seams in its walls, a three-high doorway. */
    static void home(Builder b) {
        deck(b, 4.6);
        rail(b, 4.6);
        int r = 3;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r + 0.4) continue;
                boolean wall = d > r - 0.6;
                for (int y = 0; y <= 6; y++) {
                    // a dome: the radius shrinks above the walls
                    double rr = y <= 2 ? r : r * Math.sqrt(Math.max(0, 1 - Math.pow((y - 2) / 4.2, 2)));
                    boolean shell = d <= rr + 0.4 && d > rr - 0.7;
                    if (y <= 2 && wall) b.set(x, y, z, y == 1 && b.chance(x, y, z, 0.35F) ? Blocks.MAGMA_BLOCK.defaultBlockState() : POLISHED);
                    else if (y > 2 && shell) b.set(x, y, z, y == 3 ? BRICKS : Blocks.BLACKSTONE.defaultBlockState());
                }
            }
        }
        b.set(0, 7, 0, Blocks.POLISHED_BLACKSTONE_WALL);
        b.set(0, 8, 0, Kit.lantern(false));
        // the doorway (Magmari stand nearly three blocks tall)
        b.fill(-1, 0, -r, 1, 2, -r, Blocks.AIR.defaultBlockState());
        b.fill(0, 0, -r + 1, 0, 2, -r + 1, Blocks.AIR.defaultBlockState());
        // inside: a magma hearth, a chest, a barrel, a lantern under the dome
        b.set(0, -1, 1, Blocks.MAGMA_BLOCK);
        b.set(0, 0, 2, Blocks.LAVA_CAULDRON);
        b.chest(-2, 0, 0, Direction.EAST, Loot.MOORING_HOUSE);
        b.set(2, 0, 0, Kit.facingAny(Blocks.BARREL, Direction.UP));
        Kit.hangingLantern(b, 0, 5, 0, 1, Kit.lantern(true));
        magmari(b, 0, 0, 0, Magmari.RESIDENT, false);
        if (b.random.nextInt(3) > 0) magmari(b, -1, 0, -3.5, Magmari.RESIDENT, false);
        if (b.random.nextInt(5) < 2) magmari(b, 1, 0, -3.5, Magmari.RESIDENT, true);
    }

    /** The strider pen: open lava fenced in by a ring of posts and a narrow walk, with striders inside. */
    static void pen(Builder b) {
        double r = 5.5;
        for (int x = -6; x <= 6; x++) {
            for (int z = -6; z <= 6; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d > r || d <= r - 1) continue;
                b.set(x, -1, z, BRICKS);
                b.set(x, 0, z, Blocks.NETHER_BRICK_FENCE);
                if ((Math.abs(x) + Math.abs(z)) % 3 == 0) b.foundation(x, -1, z, BASALT, PILLAR_DEPTH);
            }
        }
        int n = 2 + b.random.nextInt(2);
        for (int i = 0; i < n; i++) {
            boolean saddled = i == 0;
            b.spawn(EntityTypes.STRIDER, -2 + i * 2, -2, 1, strider -> {
                if (saddled) strider.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            });
        }
    }

    /** The ferry dock: a T-shaped landing at the end of the pier, mooring posts, the ferrier and a saddled strider. */
    static void dock(Builder b) {
        b.clear(-5, 0, -3, 5, 5, 3);
        for (int x = -5; x <= 5; x++) {
            for (int z = -2; z <= 2; z++) {
                b.set(x, -1, z, Math.abs(x) == 5 || Math.abs(z) == 2 ? BRICKS : STONE);
                if ((Math.abs(x) == 5 || x == 0) && Math.abs(z) == 2) b.foundation(x, -1, z, BASALT, PILLAR_DEPTH);
            }
        }
        for (int x : new int[]{-5, 5}) {
            for (int z : new int[]{-2, 2}) {
                b.set(x, 0, z, POLISHED);
                b.set(x, 1, z, Kit.chainY());
                b.set(x, 2, z, Kit.lantern(false));
            }
        }
        // a rail along the lander's pier side, open where the pier comes in; the sea side stays open for the barge
        for (int x = -4; x <= 4; x++) if (Math.abs(x) > 1) b.set(x, 0, -2, Blocks.NETHER_BRICK_FENCE);
        b.set(3, 0, 1, Kit.facingAny(Blocks.BARREL, Direction.UP));
        b.barrel(4, 0, 1, Direction.UP, Loot.MOORING_DOCK);
        magmari(b, -2, 0, 0, Magmari.FERRIER, false);
        b.spawn(EntityTypes.STRIDER, 0, -2, 3, strider -> strider.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE)));
    }
}
