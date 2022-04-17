package com.github.alexthe666.iceandfire.block;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockDragonScales extends Block implements IDragonProof {
    EnumDragonEgg type;

    public BlockDragonScales(String name, EnumDragonEgg type) {
        super(
    		Properties
    			.of(Material.STONE)
    			.dynamicShape()
    			.strength(30F, 500)
    			.harvestTool(ToolType.PICKAXE)
    			.harvestLevel(2)
    			.sound(SoundType.STONE)
    			.requiresCorrectToolForDrops()
		);

        this.setRegistryName("iceandfire:" + name);
        this.type = type;
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("dragon." + type.toString().toLowerCase()).withStyle(type.color));
    }
}
