package com.github.alexthe666.iceandfire.event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class WorldGenUtils {

    private static boolean canHeightSkipBlock(BlockPos pos, LevelAccessor world) {
        BlockState state = world.getBlockState(pos);
        return BlockTags.LOGS.contains(state.getBlock()) || !state.getFluidState().isEmpty();
    }

    public static BlockPos degradeSurface(LevelAccessor world, BlockPos surface) {
        while ((!world.getBlockState(surface).canOcclude() || canHeightSkipBlock(surface, world)) && surface.getY() > 1) {
            surface = surface.below();
        }
        return surface;
    }
}
