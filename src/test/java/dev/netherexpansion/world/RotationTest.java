package dev.netherexpansion.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.Test;

/** A building's own coordinates must turn the same way its block states do, or doors face walls. */
class RotationTest {
    @Test
    void localStepsTurnLikeDirections() {
        for (Rotation r : Rotation.values()) {
            for (Direction d : Direction.Plane.HORIZONTAL) {
                Direction turned = r.rotate(d);
                assertEquals(turned.getStepX(), Builder.rotX(r, d.getStepX(), d.getStepZ()), r + " " + d + " x");
                assertEquals(turned.getStepZ(), Builder.rotZ(r, d.getStepX(), d.getStepZ()), r + " " + d + " z");
            }
        }
    }

    @Test
    void facingPutsTheFrontThatWay() {
        for (Direction d : Direction.Plane.HORIZONTAL) {
            assertEquals(d, Plan.facing(d).rotate(Direction.NORTH));
        }
    }

    @Test
    void doorIsInFrontOfTheLot() {
        for (Direction d : Direction.Plane.HORIZONTAL) {
            Plan.Lot lot = new Plan.Lot(new BlockPos(100, 40, -50), Plan.facing(d), 4, 7);
            BlockPos door = lot.door();
            assertEquals(new BlockPos(100, 40, -50).relative(d, 8), door, "door of a lot facing " + d);
        }
    }

    @Test
    void boxCoversEveryCornerWhateverTheTurn() {
        BlockPos origin = new BlockPos(10, 64, 20);
        for (Rotation r : Rotation.values()) {
            BoundingBox box = Plan.box(origin, r, -3, -2, -5, 4, 9, 6);
            for (int x : new int[]{-3, 4}) {
                for (int z : new int[]{-5, 6}) {
                    BlockPos p = origin.offset(Builder.rotX(r, x, z), 0, Builder.rotZ(r, x, z));
                    assertTrue(box.isInside(p), r + " misses corner " + x + "," + z);
                }
            }
            assertEquals(62, box.minY());
            assertEquals(73, box.maxY());
        }
    }

    @Test
    void towardPicksTheLongerAxis() {
        assertEquals(Direction.EAST, Plan.toward(0, 0, 10, 3));
        assertEquals(Direction.NORTH, Plan.toward(0, 0, 2, -9));
        assertEquals(Direction.WEST, Plan.toward(5, 5, -5, 4));
        assertEquals(Direction.SOUTH, Plan.toward(0, 0, 0, 1));
    }
}
