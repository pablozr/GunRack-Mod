package com.gunrack.gunrackmod.registry;

import com.gunrack.gunrackmod.GunRackMod;
import com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GunRackMod.MODID);

    public static final RegistryObject<BlockEntityType<GunRackBlockEntity>> GUN_RACK = REGISTER.register(
            "gun_rack",
            () -> BlockEntityType.Builder.of(GunRackBlockEntity::new, ModBlocks.GUN_RACK.get()).build(null)
    );
}
