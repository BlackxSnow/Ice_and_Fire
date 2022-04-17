package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianFeather;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemStymphalianFeatherBundle extends Item {

    public ItemStymphalianFeatherBundle() {
        super(new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "stymphalian_feather_bundle");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand hand) {
        ItemStack itemStackIn = player.getItemInHand(hand);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(this, 15);
        player.playSound(SoundEvents.EGG_THROW, 1, 1);
        float rotation = player.yHeadRot;
        for (int i = 0; i < 8; i++) {
            EntityStymphalianFeather feather = new EntityStymphalianFeather(IafEntityRegistry.STYMPHALIAN_FEATHER, worldIn, player);
            rotation += 45;
            feather.shootFromRotation(player,0, rotation, 0.0F, 1.5F, 1.0F);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(feather);
            }
        }
        if (!player.isCreative()) {
            itemStackIn.shrink(1);
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, itemStackIn);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.stymphalian_feather_bundle.desc_0").withStyle(ChatFormatting.GRAY));
    }
}