package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityTideTrident;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemTideTrident extends TridentItem {

    public ItemTideTrident() {
        super(IceAndFire.PROXY.setupISTER(new Item.Properties().tab(IceAndFire.TAB_ITEMS).durability(400)));
        this.setRegistryName(IceAndFire.MODID, "tide_trident");
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player lvt_5_1_ = (Player)entityLiving;
            int lvt_6_1_ = this.getUseDuration(stack) - timeLeft;
            if (lvt_6_1_ >= 10) {
                int lvt_7_1_ = EnchantmentHelper.getRiptide(stack);
                if (lvt_7_1_ <= 0 || lvt_5_1_.isInWaterOrRain()) {
                    if (!worldIn.isClientSide) {
                        stack.hurtAndBreak(1, lvt_5_1_, (player) -> {
                            player.broadcastBreakEvent(entityLiving.getUsedItemHand());
                        });
                        if (lvt_7_1_ == 0) {
                            EntityTideTrident lvt_8_1_ = new EntityTideTrident(worldIn, lvt_5_1_, stack);
                            lvt_8_1_.shootFromRotation(lvt_5_1_, lvt_5_1_.xRot, lvt_5_1_.yRot, 0.0F, 2.5F + (float)lvt_7_1_ * 0.5F, 1.0F);
                            if (lvt_5_1_.abilities.instabuild) {
                                lvt_8_1_.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            worldIn.addFreshEntity(lvt_8_1_);
                            worldIn.playSound((Player)null, lvt_8_1_, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!lvt_5_1_.abilities.instabuild) {
                                lvt_5_1_.inventory.removeItem(stack);
                            }
                        }
                    }

                    lvt_5_1_.awardStat(Stats.ITEM_USED.get(this));
                    if (lvt_7_1_ > 0) {
                        float lvt_8_2_ = lvt_5_1_.yRot;
                        float lvt_9_1_ = lvt_5_1_.xRot;
                        float lvt_10_1_ = -Mth.sin(lvt_8_2_ * 0.017453292F) * Mth.cos(lvt_9_1_ * 0.017453292F);
                        float lvt_11_1_ = -Mth.sin(lvt_9_1_ * 0.017453292F);
                        float lvt_12_1_ = Mth.cos(lvt_8_2_ * 0.017453292F) * Mth.cos(lvt_9_1_ * 0.017453292F);
                        float lvt_13_1_ = Mth.sqrt(lvt_10_1_ * lvt_10_1_ + lvt_11_1_ * lvt_11_1_ + lvt_12_1_ * lvt_12_1_);
                        float lvt_14_1_ = 3.0F * ((1.0F + (float)lvt_7_1_) / 4.0F);
                        lvt_10_1_ *= lvt_14_1_ / lvt_13_1_;
                        lvt_11_1_ *= lvt_14_1_ / lvt_13_1_;
                        lvt_12_1_ *= lvt_14_1_ / lvt_13_1_;
                        lvt_5_1_.push((double)lvt_10_1_, (double)lvt_11_1_, (double)lvt_12_1_);
                        lvt_5_1_.startAutoSpinAttack(20);
                        if (lvt_5_1_.isOnGround()) {
                            float lvt_15_1_ = 1.1999999F;
                            lvt_5_1_.move(MoverType.SELF, new Vec3(0.0D, 1.1999999284744263D, 0.0D));
                        }

                        SoundEvent lvt_15_4_;
                        if (lvt_7_1_ >= 3) {
                            lvt_15_4_ = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (lvt_7_1_ == 2) {
                            lvt_15_4_ = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            lvt_15_4_ = SoundEvents.TRIDENT_RIPTIDE_1;
                        }

                        worldIn.playSound((Player)null, lvt_5_1_, lvt_15_4_, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }


    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)12.0D, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)-2.9F, AttributeModifier.Operation.ADDITION));
        }

        return builder.build();
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(new TranslatableComponent("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.tide_trident.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.tide_trident.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
