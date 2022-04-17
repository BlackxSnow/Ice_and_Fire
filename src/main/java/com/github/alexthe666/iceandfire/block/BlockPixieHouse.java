package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import com.github.alexthe666.iceandfire.item.ICustomRendered;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockPixieHouse extends BaseEntityBlock implements ICustomRendered {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public BlockPixieHouse(String type) {
        super(
    		Properties
    			.of(Material.WOOD)
    			.noOcclusion()
    			.dynamicShape()
    			.strength(2.0F, 5.0F)
    			.randomTicks()
		);

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
        this.setRegistryName(IceAndFire.MODID, "pixie_house_" + type);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        dropPixie(worldIn, pos);
        popResource(worldIn, pos, new ItemStack(this, 0));
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }


    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        //worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        this.checkFall(worldIn, pos);
    }

    private boolean checkFall(Level worldIn, BlockPos pos) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
            dropPixie(worldIn, pos);
            return false;
        } else {
            return true;
        }
    }

    private boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
        return true;
    }

    public void dropPixie(Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityPixieHouse && ((TileEntityPixieHouse) world.getBlockEntity(pos)).hasPixie) {
            ((TileEntityPixieHouse) world.getBlockEntity(pos)).releasePixie();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new TileEntityPixieHouse();
    }
}
