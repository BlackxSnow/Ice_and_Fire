package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityDragonArrow extends AbstractArrow {

    public EntityDragonArrow(EntityType typeIn, Level worldIn) {
        super(typeIn, worldIn);
        this.setBaseDamage(10);
    }

    public EntityDragonArrow(EntityType typeIn, double x, double y, double z, Level world) {
        super(typeIn, x, y, z, world);
        this.setBaseDamage(10);
    }

    public EntityDragonArrow(FMLPlayMessages.SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.DRAGON_ARROW, worldIn);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityDragonArrow(EntityType typeIn, LivingEntity shooter, Level worldIn) {
        super(typeIn, shooter, worldIn);
        this.setBaseDamage(10.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tagCompound) {
        super.addAdditionalSaveData(tagCompound);
        tagCompound.putDouble("damage", 10);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tagCompund) {
        super.readAdditionalSaveData(tagCompund);
        this.setBaseDamage(tagCompund.getDouble("damage"));
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(IafItemRegistry.DRAGONBONE_ARROW);
    }

}