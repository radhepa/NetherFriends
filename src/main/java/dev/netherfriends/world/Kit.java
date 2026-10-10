package dev.netherfriends.world;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

/** Block states the drawings use over and over: stairs, slabs, pillars, lanterns, doors. */
public final class Kit {
    private Kit() {}

    public static BlockState stairs(Block block, Direction facing) {
        return block.defaultBlockState().setValue(StairBlock.FACING, facing);
    }

    /** Upside-down stairs (for eaves and corbels). */
    public static BlockState stairsTop(Block block, Direction facing) {
        return stairs(block, facing).setValue(StairBlock.HALF, Half.TOP);
    }

    public static BlockState slab(Block block) {
        return block.defaultBlockState();
    }

    public static BlockState slabTop(Block block) {
        return block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);
    }

    public static BlockState slabDouble(Block block) {
        return block.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
    }

    public static BlockState axis(Block block, Direction.Axis axis) {
        return block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }

    public static BlockState facing(Block block, Direction facing) {
        return block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
    }

    public static BlockState facingAny(Block block, Direction facing) {
        return block.defaultBlockState().setValue(BlockStateProperties.FACING, facing);
    }

    public static BlockState lantern(boolean hanging) {
        return Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, hanging);
    }

    public static BlockState soulLantern(boolean hanging) {
        return Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, hanging);
    }

    public static BlockState campfire(Block campfire, boolean lit) {
        return campfire.defaultBlockState().setValue(CampfireBlock.LIT, lit);
    }

    public static BlockState chainY() {
        return Blocks.IRON_CHAIN.defaultBlockState();
    }

    public static BlockState chain(Direction.Axis axis) {
        return Blocks.IRON_CHAIN.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
    }

    public static BlockState cauldron(Block filled, int level) {
        BlockState state = filled.defaultBlockState();
        return state.hasProperty(LayeredCauldronBlock.LEVEL) ? state.setValue(LayeredCauldronBlock.LEVEL, level) : state;
    }

    public static BlockState gate(Block gate, Direction facing, boolean open) {
        return gate.defaultBlockState().setValue(FenceGateBlock.FACING, facing).setValue(FenceGateBlock.OPEN, open);
    }

    public static BlockState trapdoor(Block trapdoor, Direction facing, boolean top, boolean open) {
        return trapdoor.defaultBlockState().setValue(TrapDoorBlock.FACING, facing)
                .setValue(TrapDoorBlock.HALF, top ? Half.TOP : Half.BOTTOM).setValue(TrapDoorBlock.OPEN, open);
    }

    public static BlockState button(Block button, Direction facing) {
        return button.defaultBlockState().setValue(ButtonBlock.FACING, facing);
    }

    /** A two-block door at (x, y, z) facing {@code facing} (building space). */
    public static void door(Builder b, int x, int y, int z, Block door, Direction facing) {
        BlockState lower = door.defaultBlockState().setValue(DoorBlock.FACING, facing).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT);
        b.set(x, y, z, lower.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        b.set(x, y + 1, z, lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }

    /** A post of {@code block} with a lantern on top, standing at (x, y, z). */
    public static void lampPost(Builder b, int x, int y, int z, int height, BlockState post, BlockState lantern) {
        for (int i = 0; i < height; i++) b.set(x, y + i, z, post);
        b.set(x, y + height, z, lantern);
    }

    /** A lantern hanging on {@code length} links of chain below (x, top, z). */
    public static void hangingLantern(Builder b, int x, int top, int z, int length, BlockState lantern) {
        for (int i = 0; i < length; i++) b.set(x, top - i, z, chainY());
        b.set(x, top - length, z, lantern);
    }
}
