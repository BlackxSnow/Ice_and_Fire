package com.github.alexthe666.iceandfire.block;

import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockGenericStairs extends StairBlock {

    public BlockGenericStairs(BlockState modelState, String name) {
        super(
    		modelState,
    		BlockBehaviour.Properties
    			.of(modelState.getMaterial())
    			.harvestTool(ToolType.PICKAXE)
    			.strength(20F)
		);
        
        this.setRegistryName(name);
    }
}
