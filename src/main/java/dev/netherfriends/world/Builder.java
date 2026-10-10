package dev.netherfriends.world;

import java.util.function.Consumer;

import dev.netherfriends.entity.People;
import dev.netherfriends.entity.Resident;
import dev.netherfriends.mixin.SignAccess;
import dev.netherfriends.talk.Folk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Draws one building into the chunk being generated. Coordinates are the building's own: x across, y up
 * from its floor (y = 0 is the floor level people stand on), z from its front (negative z is the side the
 * door faces) to its back, around its centre. The piece's rotation turns all of it, block states included.
 *
 * <p>A building is drawn once per chunk it touches; only blocks inside that chunk are placed. So drawing must
 * not depend on what's already placed for its random choices: use {@link #random} for decisions made up
 * front, and {@link #chance}/{@link #pick} (hashed from the position) for per-block variety.
 */
public final class Builder {
    public final WorldGenLevel level;
    public final BoundingBox chunk;
    /** Seeded from the piece, so every chunk sees the same choices. */
    public final RandomSource random;
    public final int[] params;
    /** The piece's kind ("hold/longhouse"): residents spawned here take their job from it. */
    public final String kind;
    private final BlockPos origin;
    private final Rotation rotation;
    private final long seed;
    private final BlockPos village;
    private final int villageRadius;

    Builder(WorldGenLevel level, BoundingBox chunk, String kind, BlockPos origin, Rotation rotation, long seed, int[] params,
            BlockPos village, int villageRadius) {
        this.level = level;
        this.kind = kind;
        this.chunk = chunk;
        this.origin = origin;
        this.rotation = rotation;
        this.seed = seed;
        this.random = RandomSource.create(seed);
        this.params = params;
        this.village = village;
        this.villageRadius = villageRadius;
    }

    public int param(int i, int fallback) {
        return i < this.params.length ? this.params[i] : fallback;
    }

    // -- coordinates ------------------------------------------------------------------------------------

    public BlockPos pos(int x, int y, int z) {
        return this.origin.offset(rotX(this.rotation, x, z), y, rotZ(this.rotation, x, z));
    }

    static int rotX(Rotation r, int x, int z) {
        return switch (r) {
            case NONE -> x;
            case CLOCKWISE_90 -> -z;
            case CLOCKWISE_180 -> -x;
            case COUNTERCLOCKWISE_90 -> z;
        };
    }

    static int rotZ(Rotation r, int x, int z) {
        return switch (r) {
            case NONE -> z;
            case CLOCKWISE_90 -> x;
            case CLOCKWISE_180 -> -z;
            case COUNTERCLOCKWISE_90 -> -x;
        };
    }

    /** A direction in building space (NORTH = toward the front) turned to the world. */
    public Direction dir(Direction local) {
        return this.rotation.rotate(local);
    }

    public BlockPos origin() {
        return this.origin;
    }

    public boolean inChunk(int x, int y, int z) {
        return this.chunk.isInside(this.pos(x, y, z));
    }

    // -- per-block variety ------------------------------------------------------------------------------------

    /** A number in [0, 1) fixed for this building and position. */
    public float noise(int x, int y, int z) {
        long h = this.seed ^ (x * 0x9E3779B97F4A7C15L) ^ (y * 0xC2B2AE3D27D4EB4FL) ^ (z * 0x165667B19E3779F9L);
        h = (h ^ (h >>> 31)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 29)) * 0x94D049BB133111EBL;
        return ((h ^ (h >>> 32)) >>> 40) / (float) (1 << 24);
    }

    public boolean chance(int x, int y, int z, float p) {
        return this.noise(x, y, z) < p;
    }

    @SafeVarargs
    public final <T> T pick(int x, int y, int z, T... options) {
        return options[Math.min(options.length - 1, (int) (this.noise(x, y, z) * options.length))];
    }

    // -- placing ----------------------------------------------------------------------------------------------

    /** Place a block given in building space (its facing is turned with the building). */
    public void set(int x, int y, int z, BlockState state) {
        this.setWorld(this.pos(x, y, z), state.rotate(this.rotation));
    }

    public void set(int x, int y, int z, Block block) {
        this.set(x, y, z, block.defaultBlockState());
    }

    /** Place a block at a world position, as given (no turning). */
    public void setWorld(BlockPos pos, BlockState state) {
        if (!this.chunk.isInside(pos)) return;
        this.level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        FluidState fluid = state.getFluidState();
        if (!fluid.isEmpty()) this.level.scheduleTick(pos, fluid.getType(), 0);
        if (needsShapeUpdate(state)) this.level.getChunk(pos).markPosForPostProcessing(pos);
    }

    /** Fences, panes, bars, walls and stairs work out their connections once the chunk is finished. */
    static boolean needsShapeUpdate(BlockState state) {
        Block block = state.getBlock();
        return block instanceof CrossCollisionBlock || block instanceof WallBlock || block instanceof StairBlock
                || state.is(Blocks.IRON_CHAIN) || state.is(BlockTags.LEAVES) || state.is(Blocks.TWISTING_VINES)
                || state.is(Blocks.TWISTING_VINES_PLANT) || state.is(Blocks.WEEPING_VINES) || state.is(Blocks.WEEPING_VINES_PLANT);
    }

    public BlockState get(int x, int y, int z) {
        BlockPos p = this.pos(x, y, z);
        return this.chunk.isInside(p) ? this.level.getBlockState(p) : Blocks.AIR.defaultBlockState();
    }

    public void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState state) {
        for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) {
            for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++) {
                for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
                    this.set(x, y, z, state);
                }
            }
        }
    }

    public void fill(int x0, int y0, int z0, int x1, int y1, int z1, Block block) {
        this.fill(x0, y0, z0, x1, y1, z1, block.defaultBlockState());
    }

    public void air(int x0, int y0, int z0, int x1, int y1, int z1) {
        this.fill(x0, y0, z0, x1, y1, z1, Blocks.AIR.defaultBlockState());
    }

    /** The four walls of a box (no floor or roof). */
    public void walls(int x0, int y0, int z0, int x1, int y1, int z1, BlockState state) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                this.set(x, y, z0, state);
                this.set(x, y, z1, state);
            }
            for (int z = z0 + 1; z < z1; z++) {
                this.set(x0, y, z, state);
                this.set(x1, y, z, state);
            }
        }
    }

    /** A filled disc of radius r (in blocks, measured to block centres) at height y. */
    public void disc(int cx, int y, int cz, double r, BlockState state) {
        int ri = Mth.ceil(r);
        for (int x = -ri; x <= ri; x++) {
            for (int z = -ri; z <= ri; z++) {
                if (x * x + z * z <= r * r) this.set(cx + x, y, cz + z, state);
            }
        }
    }

    /** A ring one block wide between radius r - 1 and r. */
    public void ring(int cx, int y, int cz, double r, BlockState state) {
        int ri = Mth.ceil(r);
        for (int x = -ri; x <= ri; x++) {
            for (int z = -ri; z <= ri; z++) {
                double d = Math.sqrt(x * x + z * z);
                if (d <= r && d > r - 1) this.set(cx + x, y, cz + z, state);
            }
        }
    }

    /**
     * Fill downward from just below y until something solid is reached (or {@code maxDepth} blocks), so a
     * building never hangs over a gap. Lava counts as a gap: foundations stand on the sea floor.
     */
    public void foundation(int x, int y, int z, BlockState state, int maxDepth) {
        for (int i = 1; i <= maxDepth; i++) {
            BlockPos p = this.pos(x, y - i, z);
            if (!this.chunk.isInside(p)) return;
            BlockState here = this.level.getBlockState(p);
            if (!here.isAir() && here.getFluidState().isEmpty() && !here.canBeReplaced()) return;
            this.setWorld(p, state.rotate(this.rotation));
        }
    }

    public void foundation(int x0, int z0, int x1, int z1, int y, BlockState state, int maxDepth) {
        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                this.foundation(x, y, z, state, maxDepth);
            }
        }
    }

    /** Clear a box to air, leaving nothing of the hill or the fungus that was there. */
    public void clear(int x0, int y0, int z0, int x1, int y1, int z1) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                for (int z = z0; z <= z1; z++) {
                    BlockPos p = this.pos(x, y, z);
                    if (this.chunk.isInside(p) && !this.level.getBlockState(p).isAir()) this.setWorld(p, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    /** A column of hanging chain from y up to the first solid block (at most {@code max}); returns its top. */
    public int chainUp(int x, int y, int z, int max) {
        int top = y;
        for (int i = 0; i < max; i++) {
            BlockPos p = this.pos(x, y + i, z);
            if (!this.chunk.isInside(p)) break;
            BlockState here = this.level.getBlockState(p);
            if (!here.isAir() && !here.is(Blocks.IRON_CHAIN)) break;
            this.setWorld(p, Blocks.IRON_CHAIN.defaultBlockState());
            top = y + i;
        }
        return top;
    }

    // -- block entities and residents -------------------------------------------------------------------------

    /** A chest facing {@code facing} (building space) filled from a loot table. */
    public void chest(int x, int y, int z, Direction facing, ResourceKey<LootTable> loot) {
        this.containerWithLoot(x, y, z, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing), loot);
    }

    public void barrel(int x, int y, int z, Direction facing, ResourceKey<LootTable> loot) {
        this.containerWithLoot(x, y, z, Blocks.BARREL.defaultBlockState().setValue(BlockStateProperties.FACING, facing), loot);
    }

    private void containerWithLoot(int x, int y, int z, BlockState state, ResourceKey<LootTable> loot) {
        BlockPos p = this.pos(x, y, z);
        if (!this.chunk.isInside(p)) return;
        this.set(x, y, z, state);
        RandomizableContainer.setBlockEntityLootTable(this.level, RandomSource.create(this.seed ^ p.asLong()), p, loot);
    }

    /** A wall sign facing {@code facing} (building space), with up to four lines. */
    public void sign(int x, int y, int z, BlockState wallSign, Direction facing, boolean glowing, String... lines) {
        BlockPos p = this.pos(x, y, z);
        if (!this.chunk.isInside(p)) return;
        this.set(x, y, z, wallSign.setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
        if (this.level.getBlockEntity(p) instanceof SignBlockEntity sign) {
            java.util.List<Component> messages = new java.util.ArrayList<>();
            for (int i = 0; i < SignText.LINES; i++) messages.add(Component.literal(i < lines.length ? lines[i] : ""));
            SignAccess access = (SignAccess) sign;
            access.netherfriends$setFrontText(new SignText(messages, messages, glowing ? DyeColor.CYAN : DyeColor.BLACK, glowing));
            access.netherfriends$setWaxed(true);
        }
    }

    /**
     * Spawn a mob standing at this spot (building space) when the spot falls in this chunk; residents settle in
     * the village. {@code setup} runs before it's added (to set a role, a gender, make a child...).
     */
    public <T extends Mob> void spawn(EntityType<T> type, double x, double y, double z, Consumer<T> setup) {
        this.spawn(type, x, y, z, null, setup);
    }

    /**
     * Spawn a mob as {@link #spawn(EntityType, double, double, double, Consumer)} does. One of the peoples also gets
     * their profile (name, job, home): {@code job} is a job or a choice of jobs ("Weaver|Spinner"); null takes the
     * job this kind of building gives. Must not use {@link #random}, which every chunk has to draw the same way.
     */
    public <T extends Mob> void spawn(EntityType<T> type, double x, double y, double z, @org.jspecify.annotations.Nullable String job,
                                      Consumer<T> setup) {
        int bx = Mth.floor(x), bz = Mth.floor(z);
        BlockPos p = this.pos(bx, Mth.floor(y), bz);
        if (!this.chunk.isInside(p)) return;
        T mob = type.create(this.level.getLevel(), EntitySpawnReason.STRUCTURE);
        if (mob == null) return;
        mob.snapTo(p.getX() + 0.5, p.getY() + (y - Mth.floor(y)), p.getZ() + 0.5, this.noise(bx, 7, bz) * 360.0F, 0.0F);
        mob.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(p), EntitySpawnReason.STRUCTURE, null);
        mob.setPersistenceRequired();
        if (mob instanceof Resident resident) resident.settle(this.village, this.villageRadius);
        else mob.setHomeTo(this.village, this.villageRadius);
        setup.accept(mob);
        if (People.of(mob) != null) Folk.settle(mob, job != null ? job : Folk.jobAt(this.kind), this.origin, this.village);
        this.level.addFreshEntityWithPassengers(mob);
    }

    public BlockPos village() {
        return this.village;
    }
}
