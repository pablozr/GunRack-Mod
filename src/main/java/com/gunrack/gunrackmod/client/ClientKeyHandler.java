package com.gunrack.gunrackmod.client;

import com.gunrack.gunrackmod.network.ModNetwork;
import com.gunrack.gunrackmod.network.RackInteractC2S;
import com.gunrack.gunrackmod.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientKeyHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        while (ModKeyMappings.RACK_INTERACT.consumeClick()) {
            HitResult hit = mc.hitResult;
            if (hit instanceof BlockHitResult bhr) {
                var pos = bhr.getBlockPos();
                var state = mc.level.getBlockState(pos);
                if (state.getBlock() == ModBlocks.GUN_RACK.get()) {
                    boolean hasItemInHand = !mc.player.getMainHandItem().isEmpty();
                    int action = hasItemInHand ? 0 : 1; // 0=insert, 1=extract
                    var v = bhr.getLocation();
                    ModNetwork.sendToServer(new RackInteractC2S(pos, action, v.x, v.y, v.z));
                }
            }
        }
    }
}
