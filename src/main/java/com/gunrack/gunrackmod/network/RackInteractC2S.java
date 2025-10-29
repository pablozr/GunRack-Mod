package com.gunrack.gunrackmod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.gunrack.gunrackmod.content.block.GunRackBlock;
import com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity;
import com.gunrack.gunrackmod.util.SlotMath;

public class RackInteractC2S {
    public final BlockPos pos;
    public final int action; // 0 insert, 1 extract
    public final double hx, hy, hz;

    public RackInteractC2S(BlockPos pos, int action, double hx, double hy, double hz) {
        this.pos = pos; this.action = action; this.hx = hx; this.hy = hy; this.hz = hz;
    }

    public static void encode(RackInteractC2S pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.action);
        buf.writeDouble(pkt.hx); buf.writeDouble(pkt.hy); buf.writeDouble(pkt.hz);
    }

    public static RackInteractC2S decode(FriendlyByteBuf buf) {
        BlockPos p = buf.readBlockPos();
        int a = buf.readVarInt();
        double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
        return new RackInteractC2S(p, a, x, y, z);
    }

    public static void handle(RackInteractC2S pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) return;
            var level = sp.serverLevel();
            if (!level.isLoaded(pkt.pos)) return;
            var state = level.getBlockState(pkt.pos);
            if (!(state.getBlock() instanceof GunRackBlock)) return;

            BlockEntity be = level.getBlockEntity(pkt.pos);
            if (!(be instanceof GunRackBlockEntity rack)) return;

            if (sp.position().distanceTo(Vec3.atCenterOf(pkt.pos)) > 8.0) return;

            if (pkt.action == 0) {
                ItemStack held = sp.getMainHandItem();
                if (!held.isEmpty()) {
                    ItemStack rem = rack.insertOne(held);
                    sp.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, rem);
                    rack.setChanged();
                    level.sendBlockUpdated(pkt.pos, state, state, 3);
                }
            } else {
                int idx = SlotMath.slotFromHit(state, pkt.pos, new Vec3(pkt.hx, pkt.hy, pkt.hz), rack.getSlotCount());
                ItemStack out = idx >= 0 ? rack.extractAt(idx) : rack.extractOne();
                if (!out.isEmpty()) {
                    if (!sp.addItem(out)) sp.drop(out, false);
                    rack.setChanged();
                    level.sendBlockUpdated(pkt.pos, state, state, 3);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
