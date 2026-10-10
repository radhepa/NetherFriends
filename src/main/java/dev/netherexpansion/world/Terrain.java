package dev.netherexpansion.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import net.minecraft.core.QuartPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * What a village plan can know about the ground before any of it exists: the noise shape of the Nether
 * (netherrack, lava below the sea, air), read one column at a time from the chunk generator and cached.
 * There's no heightmap worth using under the Nether's roof, so everything here scans columns.
 */
public final class Terrain {
    public static final int NONE = Integer.MIN_VALUE;
    private final Structure.GenerationContext ctx;
    private final Long2ObjectOpenHashMap<NoiseColumn> columns = new Long2ObjectOpenHashMap<>();
    public final int seaLevel;
    public final int minY;
    /** The highest y a floor search looks at: the Nether's roof begins a little above this. */
    public final int topY;

    public Terrain(Structure.GenerationContext ctx) {
        this.ctx = ctx;
        this.seaLevel = ctx.chunkGenerator().getSeaLevel();
        this.minY = ctx.heightAccessor().getMinY();
        this.topY = Math.min(ctx.chunkGenerator().getGenDepth() + this.minY, 128) - 6;
    }

    public NoiseColumn column(int x, int z) {
        return this.columns.computeIfAbsent(ChunkPos.pack(x, z),
                k -> this.ctx.chunkGenerator().getBaseColumn(x, z, this.ctx.heightAccessor(), this.ctx.randomState()));
    }

    public BlockState block(int x, int y, int z) {
        return this.column(x, z).getBlock(y);
    }

    /** Netherrack (or whatever the noise is made of): anything that isn't air or a fluid. */
    public boolean solid(int x, int y, int z) {
        BlockState state = this.block(x, y, z);
        return !state.isAir() && state.getFluidState().isEmpty();
    }

    public boolean air(int x, int y, int z) {
        return this.block(x, y, z).isAir();
    }

    public boolean lava(int x, int y, int z) {
        return this.block(x, y, z).getFluidState().is(FluidTags.LAVA);
    }

    /** Is y a standing height: solid below, and {@code clear} blocks of air from y up? */
    public boolean standable(int x, int y, int z, int clear) {
        if (!this.solid(x, y - 1, z)) return false;
        for (int i = 0; i < clear; i++) {
            if (!this.air(x, y + i, z)) return false;
        }
        return true;
    }

    /** The standing height nearest {@code hint} (within {@code range} up or down) with {@code clear} air above, or NONE. */
    public int floorNear(int x, int z, int hint, int range, int clear) {
        for (int d = 0; d <= range; d++) {
            if (hint - d > this.seaLevel && this.standable(x, hint - d, z, clear)) return hint - d;
            if (d > 0 && hint + d < this.topY && this.standable(x, hint + d, z, clear)) return hint + d;
        }
        return NONE;
    }

    /** The lowest standing height above the lava sea with {@code clear} air above it, or NONE. */
    public int lowestFloor(int x, int z, int clear) {
        for (int y = this.seaLevel + 1; y < this.topY - clear; y++) {
            if (this.standable(x, y, z, clear)) return y;
        }
        return NONE;
    }

    /** How many blocks of air there are from y upward (counting stops at {@code max}). */
    public int headroom(int x, int y, int z, int max) {
        int n = 0;
        while (n < max && this.air(x, y + n, z)) n++;
        return n;
    }

    /** Open lava sea: the surface block is lava and there's air for {@code clear} blocks above it. */
    public boolean openLava(int x, int z, int clear) {
        if (!this.lava(x, this.seaLevel - 1, z)) return false;
        for (int i = 0; i < clear; i++) {
            if (!this.air(x, this.seaLevel + i, z)) return false;
        }
        return true;
    }

    /** Whether the village's biome is found at this block (structures only check their centre otherwise). */
    public boolean biome(int x, int y, int z) {
        return this.ctx.validBiome().test(this.ctx.biomeResolver().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z)));
    }
}
