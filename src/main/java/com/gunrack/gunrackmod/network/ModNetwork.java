package com.gunrack.gunrackmod.network;

import com.gunrack.gunrackmod.GunRackMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GunRackMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );
    private static int id = 0;
    public static void register() {
        CHANNEL.registerMessage(id++, RackInteractC2S.class,
                RackInteractC2S::encode, RackInteractC2S::decode, RackInteractC2S::handle);
    }
    public static void sendToServer(Object msg) { CHANNEL.sendToServer(msg); }
}
