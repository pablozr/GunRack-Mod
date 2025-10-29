package com.gunrack.gunrackmod.registry;

import com.gunrack.gunrackmod.GunRackMod;
import com.gunrack.gunrackmod.content.block.GunRackBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GunRackMod.MODID);

    public static final RegistryObject<Block> GUN_RACK = REGISTER.register("gun_rack", () ->
            new GunRackBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f, 3.0f)
                    .noOcclusion()
                    .noCollission()
                    .pushReaction(PushReaction.NORMAL)
            ));
}
