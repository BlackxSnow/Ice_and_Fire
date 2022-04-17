package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityPixieCharge extends Fireball {

    public int ticksInAir;
    private float[] rgb;

    public EntityPixieCharge(EntityType t, Level worldIn) {
        super(t, worldIn);
        rgb = EntityPixie.PARTICLE_RGB[random.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }


    public EntityPixieCharge(FMLPlayMessages.SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.PIXIE_CHARGE, worldIn);
    }

    public EntityPixieCharge(EntityType t, Level worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
        double d0 = Mth.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.xPower = accelX / d0 * 0.07D;
        this.yPower = accelY / d0 * 0.07D;
        this.zPower = accelZ / d0 * 0.07D;
        rgb = EntityPixie.PARTICLE_RGB[random.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    public EntityPixieCharge(EntityType t, Level worldIn, Player shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = Mth.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.xPower = accelX / d0 * 0.07D;
        this.yPower = accelY / d0 * 0.07D;
        this.zPower = accelZ / d0 * 0.07D;
        rgb = EntityPixie.PARTICLE_RGB[random.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public void tick() {
        Entity shootingEntity = this.getOwner();
        if (this.level.isClientSide) {
            for (int i = 0; i < 5; ++i) {
                IceAndFire.PROXY.spawnParticle("if_pixie", this.getX() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), rgb[0], rgb[1], rgb[2]);
            }
        }
        this.clearFire();
        if (this.tickCount > 30) {
            this.remove();
        }
        if (this.level.isClientSide || (shootingEntity == null || shootingEntity.isAlive()) && this.level.hasChunkAt(this.blockPosition())) {
            if (this.level.isClientSide || (shootingEntity == null || !shootingEntity.removed) && this.level.hasChunkAt(this.blockPosition())) {
                if (this.shouldBurn()) {
                    this.setSecondsOnFire(1);
                }

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


                this.setDeltaMovement(Vector3d.add(this.xPower, this.yPower, this.zPower).scale(f));
                this.setPos(d0, d1, d2);
            } else {
                this.remove();
            }
            this.xPower *= 0.95F;
            this.yPower *= 0.95F;
            this.zPower *= 0.95F;
            this.push(this.xPower, this.yPower, this.zPower);
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

    @Override
    protected void onHit(HitResult movingObject) {
        boolean flag = false;
        Entity shootingEntity = this.getOwner();
        if (!this.level.isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY && !((EntityHitResult) movingObject).getEntity().is(shootingEntity)) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();
                if (shootingEntity != null && shootingEntity.equals(entity)) {
                    flag = true;
                } else {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                        entity.hurt(DamageSource.indirectMagic(shootingEntity, null), 5.0F);
                    }
                    if (this.level.isClientSide) {
                        for (int i = 0; i < 20; ++i) {
                            IceAndFire.PROXY.spawnParticle("if_pixie", this.getX() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), rgb[0], rgb[1], rgb[2]);
                        }
                    }
                    if (shootingEntity == null || !(shootingEntity instanceof Player) || !((Player) shootingEntity).isCreative()) {
                        if (random.nextInt(3) == 0) {
                            this.spawnAtLocation(new ItemStack(IafItemRegistry.PIXIE_DUST, 1), 0.45F);
                        }
                    }
                }
                if (!flag && this.tickCount > 4) {
                    this.remove();
                }
            }

        }
    }
}