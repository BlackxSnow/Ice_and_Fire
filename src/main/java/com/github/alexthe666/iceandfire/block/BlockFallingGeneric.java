package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockFallingGeneric extends FallingBlock {
    public Item itemBlock;

    public BlockFallingGeneric(Material materialIn, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound) {
        super(
    		BlockBehaviour.Properties
    			.of(materialIn)
				.sound(sound)
				.strength(hardness, resistance)
				.harvestTool(ToolType.get(toolUsed))
				.harvestLevel(toolStrength)
		);

        setRegistryName(IceAndFire.MODID, name);

    }

    @SuppressWarnings("deprecation")
    public BlockFallingGeneric(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean slippery) {
        super(
    		BlockBehaviour.Properties
    			.of(materialIn)
    			.sound(sound)
    			.strength(hardness, resistance)
    			.harvestTool(ToolType.get(toolUsed))
    			.harvestLevel(toolStrength)
    			.friction(0.98F)
		);

        setRegistryName(IceAndFire.MODID, name);
    }

    @OnlyIn(Dist.CLIENT)
    public int getDustColor(BlockState blkst) {
        return -8356741;
    }
}
