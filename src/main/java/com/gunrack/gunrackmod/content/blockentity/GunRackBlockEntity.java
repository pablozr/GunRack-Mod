package com.gunrack.gunrackmod.content.blockentity;

import com.gunrack.gunrackmod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class GunRackBlockEntity extends BlockEntity {
    private final ItemStackHandler items = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            onInvChanged();
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return isTacZGun(stack); }
        @Override
        public int getSlotLimit(int slot) { return 1; }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> items);

    public GunRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GUN_RACK.get(), pos, state);
    }

    public int getSlotCount() {
        return items.getSlots();
    }

    public boolean isTacZGun(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key == null) return false;
        CompoundTag tag = stack.getTag();
        return "tacz".equals(key.getNamespace()) && tag != null && tag.contains("GunId");
    }

    public ItemStack insertOne(ItemStack fromHand) {
        if (fromHand.isEmpty() || !isTacZGun(fromHand)) return fromHand;
        ItemStack copyOne = fromHand.copy();
        copyOne.setCount(1);
        for (int i = 0; i < items.getSlots(); i++) {
            if (items.getStackInSlot(i).isEmpty()) {
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
            if (!s.isEmpty()) {
                items.setStackInSlot(i, ItemStack.EMPTY);
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    public void dropAllItemsInto(Level level, BlockPos pos) {
        this.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack s = handler.getStackInSlot(i);
                if (!s.isEmpty()) {
                    net.minecraft.world.level.block.Block.popResource(level, pos, s.copy());
                    if (this.level != null && !this.level.isClientSide) {
                        if (handler == null) continue;
                        if (this.level.getBlockEntity(this.worldPosition) == this) {
                            ((net.minecraftforge.items.ItemStackHandler) handler).setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
            this.setChanged();
        });
    }

    public ItemStack extractAt(int index) {
        if (index < 0 || index >= items.getSlots()) return ItemStack.EMPTY;
        ItemStack s = items.getStackInSlot(index);
        if (!s.isEmpty()) {
            items.setStackInSlot(index, ItemStack.EMPTY);
            return s;
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

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("inv", items.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            items.deserializeNBT(tag.getCompound("inv"));
        }
    }

    private void onInvChanged() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }
}
