package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemHippogryphSword extends SwordItem {

    public ItemHippogryphSword() {
        super(IafItemRegistry.HIPPOGRYPH_SWORD_TOOL_MATERIAL, 3, -2.4F, new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "hippogryph_sword");
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity targetEntity, LivingEntity attacker) {
        float f = (float) attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(attacker) * f;
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            for (LivingEntity LivingEntity : attacker.level.getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                if (LivingEntity != player && LivingEntity != targetEntity && !attacker.isAlliedTo(LivingEntity) && attacker.distanceToSqr(LivingEntity) < 9.0D) {
                    LivingEntity.knockback(0.4F, Mth.sin(attacker.yRot * 0.017453292F), -Mth.cos(attacker.yRot * 0.017453292F));
                    LivingEntity.hurt(DamageSource.playerAttack(player), f3);
                }
            }
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
            player.sweepAttack();
        }
        return super.hurtEnemy(stack, targetEntity, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.hippogryph_sword.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.hippogryph_sword.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
