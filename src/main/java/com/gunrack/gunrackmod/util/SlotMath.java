
package com.gunrack.gunrackmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.gunrack.gunrackmod.content.block.GunRackBlock;

public class SlotMath {
    public static final float ITEM_SPACING = 0.24f;

    public static int slotFromHit(BlockState state, BlockPos pos, Vec3 hitLoc, int slots) {
        Direction facing = state.getValue(GunRackBlock.FACING);
        Vec3 hitLocal = hitLoc.subtract(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Direction rightDir = facing.getClockWise();
        Vec3 right = new Vec3(rightDir.getStepX(), 0, rightDir.getStepZ()).normalize();
        float x = (float) hitLocal.dot(right);
        float start = -((slots - 1) * ITEM_SPACING) / 2f;
        return Mth.clamp(Math.round((x - start) / ITEM_SPACING), 0, slots - 1);
    }
}
