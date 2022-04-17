package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemDragonScales extends Item {
    EnumDragonEgg type;

    public ItemDragonScales(String name, EnumDragonEgg type) {
        super(new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.type = type;
        this.setRegistryName(IceAndFire.MODID, name);
    }

    public String getDescriptionId() {
        return "item.iceandfire.dragonscales";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("dragon." + type.toString().toLowerCase()).withStyle(type.color));
    }

}
