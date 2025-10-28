package com.gunrack.gunrackmod.client.render;

import com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class GunRackRenderer implements BlockEntityRenderer<GunRackBlockEntity> {
    private final ItemRenderer itemRenderer;

    public GunRackRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render (GunRackBlockEntity blockEntity, float partialTick, PoseStack pose,
                        MultiBufferSource buffers, int light, int overlay) {
        Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        float yaw = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f;
        };

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                pose.pushPose();
                pose.translate(0.5, 0.95, 0.5);
                pose.mulPose(Axis.YP.rotationDegrees(yaw));
                float x = -0.4f + i * 0.2f;
                pose.translate(x, 0.0, 0.0);
                pose.mulPose(Axis.XP.rotationDegrees(90f));
                pose.scale(0.6f, 0.6f, 0.6f);

                itemRenderer.renderStatic(
                        stack,
                        ItemDisplayContext.FIXED,
                        light,
                        overlay,
                        pose,
                        buffers,
                        blockEntity.getLevel(),
                        0
                );
                pose.popPose();
            }
        });
    }
}
