package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemDragonEgg extends Item {
    public EnumDragonEgg type;

    public ItemDragonEgg(String name, EnumDragonEgg type) {
        super(new Item.Properties().tab(IceAndFire.TAB_ITEMS).stacksTo(1));
        this.type = type;
        this.setRegistryName(IceAndFire.MODID, name);
    }

    public String getDescriptionId() {
        return "item.iceandfire.dragonegg";
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Level world, Player player) {
        itemStack.setTag(new CompoundTag());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("dragon." + type.toString().toLowerCase()).withStyle(type.color));
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getPlayer().getItemInHand(context.getHand());
        BlockPos offset = context.getClickedPos().relative(context.getClickedFace());
        EntityDragonEgg egg = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG, context.getLevel());
        egg.setEggType(type);
        egg.moveTo(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        egg.onPlayerPlace(context.getPlayer());
        if (itemstack.hasCustomHoverName()) {
            egg.setCustomName(itemstack.getHoverName());
        }
        if (!context.getLevel().isClientSide) {
            context.getLevel().addFreshEntity(egg);
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
