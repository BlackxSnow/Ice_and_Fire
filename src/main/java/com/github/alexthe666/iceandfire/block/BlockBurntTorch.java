package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockBurntTorch extends TorchBlock implements IDreadBlock, IWallBlock {

    public BlockBurntTorch() {
        super(
    		Properties.of(Material.WOOD)
	    		.lightLevel((state) -> { return 0; })
	    		.sound(SoundType.WOOD)
	    		.noOcclusion()
	    		.dynamicShape()
    		.noCollission(),
    		DustParticleOptions.REDSTONE
		);

        setRegistryName(IceAndFire.MODID, "burnt_torch");
    }


    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {

    }

    @Override
    public Block wallBlock() {
        return IafBlockRegistry.BURNT_TORCH_WALL;
    }
}