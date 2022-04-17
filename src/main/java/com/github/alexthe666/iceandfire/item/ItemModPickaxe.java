package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.citadel.server.item.CustomToolMaterial;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;

import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemModPickaxe extends PickaxeItem {

    private final CustomToolMaterial toolMaterial;

    public ItemModPickaxe(CustomToolMaterial toolmaterial, String gameName) {
        super(toolmaterial, 1, -2.8F, new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.toolMaterial = toolmaterial;
        this.setRegistryName(IceAndFire.MODID, gameName);
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND && this.toolMaterial instanceof DragonsteelToolMaterial ? this.bakeDragonsteel() : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    private Multimap<Attribute, AttributeModifier> dragonsteelModifiers;
    private Multimap<Attribute, AttributeModifier> bakeDragonsteel() {
        if(toolMaterial.getAttackDamageBonus() != IafConfig.dragonsteelBaseDamage || dragonsteelModifiers == null){
            ImmutableMultimap.Builder<Attribute, AttributeModifier> lvt_5_1_ = ImmutableMultimap.builder();
            lvt_5_1_.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double) IafConfig.dragonsteelBaseDamage - 1F + 1F, AttributeModifier.Operation.ADDITION));
            lvt_5_1_.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)-2.8F, AttributeModifier.Operation.ADDITION));
            this.dragonsteelModifiers = lvt_5_1_.build();
            return this.dragonsteelModifiers;
        }else{
            return dragonsteelModifiers;
        }
    }

    public float getAttackDamage() {
        return this.toolMaterial instanceof DragonsteelToolMaterial ? (float) IafConfig.dragonsteelBaseDamage : super.getAttackDamage();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (toolMaterial == IafItemRegistry.SILVER_TOOL_MATERIAL) {
            if (target.getMobType() == MobType.UNDEAD) {
                target.hurt(DamageSource.MAGIC, getAttackDamage() + 3.0F);
            }
        }
        if (this.toolMaterial == IafItemRegistry.MYRMEX_CHITIN_TOOL_MATERIAL) {
            if (target.getMobType() != MobType.ARTHROPOD) {
                target.hurt(DamageSource.GENERIC, getAttackDamage() + 5.0F);
            }
            if (target instanceof EntityDeathWorm) {
                target.hurt(DamageSource.GENERIC, getAttackDamage() + 5.0F);
            }
        }
        if (toolMaterial == IafItemRegistry.DRAGONSTEEL_FIRE_TOOL_MATERIAL && IafConfig.dragonWeaponFireAbility) {
            target.setSecondsOnFire(15);
            target.knockback( 1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        if (toolMaterial == IafItemRegistry.DRAGONSTEEL_ICE_TOOL_MATERIAL && IafConfig.dragonWeaponIceAbility) {
            FrozenProperties.setFrozenFor(target, 300);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 2));
            target.knockback( 1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        if (toolMaterial == IafItemRegistry.DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL && IafConfig.dragonWeaponLightningAbility) {
            boolean flag = true;
            if(attacker instanceof Player){
                if(((Player)attacker).attackAnim > 0.2){
                    flag = false;
                }
            }
            if(!attacker.level.isClientSide && flag){
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(target.level);
                lightningboltentity.moveTo(target.position());
                if(!target.level.isClientSide){
                    target.level.addFreshEntity(lightningboltentity);
                }
            }
            target.knockback( 1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (toolMaterial == IafItemRegistry.SILVER_TOOL_MATERIAL) {
            tooltip.add(new TranslatableComponent("silvertools.hurt").withStyle(ChatFormatting.GREEN));
        }
        if (toolMaterial == IafItemRegistry.MYRMEX_CHITIN_TOOL_MATERIAL) {
            tooltip.add(new TranslatableComponent("myrmextools.hurt").withStyle(ChatFormatting.GREEN));
        }
        if (toolMaterial == IafItemRegistry.DRAGONSTEEL_FIRE_TOOL_MATERIAL) {
            tooltip.add(new TranslatableComponent("dragon_sword_fire.hurt2").withStyle(ChatFormatting.DARK_RED));
        }
        if (toolMaterial == IafItemRegistry.DRAGONSTEEL_ICE_TOOL_MATERIAL) {
            tooltip.add(new TranslatableComponent("dragon_sword_ice.hurt2").withStyle(ChatFormatting.AQUA));
        }
        if (toolMaterial == IafItemRegistry.DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL) {
            tooltip.add(new TranslatableComponent("dragon_sword_lightning.hurt2").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
