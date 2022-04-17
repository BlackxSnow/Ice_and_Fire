package com.github.alexthe666.iceandfire.pathfinding.raycoms;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public interface IPassabilityNavigator {

    int maxSearchNodes();

    boolean isBlockPassable(BlockState state, BlockPos pos, BlockPos entityPos);
}
