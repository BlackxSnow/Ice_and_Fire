package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonArrow;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class ItemDragonArrow extends ArrowItem {
    public ItemDragonArrow() {
        super(new Properties().tab(IceAndFire.TAB_ITEMS));
        this.setRegistryName("iceandfire:dragonbone_arrow");
    }

    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        EntityDragonArrow arrowentity = new EntityDragonArrow(IafEntityRegistry.DRAGON_ARROW, shooter, worldIn);
        return arrowentity;
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.world.entity.player.Player player) {
        int enchant = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow);
        return enchant <= 0 ? false : this.getClass() == ItemDragonArrow.class;
    }
}
