package com.gunrack.gunrackmod;

import com.gunrack.gunrackmod.registry.ModBlocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GunRackMod.MODID)
public final class GunRackMod {
    public static final String MODID = "gunrack";

    public GunRackMod(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.REGISTER.register(bus);
    }
}