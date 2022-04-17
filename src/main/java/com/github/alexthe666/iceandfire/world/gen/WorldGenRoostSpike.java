package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class WorldGenRoostSpike {

    private Direction direction;

    public WorldGenRoostSpike(Direction direction) {
        this.direction = direction;
    }

    public boolean generate(LevelAccessor worldIn, Random rand, BlockPos position) {
        int radius = 5;
        for(int i = 0; i < 5; i++){
            int j = Math.max(0, radius - (int)(i * 1.75F));
            int l = radius - i;
            int k = Math.max(0, radius - (int)( i * 1.5F));
            float f = (float) (j + l) * 0.333F + 0.5F;
            BlockPos up = position.above().relative(direction, i);
            int xOrZero = direction.getAxis() == Direction.Axis.Z ? j : 0;
            int zOrZero = direction.getAxis() == Direction.Axis.Z ? 0 : k;
            for (BlockPos blockpos : BlockPos.betweenClosedStream(up.offset(-xOrZero, -l, -zOrZero), up.offset(xOrZero, l, zOrZero)).map(BlockPos::immutable).collect(Collectors.toSet())) {
                if (blockpos.distSqr(position) <= (double) (f * f)) {
                    int height = Math.max(blockpos.getY() - up.getY(), 0);
                    if(i <= 0){
                        if(rand.nextFloat() < height * 0.3F){
                            worldIn.setBlock(blockpos, IafBlockRegistry.CRACKLED_STONE.defaultBlockState(), 2);
                        }
                    }else{
                        worldIn.setBlock(blockpos, IafBlockRegistry.CRACKLED_STONE.defaultBlockState(), 2);
                    }
                }
            }
        }
        return true;
    }
}