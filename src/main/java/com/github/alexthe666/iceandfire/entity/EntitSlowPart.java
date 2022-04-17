package com.github.alexthe666.iceandfire.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntitSlowPart extends EntityMutlipartPart{
    public EntitSlowPart(EntityType t, Level world) {
        super(t, world);
    }

    public EntitSlowPart(FMLPlayMessages.SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.SLOW_MULTIPART, worldIn);
    }

    public EntitSlowPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(t, parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    public EntitSlowPart(Entity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntityRegistry.SLOW_MULTIPART, parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected boolean isSlowFollow(){
        return true;
    }
}
