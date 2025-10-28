package com.gunrack.gunrackmod.content.blockentity;

import com.gunrack.gunrackmod.registry.ModBlockEntities;
import com.gunrack.gunrackmod.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class GunRackBlockEntity extends BlockEntity {
    private final ItemStackHandler items = new ItemStackHandler(5){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack){
            return isTacZGun(stack);
        }

        @Override
        public int getSlotLimit(int slot){
            return 1;
        }
    };

    public GunRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUN_RACK.get(), pos, state);
    }

    public boolean isTacZGun(ItemStack stack) {
        if (stack.isEmpty()) return false;

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(key == null) return false;

        CompoundTag tag = stack.getTag();
        return "tacz".equals(key.getNamespace()) && tag != null && tag.contains("GunId");
    }

    public ItemStack insertOne(ItemStack fromHand) {
        if (fromHand.isEmpty() || !isTacZGun(fromHand)) return fromHand;

        ItemStack copyOne = fromHand.copy();
        copyOne.setCount(1);

        for (int i = 0; i < items.getSlots(); i++) {
            if(items.getStackInSlot(i).isEmpty()) {
                items.setStackInSlot(i, copyOne);
                fromHand.shrink(1);
                break;
            }
        }
        return fromHand;
    }

    public ItemStack extractOne() {
        for (int i = items.getSlots() - 1; i >= 0; i--) {
            ItemStack s = items.getStackInSlot(i);
            if (!s.isEmpty()){
                items.setStackInSlot(i, ItemStack.EMPTY);
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("inv"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inv", items.serializeNBT());
        super.saveAdditional(tag);
    }
}
