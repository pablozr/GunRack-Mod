package com.gunrack.gunrackmod.client.render;

import com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
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
    public void render(GunRackBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        float yaw = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default    -> 0f;
        };

        int packedLight = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());

        be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int slots = handler.getSlots();
            float spacing = 0.24f;
            float start = -((slots - 1) * spacing) / 2f;
            float scale = 0.5f;

            for (int i = 0; i < slots; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                pose.pushPose();
                pose.translate(0.5, 0.95, 0.5);
                pose.mulPose(Axis.YP.rotationDegrees(yaw));
                float x = start + i * spacing;
                pose.translate(x, 0.0, 0.0);
                pose.mulPose(Axis.XP.rotationDegrees(90f));
                pose.translate(0.0, 0.0, -0.08f);
                pose.scale(scale, scale, scale);

                itemRenderer.renderStatic(
                        stack,
                        ItemDisplayContext.NONE,
                        packedLight,
                        overlay,
                        pose,
                        buffers,
                        be.getLevel(),
                        0
                );
                pose.popPose();
            }
        });
    }
}
