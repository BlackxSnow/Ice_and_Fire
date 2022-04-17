package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DeathWormAIAttack extends Goal {
    private EntityDeathWorm worm;
    private LivingEntity target;
    private Vec3 leapPos = null;
    private int jumpCooldown = 0;

    public DeathWormAIAttack(EntityDeathWorm worm) {
        this.worm = worm;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        if (this.worm.getTarget() == null || worm.isVehicle() || !worm.isOnGround() && !worm.isInSandStrict() || jumpCooldown > 0) {
            return false;
        } else {
            BlockPos blockpos = this.worm.blockPosition();
            return true;
        }
    }

    public boolean canContinueToUse() {
        double d0 = this.worm.getDeltaMovement().y;
        return worm.getTarget() != null && jumpCooldown > 0 && (!(d0 * d0 < (double) 0.03F) || this.worm.xRot == 0.0F || !(Math.abs(this.worm.xRot) < 10.0F) || !this.worm.isInWater()) && !this.worm.isOnGround();
    }

    public boolean isInterruptable() {
        return false;
    }

    public void start() {
        LivingEntity target = this.worm.getTarget();
        if (target != null) {
            if(worm.isInSand()){
                BlockPos topSand = worm.blockPosition();
                while(worm.level.getBlockState(topSand.above()).is(BlockTags.SAND)){
                    topSand = topSand.above();
                }
                worm.setPos(worm.getX(), topSand.getY() + 0.5F, worm.getZ());
            }
            double distanceXZ = worm.distanceToSqr(target.getX(), worm.getY(), target.getZ());
            if (Math.sqrt(distanceXZ) < 12 && Math.sqrt(distanceXZ) > 2) {
                worm.lookAt(target, 260, 30);
                double smoothX = Mth.clamp(Math.abs(target.getX() - worm.getX()), 0, 1);
                double smoothY = Mth.clamp(Math.abs(target.getY() - worm.getY()), 0, 1);
                double smoothZ = Mth.clamp(Math.abs(target.getZ() - worm.getZ()), 0, 1);
                double d0 = (target.getX() - this.worm.getX()) * 0.2 * smoothX;
                double d1 = Math.signum(target.getY() - this.worm.getY());
                double d2 = (target.getZ() - this.worm.getZ()) * 0.2 * smoothZ;
                float up = (worm.getScale() > 3 ? 0.8F : 0.5F) + worm.getRandom().nextFloat() * 0.5F;
                this.worm.setDeltaMovement(this.worm.getDeltaMovement().add(d0 * 0.3D, up, d2 * 0.3D));
                this.worm.getNavigation().stop();
                this.worm.setWormJumping(20);
                this.jumpCooldown = worm.getRandom().nextInt(32) + 64;
            } else if(distanceXZ > 16F){
                worm.getNavigation().moveTo(target, 1.0F);
            }

        }
    }

    public void stop() {
        this.worm.xRot = 0.0F;
    }

    public void tick() {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        LivingEntity target = this.worm.getTarget();
        if (target != null && this.worm.canSee(target)) {
            if (this.worm.distanceTo(target) < 3F) {
                this.worm.doHurtTarget(target);
            }
        }

        Vec3 vector3d = this.worm.getDeltaMovement();
        if (vector3d.y * vector3d.y < (double) 0.1F && this.worm.xRot != 0.0F) {
            this.worm.xRot = Mth.rotlerp(this.worm.xRot, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(Entity.getHorizontalDistanceSqr(vector3d));
            double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double) (180F / (float) Math.PI);
            this.worm.xRot = (float) d1;
        }

    }
}
