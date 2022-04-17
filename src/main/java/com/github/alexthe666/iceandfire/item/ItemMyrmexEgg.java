package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemMyrmexEgg extends Item implements ICustomRendered {

    boolean isJungle;

    public ItemMyrmexEgg(boolean isJungle) {
        super(new Item.Properties().tab(IceAndFire.TAB_ITEMS).stacksTo(1));
        this.isJungle = isJungle;
        this.setRegistryName(IceAndFire.MODID, isJungle ? "myrmex_jungle_egg" : "myrmex_desert_egg");
    }

    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            for (int i = 0; i < 5; i++) {
                ItemStack stack = new ItemStack(this);
                CompoundTag tag = new CompoundTag();
                tag.putInt("EggOrdinal", i);
                stack.setTag(tag);
                items.add(stack);
            }
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        String caste;
        CompoundTag tag = stack.getTag();
        int eggOrdinal = 0;
        if (tag != null) {
            eggOrdinal = tag.getInt("EggOrdinal");
        }
        switch (eggOrdinal) {
            default:
                caste = "worker";
                break;
            case 1:
                caste = "soldier";
                break;
            case 2:
                caste = "royal";
                break;
            case 3:
                caste = "sentinel";
                break;
            case 4:
                caste = "queen";
        }
        if (eggOrdinal == 4) {
            tooltip.add(new TranslatableComponent("myrmex.caste_" + caste + ".name").withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(new TranslatableComponent("myrmex.caste_" + caste + ".name").withStyle(ChatFormatting.GRAY));
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getPlayer().getItemInHand(context.getHand());
        BlockPos offset = context.getClickedPos().relative(context.getClickedFace());
        EntityMyrmexEgg egg = new EntityMyrmexEgg(IafEntityRegistry.MYRMEX_EGG, context.getLevel());
        CompoundTag tag = itemstack.getTag();
        int eggOrdinal = 0;
        if (tag != null) {
            eggOrdinal = tag.getInt("EggOrdinal");
        }
        egg.setJungle(isJungle);
        egg.setMyrmexCaste(eggOrdinal);
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

    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int eggOrdinal = 0;
        if (tag != null) {
            eggOrdinal = tag.getInt("EggOrdinal");
        }
        return super.isFoil(stack) || eggOrdinal == 4;
    }
}
