package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IDragonProjectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySeaSerpentBubbles extends Fireball implements IDragonProjectile {

    private int ticksInAir;

    public EntitySeaSerpentBubbles(EntityType t, Level worldIn) {
        super(t, worldIn);
    }

    public EntitySeaSerpentBubbles(EntityType t, Level worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
    }

    @Override
    public boolean isPickable() {
        return false;
    }


    public EntitySeaSerpentBubbles(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        this(IafEntityRegistry.SEA_SERPENT_BUBBLES, world);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntitySeaSerpentBubbles(EntityType t, Level worldIn, EntitySeaSerpent shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = Mth.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.xPower = accelX / d0 * 0.1D;
        this.yPower = accelY / d0 * 0.1D;
        this.zPower = accelZ / d0 * 0.1D;
    }

    protected boolean shouldBurn() {
        return false;
    }

    public void tick() {
        super.tick();
        Entity shootingEntity = this.getOwner();
        if(this.tickCount > 400 ){
            this.remove();
        }
        autoTarget();
        this.xPower *= 0.95F;
        this.yPower *= 0.95F;
        this.zPower *= 0.95F;
        this.push(this.xPower, this.yPower, this.zPower);

        if (this.level.isClientSide || (shootingEntity == null || !shootingEntity.isAlive()) && this.level.hasChunkAt(this.blockPosition())) {
            if (this.level.isClientSide || (shootingEntity == null || !shootingEntity.removed) && this.level.hasChunkAt(this.blockPosition())) {
                ++this.ticksInAir;
                HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
                if (raytraceresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.onHit(raytraceresult);
                }

                Vec3 Vector3d = this.getDeltaMovement();
                double d0 = this.getX() + Vector3d.x;
                double d1 = this.getY() + Vector3d.y;
                double d2 = this.getZ() + Vector3d.z;
                ProjectileUtil.rotateTowardsMovement(this, 0.2F);
                float f = this.getInertia();
                if (this.level.isClientSide) {
                    for (int i = 0; i < 3; ++i) {
                        IceAndFire.PROXY.spawnParticle("serpent_bubble", this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() - 0.5D, this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, 0, 0, 0);
                    }
                }

                this.setDeltaMovement(Vector3d.add(this.xPower, this.yPower, this.zPower).scale((double)f));
                this.setPos(d0, d1, d2);
            } else {
                this.remove();
            }
            ++this.ticksInAir;
            Vec3 Vector3d = this.getDeltaMovement();
            HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);

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
            this.setPos(d0, d1, d2);
            this.setPos(this.getX(), this.getY(), this.getZ());
        }
        this.setPos(this.getX(), this.getY(), this.getZ());
        if(this.tickCount > 20 && !isInWaterOrRain()){
            this.remove();
        }
    }

    protected boolean canHitEntity(Entity entityIn) {
        return super.canHitEntity(entityIn) && !(entityIn instanceof EntityMutlipartPart) && !(entityIn instanceof EntitySeaSerpentBubbles);
    }


    public void autoTarget() {
        if(!level.isClientSide){
            Entity shootingEntity = this.getOwner();
            if (shootingEntity instanceof EntitySeaSerpent && ((EntitySeaSerpent) shootingEntity).getTarget() != null) {
                Entity target = ((EntitySeaSerpent) shootingEntity).getTarget();
                double d2 = target.getX() - this.getX();
                double d3 = target.getY() - this.getY();
                double d4 = target.getZ() - this.getZ();
                double d0 = Mth.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
                this.xPower = d2 / d0 * 0.1D;
                this.yPower = d3 / d0 * 0.1D;
                this.zPower = d4 / d0 * 0.1D;
            }else if(tickCount > 20){
                this.remove();
            }
        }
    }

    public boolean handleWaterMovement() {
        return true;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.BUBBLE;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public float getPickRadius() {
        return 0F;
    }

    @Override
    protected void onHit(HitResult movingObject) {
        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        if (!this.level.isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity != null && entity instanceof EntitSlowPart) {
                    return;
                }
                Entity shootingEntity = this.getOwner();
                if (shootingEntity != null && shootingEntity instanceof EntitySeaSerpent) {
                    EntitySeaSerpent dragon = (EntitySeaSerpent) shootingEntity;
                    if (dragon.isAlliedTo(entity) || dragon.is(entity)) {
                        return;
                    }
                    entity.hurt(DamageSource.mobAttack(dragon), 6.0F);

                }
            }
        }
    }

}
