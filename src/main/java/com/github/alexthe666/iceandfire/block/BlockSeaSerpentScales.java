package com.github.alexthe666.iceandfire.block;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockSeaSerpentScales extends Block {
    ChatFormatting color;
    String name;

    public BlockSeaSerpentScales(String name, ChatFormatting color) {
        super(
    		Properties
    			.of(Material.STONE)
    			.strength(30F, 500F)
    			.sound(SoundType.STONE)
    			.harvestTool(ToolType.PICKAXE)
    			.harvestLevel(2)
    			.requiresCorrectToolForDrops()
		);

        this.color = color;
        this.name = name;
        this.setRegistryName(IceAndFire.MODID, "sea_serpent_scale_block_" + name);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("sea_serpent." + name).withStyle(color));
    }
}
