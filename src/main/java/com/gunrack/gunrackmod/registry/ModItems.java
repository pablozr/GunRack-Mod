package com.gunrack.gunrackmod.registry;

import com.gunrack.gunrackmod.GunRackMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, GunRackMod.MODID);

    public static final RegistryObject<Item> GUN_RACK_ITEM = REGISTER.register("gun_rack", () ->
            new BlockItem(ModBlocks.GUN_RACK.get(), new Item.Properties()));
}
