package com.github.alexthe666.iceandfire.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtils {

    public static boolean isDreadBlock(BlockState state) {
        Block block = state.getBlock();
        return block instanceof IDreadBlock || block == IafBlockRegistry.DREAD_STONE || block == IafBlockRegistry.DREAD_STONE_BRICKS || block == IafBlockRegistry.DREAD_STONE_BRICKS_CHISELED
                || block == IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED || block == IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY || block == IafBlockRegistry.DREAD_STONE_TILE
                || block == IafBlockRegistry.DREADWOOD_PLANKS || block == IafBlockRegistry.DREAD_STONE_BRICKS_STAIRS;
    }

    public static boolean canSnowUpon(BlockState state) {
        return !isDreadBlock(state) && state.getBlock() != IafBlockRegistry.DRAGON_ICE_SPIKES;
    }
}
