package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class WorldGenCaveStalactites {
    private Block block;
    private int maxHeight = 3;

    public WorldGenCaveStalactites(Block block, int maxHeight) {
        this.block = block;
        this.maxHeight = maxHeight;
    }

    public boolean generate(LevelAccessor worldIn, Random rand, BlockPos position) {
        int height = maxHeight + rand.nextInt(3);
        for (int i = 0; i < height; i++) {
            if (i < height / 2) {
                worldIn.setBlock(position.below(i).north(), block.defaultBlockState(), 2);
                worldIn.setBlock(position.below(i).east(), block.defaultBlockState(), 2);
                worldIn.setBlock(position.below(i).south(), block.defaultBlockState(), 2);
                worldIn.setBlock(position.below(i).west(), block.defaultBlockState(), 2);
            }
            worldIn.setBlock(position.below(i), block.defaultBlockState(), 2);
        }
        return true;
    }
}
