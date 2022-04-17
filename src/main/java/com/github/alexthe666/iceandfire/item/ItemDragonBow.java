package com.github.alexthe666.iceandfire.item;

import java.util.function.Predicate;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.misc.IafTagRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ItemDragonBow extends BowItem implements ICustomRendered {
    public static final Predicate<ItemStack> DRAGON_ARROWS = (stack) -> {
        Tag<Item> tag = ItemTags.getAllTags().getTag(  IafTagRegistry.DRAGON_ARROWS);
        return stack.getItem().is(tag);
    };

    public ItemDragonBow() {
        super(new Item.Properties().tab(IceAndFire.TAB_ITEMS).durability(584));
        this.setRegistryName(IceAndFire.MODID, "dragonbone_bow");
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return DRAGON_ARROWS;
    }
}
