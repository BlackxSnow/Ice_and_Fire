package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockIafOre extends Block {
    public Item itemBlock;

    public BlockIafOre(int toollevel, float hardness, float resistance, String name, String gameName) {
        super(
    		Properties
    			.of(Material.STONE)
    			.harvestTool(ToolType.PICKAXE)
    			.harvestLevel(toollevel)
    			.strength(hardness, resistance)
    			.requiresCorrectToolForDrops()
		);

        setRegistryName(IceAndFire.MODID, gameName);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.getExperience(RANDOM) : 0;
    }

    protected int getExperience(Random rand) {
        if (this == IafBlockRegistry.SAPPHIRE_ORE || this == IafBlockRegistry.AMYTHEST_ORE) {
            return Mth.nextInt(rand, 3, 7);
        }
        return 0;
    }
}
