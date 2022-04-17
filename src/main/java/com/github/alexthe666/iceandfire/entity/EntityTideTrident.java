package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.lang.reflect.Field;

public class EntityTideTrident extends ThrownTrident {


    public EntityTideTrident(EntityType type, Level worldIn) {
        super(type, worldIn);
        tridentItem = new ItemStack(IafItemRegistry.TIDE_TRIDENT);
    }

    public EntityTideTrident(Level worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        this(IafEntityRegistry.TIDE_TRIDENT, worldIn);
        this.setPos(thrower.getX(), thrower.getEyeY() - (double)0.1F, thrower.getZ());
        this.setOwner(thrower);
        tridentItem = thrownStackIn;
        this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(thrownStackIn));
        this.entityData.set(ID_FOIL, thrownStackIn.hasFoil());
    }

    public EntityTideTrident(FMLPlayMessages.SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.TIDE_TRIDENT, worldIn);
    }

    public EntityTideTrident(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        tridentItem = new ItemStack(IafItemRegistry.TIDE_TRIDENT);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 12.0F;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entity;
            f += EnchantmentHelper.getDamageBonus(this.tridentItem, livingentity.getMobType());
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource = DamageSource.trident(this, entity1 == null ? this : entity1);
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        if (this.level instanceof ServerLevel && this.level.isThundering() && EnchantmentHelper.hasChanneling(this.tridentItem)) {
            BlockPos blockpos = entity.blockPosition();
            if (this.level.canSeeSky(blockpos)) {
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
                lightningboltentity.moveTo(Vec3.atCenterOf(blockpos));
                lightningboltentity.setCause(entity1 instanceof ServerPlayer ? (ServerPlayer)entity1 : null);
                this.level.addFreshEntity(lightningboltentity);
                soundevent = SoundEvents.TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundevent, f1, 1.0F);
    }

    protected ItemStack getPickupItem() {
        return this.tridentItem.getItem() == Items.TRIDENT ? new ItemStack(IafItemRegistry.TIDE_TRIDENT) : this.tridentItem.copy();
    }

}