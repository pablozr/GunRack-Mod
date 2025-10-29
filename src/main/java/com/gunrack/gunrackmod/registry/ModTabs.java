package com.gunrack.gunrackmod.registry;

import com.gunrack.gunrackmod.GunRackMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GunRackMod.MODID);

    public static final RegistryObject<CreativeModeTab> MY_TAB =
            REGISTER.register("gunrack_tab", () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.GUN_RACK_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.gunrack_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.GUN_RACK_ITEM.get());
                    })
                    .build());

}
