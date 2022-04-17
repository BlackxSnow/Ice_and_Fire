package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityStymphalianArrow extends AbstractArrow {

    public EntityStymphalianArrow(EntityType t, Level worldIn) {
        super(t, worldIn);
        this.setBaseDamage(3.5F);
    }

    public EntityStymphalianArrow(EntityType t, Level worldIn, double x, double y, double z) {
        this(t, worldIn);
        this.setPos(x, y, z);
        this.setBaseDamage(3.5F);
    }

    public EntityStymphalianArrow(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        this(IafEntityRegistry.STYMPHALIAN_ARROW, world);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityStymphalianArrow(EntityType t, Level worldIn, LivingEntity shooter) {
        super(t, shooter, worldIn);
        this.setBaseDamage(3.5F);
    }

    public void tick() {
        super.tick();
        float sqrt = Mth.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);
        if (sqrt < 0.1F) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.01F, 0));
        }
    }

    protected void damageShield(Player player, float damage) {
        if (damage >= 3.0F && player.getUseItem().getItem().isShield(player.getUseItem(), player)) {
            ItemStack copyBeforeUse = player.getUseItem().copy();
            int i = 1 + Mth.floor(damage);
            player.getUseItem().hurtAndBreak(i, player, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
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

    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(IafItemRegistry.STYMPHALIAN_ARROW);
    }
}
