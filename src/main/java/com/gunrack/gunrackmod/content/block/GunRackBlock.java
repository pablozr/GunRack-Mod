package com.gunrack.gunrackmod.content.block;

import com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class GunRackBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public GunRackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GunRackBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx){
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit){
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GunRackBlockEntity gunRack) {
            ItemStack held = player.getItemInHand(hand);
            if (player.isShiftKeyDown()){
                ItemStack out = gunRack.extractOne();
                if (!out.isEmpty()) player.addItem(out);
            } else{
                if(!held.isEmpty()){
                    ItemStack remainder = gunRack.insertOne(held);
                    player.setItemInHand(hand, remainder);
                }
            }
            gunRack.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
