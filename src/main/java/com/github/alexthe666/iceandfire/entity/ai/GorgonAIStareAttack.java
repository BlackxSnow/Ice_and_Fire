package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class GorgonAIStareAttack extends Goal {
    private final EntityGorgon entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    private int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public GorgonAIStareAttack(EntityGorgon gorgon, double speedAmplifier, int delay, float maxDistance) {
        this.entity = gorgon;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    public boolean canUse() {
        return this.entity.getTarget() != null;
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.entity.getNavigation().isDone());
    }

    public void stop() {
        super.stop();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.stopUsingItem();
    }

    public void tick() {
        LivingEntity LivingEntity = this.entity.getTarget();

        if (LivingEntity != null) {
            if (EntityGorgon.isStoneMob(LivingEntity)) {
                entity.setTarget(null);
                stop();
                return;
            }
            this.entity.getLookControl().setLookAt(LivingEntity.getX(), LivingEntity.getY() + (double) LivingEntity.getEyeHeight(), LivingEntity.getZ(), (float) this.entity.getMaxHeadYRot(), (float) this.entity.getMaxHeadXRot());

            double d0 = this.entity.distanceToSqr(LivingEntity.getX(), LivingEntity.getBoundingBox().minY, LivingEntity.getZ());
            boolean flag = this.entity.getSensing().canSee(LivingEntity);
            boolean flag1 = this.seeTime > 0;

            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
                this.entity.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.entity.getNavigation().moveTo(LivingEntity, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double) this.entity.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.entity.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double) (this.maxAttackDistance * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double) (this.maxAttackDistance * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.entity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.getLookControl().setLookAt(LivingEntity.getX(), LivingEntity.getY() + (double) LivingEntity.getEyeHeight(), LivingEntity.getZ(), (float) this.entity.getMaxHeadYRot(), (float) this.entity.getMaxHeadXRot());
                this.entity.forcePreyToLook(LivingEntity);
            } else {
                this.entity.getLookControl().setLookAt(LivingEntity, 30.0F, 30.0F);
                this.entity.forcePreyToLook(LivingEntity);
            }

        }
    }
}