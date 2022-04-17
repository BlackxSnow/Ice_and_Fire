package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import com.github.alexthe666.iceandfire.item.ICustomRendered;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockGhostChest extends ChestBlock implements ICustomRendered {

    public BlockGhostChest() {
        super(
    		Properties
    			.of(Material.WOOD)
    			.strength(2.5F)
    			.sound(SoundType.WOOD),
			() -> {
	            return IafTileEntityRegistry.GHOST_CHEST;
	        }
		);

        setRegistryName(IceAndFire.MODID, "ghost_chest");
    }

    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new TileEntityGhostChest();
    }

    protected Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return Mth.clamp(ChestBlockEntity.getOpenCount(blockAccess, pos), 0, 15);
    }

    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return side == Direction.UP ? blockState.getSignal(blockAccess, pos, side) : 0;
    }
}
