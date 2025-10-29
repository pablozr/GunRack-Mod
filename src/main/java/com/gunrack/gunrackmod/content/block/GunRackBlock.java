package com.gunrack.gunrackmod.content.block;

import com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class GunRackBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final float ITEM_SPACING = 0.24f;

    private static final Map<Direction, VoxelShape> OUTLINES = new EnumMap<>(Direction.class);
    static {
        // cubo base
        VoxelShape core = Block.box(0, 0, 0, 16, 16, 16);
        // “placa” fina estendida ~5 px para fora na frente (alinha com itens renderizados)
        OUTLINES.put(Direction.NORTH, Shapes.or(core, Block.box(0, 2, -5, 16, 16, 0)));
        OUTLINES.put(Direction.SOUTH, Shapes.or(core, Block.box(0, 2, 16, 16, 16+5, 16)));
        OUTLINES.put(Direction.WEST,  Shapes.or(core, Block.box(-5, 2, 0, 0, 16, 16)));
        OUTLINES.put(Direction.EAST,  Shapes.or(core, Block.box(16, 2, 0, 16+5, 16, 16)));
    }

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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof com.gunrack.gunrackmod.content.blockentity.GunRackBlockEntity rack && !level.isClientSide) {
                rack.dropAllItemsInto(level, pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        } else {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        Direction f = state.getValue(FACING);
        // mesma OUTLINES que você já montou, com a “placa” à frente
        return OUTLINES.getOrDefault(f, Block.box(0, 0, 0, 16, 16, 16));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty(); // continua sem colisão para poder atravessar
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit){
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof GunRackBlockEntity rack)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            int idx = slotFromHit(state, pos, hit, rack.getSlotCount());
            ItemStack out = idx >= 0 ? rack.extractAt(idx) : rack.extractOne();
            if (!out.isEmpty()) player.addItem(out);
            rack.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            return InteractionResult.CONSUME;
        } else {
            if (!held.isEmpty()){
                ItemStack remainder = rack.insertOne(held);
                player.setItemInHand(hand, remainder);
                rack.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    private static int slotFromHit(BlockState state, BlockPos pos, BlockHitResult hit, int slots) {
        Direction facing = state.getValue(FACING);

        Vec3 hitLocal = hit.getLocation().subtract(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Direction rightDir = facing.getClockWise();
        Vec3 right = new Vec3(rightDir.getStepX(), 0, rightDir.getStepZ()).normalize();

        float x = (float) hitLocal.dot(right); // coordenada “lateral” no espaço do rack
        float start = -((slots - 1) * ITEM_SPACING) / 2f;

        int idx = Mth.clamp(Math.round((x - start) / ITEM_SPACING), 0, slots - 1);
        return idx;
    }
}
