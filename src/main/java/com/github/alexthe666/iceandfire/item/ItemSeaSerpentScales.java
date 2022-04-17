package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemSeaSerpentScales extends ItemGeneric {

    private ChatFormatting color;
    private String colorName;

    public ItemSeaSerpentScales(String colorName, ChatFormatting color) {
        super("sea_serpent_scales_" + colorName);
        this.color = color;
        this.colorName = colorName;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("sea_serpent." + colorName).withStyle(color));
    }
}
