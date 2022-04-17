package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.util.IDragonProjectile;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityDragonLightningCharge  extends Fireball implements IDragonProjectile {

    public int ticksInAir;

    public EntityDragonLightningCharge(EntityType type, Level worldIn) {
        super(type, worldIn);

    }

    public EntityDragonLightningCharge(FMLPlayMessages.SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.LIGHTNING_DRAGON_CHARGE, worldIn);
    }

    public EntityDragonLightningCharge(EntityType type, Level worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(type, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
        double d0 = Mth.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.xPower = accelX / d0 * 0.07D;
        this.yPower = accelY / d0 * 0.07D;
        this.zPower = accelZ / d0 * 0.07D;
    }

    public EntityDragonLightningCharge(EntityType type, Level worldIn, EntityDragonBase shooter, double accelX, double accelY, double accelZ) {
        super(type, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = Mth.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.xPower = accelX / d0 * 0.07D;
        this.yPower = accelY / d0 * 0.07D;
        this.zPower = accelZ / d0 * 0.07D;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public void tick() {
        Entity shootingEntity = this.getOwner();
        this.clearFire();
        if (this.level.isClientSide || (shootingEntity == null || shootingEntity.isAlive()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();
            this.clearFire();

            ++this.ticksInAir;
            Vec3 Vector3d = this.getDeltaMovement();
            HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitMob);

            if (raytraceresult != null) {
                this.onHit(raytraceresult);
            }

            double d0 = this.getX() + Vector3d.x;
            double d1 = this.getY() + Vector3d.y;
            double d2 = this.getZ() + Vector3d.z;
            float f = Mth.sqrt(getHorizontalDistanceSqr(Vector3d));
            this.yRot = (float) (Mth.atan2(Vector3d.x, Vector3d.z) * (double) (180F / (float) Math.PI));
            for (this.xRot = (float) (Mth.atan2(Vector3d.y, f) * (double) (180F / (float) Math.PI)); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
            }
            while (this.xRot - this.xRotO >= 180.0F) {
                this.xRotO += 360.0F;
            }

            while (this.yRot - this.yRotO < -180.0F) {
                this.yRotO -= 360.0F;
            }

            while (this.yRot - this.yRotO >= 180.0F) {
                this.yRotO += 360.0F;
            }

            this.xRot = Mth.lerp(0.2F, this.xRotO, this.xRot);
            this.yRot = Mth.lerp(0.2F, this.yRotO, this.yRot);
            float f1 = 0.99F;
            float f2 = 0.06F;


            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.level.addParticle(ParticleTypes.BUBBLE, this.getX() - this.getDeltaMovement().x * 0.25D, this.getY() - this.getDeltaMovement().y * 0.25D, this.getZ() - this.getDeltaMovement().z * 0.25D, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
                }

                f = 0.8F;
            }
            this.setPos(d0, d1, d2);
            this.setPos(this.getX(), this.getY(), this.getZ());
        }
    }

    protected boolean canHitMob(Entity hitMob) {
        Entity shooter = getOwner();
        return hitMob != this && super.canHitEntity(hitMob) && !(shooter == null || hitMob.isAlliedTo(shooter)) && !(hitMob instanceof EntityDragonPart);
    }

    @Override
    protected void onHit(HitResult movingObject) {
        Entity shootingEntity = this.getOwner();
        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        if (!this.level.isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity != null && entity instanceof IDragonProjectile) {
                    return;
                }
                if (shootingEntity != null && shootingEntity instanceof EntityDragonBase) {
                    EntityDragonBase dragon = (EntityDragonBase) shootingEntity;
                    if (dragon.isAlliedTo(entity) || dragon.is(entity) || dragon.isPart(entity)) {
                        return;
                    }
                }
                if (entity == null || !(entity instanceof IDragonProjectile) && entity != shootingEntity && shootingEntity instanceof EntityDragonBase) {
                    EntityDragonBase dragon = (EntityDragonBase) shootingEntity;
                    if (shootingEntity != null && (entity == shootingEntity || (entity instanceof TamableAnimal && ((EntityDragonBase) shootingEntity).isOwnedBy(((EntityDragonBase) shootingEntity).getOwner())))) {
                        return;
                    }
                    if (dragon != null) {
                        dragon.randomizeAttacks();
                    }
                    this.remove();
                }
                if (entity != null && !(entity instanceof IDragonProjectile) && !entity.is(shootingEntity)) {
                    if (shootingEntity != null && (entity.is(shootingEntity) || (shootingEntity instanceof EntityDragonBase & entity instanceof TamableAnimal && ((EntityDragonBase) shootingEntity).getOwner() == ((TamableAnimal) entity).getOwner()))) {
                        return;
                    }
                    if (shootingEntity != null && shootingEntity instanceof EntityDragonBase) {
                        float damageAmount = (float) IafConfig.dragonAttackDamageLightning * ((EntityDragonBase) shootingEntity).getDragonStage();
                        entity.hurt(IafDamageRegistry.DRAGON_LIGHTNING, damageAmount);
                        if (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() == 0) {
                            ((EntityDragonBase) shootingEntity).randomizeAttacks();
                        }
                    }
                    if(shootingEntity instanceof LivingEntity){
                        this.doEnchantDamageEffects((LivingEntity)shootingEntity, entity);
                    }
                    this.remove();
                }
            }
            if(movingObject.getType() != HitResult.Type.MISS){
                if (shootingEntity instanceof EntityDragonBase && IafConfig.dragonGriefing != 2) {
                    IafDragonDestructionManager.destroyAreaLightningCharge(level, new BlockPos(this.getX(), this.getY(), this.getZ()), ((EntityDragonBase) shootingEntity));
                }
                this.remove();
            }
        }

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public float getPickRadius() {
        return 0F;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}