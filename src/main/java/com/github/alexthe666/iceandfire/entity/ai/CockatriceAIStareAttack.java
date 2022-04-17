package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class CockatriceAIStareAttack extends Goal {
    private final EntityCockatrice entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    private int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private BlockPos target = null;
    private int walkingTime = -1;
    private float prevYaw;

    public CockatriceAIStareAttack(EntityCockatrice cockatrice, double speedAmplifier, int delay, float maxDistance) {
        this.entity = cockatrice;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public static boolean isEntityLookingAt(LivingEntity looker, LivingEntity seen, double degree) {
        Vec3 Vector3d = looker.getViewVector(1.0F).normalize();
        Vec3 Vector3d1 = new Vec3(seen.getX() - looker.getX(), seen.getBoundingBox().minY + (double) seen.getEyeHeight() - (looker.getY() + (double) looker.getEyeHeight()), seen.getZ() - looker.getZ());
        double d0 = Vector3d1.length();
        Vector3d1 = Vector3d1.normalize();
        double d1 = Vector3d.dot(Vector3d1);
        return d1 > 1.0D - degree / d0 && !looker.isSpectator();
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    public boolean canUse() {
        return this.entity.getTarget() != null;
    }

    public boolean canContinueToUse() {
        return this.canUse();
    }

    public void stop() {
        super.stop();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.stopUsingItem();
        this.entity.getNavigation().stop();
        target = null;
    }

    public void tick() {
        LivingEntity LivingEntity = this.entity.getTarget();
        if (LivingEntity != null) {

            if (EntityGorgon.isStoneMob(LivingEntity) || !LivingEntity.isAlive()) {
                entity.setTarget(null);
                this.entity.setTargetedEntity(0);
                stop();
                return;
            }
            if (!isEntityLookingAt(LivingEntity, entity, EntityCockatrice.VIEW_RADIUS) || (LivingEntity.xo != entity.getX() || LivingEntity.yo != entity.getY() || LivingEntity.zo != entity.getZ())) {
                this.entity.getNavigation().stop();
                this.prevYaw = LivingEntity.yRot;
                BlockPos pos = DragonUtils.getBlockInTargetsViewCockatrice(this.entity, LivingEntity);
                if (target == null || pos.distSqr(target) > 4) {
                    target = pos;
                }
            }
            this.entity.setTargetedEntity(LivingEntity.getId());

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
            if (target != null) {
                if (this.entity.distanceToSqr(target.getX(), target.getY(), target.getZ()) > 16 && !isEntityLookingAt(LivingEntity, entity, EntityCockatrice.VIEW_RADIUS)) {
                    this.entity.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), moveSpeedAmp);
                }

            }
            this.entity.getLookControl().setLookAt(LivingEntity.getX(), LivingEntity.getY() + (double) LivingEntity.getEyeHeight(), LivingEntity.getZ(), (float) this.entity.getMaxHeadYRot(), (float) this.entity.getMaxHeadXRot());
        }
    }

}