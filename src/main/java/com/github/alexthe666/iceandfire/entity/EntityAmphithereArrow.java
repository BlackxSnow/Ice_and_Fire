package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityAmphithereArrow extends AbstractArrow {

    public EntityAmphithereArrow(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setBaseDamage(2.5F);
    }

    public EntityAmphithereArrow(EntityType type, Level worldIn, double x, double y, double z) {
        this(type, worldIn);
        this.setPos(x, y, z);
        this.setBaseDamage(2.5F);
    }

    public EntityAmphithereArrow(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        this(IafEntityRegistry.AMPHITHERE_ARROW, world);
    }

    public EntityAmphithereArrow(EntityType type, LivingEntity shooter, Level worldIn) {
        super(type, shooter, worldIn);
        this.setBaseDamage(2.5F);
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        if ((tickCount == 1 || this.tickCount % 70 == 0) && !this.inGround && !this.onGround) {
            this.playSound(IafSoundRegistry.AMPHITHERE_GUST, 1, 1);
        }
        if (level.isClientSide && !this.inGround) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            double xRatio = this.getDeltaMovement().x * this.getBbWidth();
            double zRatio = this.getDeltaMovement().z * this.getBbWidth();
            this.level.addParticle(ParticleTypes.CLOUD, this.getX() + xRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d0 * 10.0D, this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()) - d1 * 10.0D, this.getZ() + zRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d2 * 10.0D, d0, d1, d2);

        }
    }

    protected void doPostHurtEffects(LivingEntity living) {
        if (living instanceof Player) {
            this.damageShield((Player) living, (float) this.getBaseDamage());
        }
        living.hasImpulse = true;
        double xRatio = this.getDeltaMovement().x;
        double zRatio = this.getDeltaMovement().z;
        float strength = -1.4F;
        float f = Mth.sqrt(xRatio * xRatio + zRatio * zRatio);
        living.setDeltaMovement(living.getDeltaMovement().multiply(0.5D, 1, 0.5D).subtract(xRatio / (double) f * (double) strength, 0, zRatio / (double) f * (double) strength).add(0, 0.6, 0));
        spawnExplosionParticle();
    }

    public void spawnExplosionParticle() {
        if (this.level.isClientSide) {
            for (int height = 0; height < 1 + random.nextInt(2); height++) {
                for (int i = 0; i < 20; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d3 = 10.0D;
                    double xRatio = this.getDeltaMovement().x * this.getBbWidth();
                    double zRatio = this.getDeltaMovement().z * this.getBbWidth();
                    this.level.addParticle(ParticleTypes.CLOUD, this.getX() + xRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d0 * 10.0D, this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()) - d1 * 10.0D, this.getZ() + zRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d2 * 10.0D, d0, d1, d2);
                }
            }
        } else {
            this.level.broadcastEntityEvent(this, (byte) 20);
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 20) {
            this.spawnExplosionParticle();
        } else {
            super.handleEntityEvent(id);
        }
    }

    protected void damageShield(Player player, float damage) {
        if (damage >= 3.0F && player.getUseItem().getItem().isShield(player.getUseItem(), player)) {
            ItemStack copyBeforeUse = player.getUseItem().copy();
            int i = 1 + Mth.floor(damage);
            player.getUseItem().hurtAndBreak(i, player, (playerSheild) -> {
                playerSheild.broadcastBreakEvent(playerSheild.getUsedItemHand());
            });

            if (player.getUseItem().isEmpty()) {
                InteractionHand Hand = player.getUsedItemHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, Hand);

                if (Hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                player.stopUsingItem();
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(IafItemRegistry.AMPHITHERE_ARROW);
    }
}
