package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;

import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockEggInIce extends BaseEntityBlock {
    public Item itemBlock;

    @SuppressWarnings("deprecation")
    public BlockEggInIce() {
        super(
    		Properties
    			.of(Material.ICE)
    			.noOcclusion()
    			.dynamicShape()
    			.strength(0.5F)
    			.dynamicShape()
    			.sound(SoundType.GLASS)
		);

        setRegistryName(IceAndFire.MODID, "egginice");
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new TileEntityEggInIce();
    }

    public Item getItem(Level worldIn, BlockPos pos) {
        return Item.byBlock(Blocks.ICE);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack) {
        if (worldIn.getBlockEntity(pos) != null) {
            if (worldIn.getBlockEntity(pos) instanceof TileEntityEggInIce) {
                TileEntityEggInIce tile = (TileEntityEggInIce) worldIn.getBlockEntity(pos);
                tile.spawnEgg();
            }
        }
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
    }

    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(BlockState blockstate) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isFullCube(BlockState blockstate) {
        return false;
    }
}
