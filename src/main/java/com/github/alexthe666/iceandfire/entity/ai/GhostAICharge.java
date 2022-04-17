package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class GhostAICharge extends Goal {
    private EntityGhost ghost;
    public boolean firstPhase = true;
    public Vec3 moveToPos = null;
    public Vec3 offsetOf = Vec3.ZERO;

    public GhostAICharge(EntityGhost ghost) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.ghost = ghost;
    }

    public boolean canUse() {
        return ghost.getTarget() != null && !ghost.isCharging();
    }

    public boolean canContinueToUse() {
        return ghost.getTarget() != null && ghost.getTarget().isAlive();
    }

    public void start() {
        ghost.setCharging(true);
    }

    public void stop() {
        firstPhase = true;
        this.moveToPos = null;
        ghost.setCharging(false);
    }

    public void tick() {
        LivingEntity target = ghost.getTarget();
        if(target != null){
            if(this.ghost.getAnimation() == IAnimatedEntity.NO_ANIMATION && this.ghost.distanceTo(target) < 1.4D) {
                this.ghost.setAnimation(EntityGhost.ANIMATION_HIT);
            }
            if(firstPhase){
                if(this.moveToPos == null){
                    BlockPos moveToPos = DragonUtils.getBlockInTargetsViewGhost(ghost, target);
                    this.moveToPos = Vec3.atCenterOf(moveToPos);
                }else{
                    this.ghost.getNavigation().moveTo(this.moveToPos.x + 0.5D, this.moveToPos.y + 0.5D, this.moveToPos.z + 0.5D, 1F);
                    if(this.ghost.distanceToSqr(this.moveToPos.add(0.5D, 0.5D, 0.5D)) < 9D){
                        if(this.ghost.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                            this.ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
                        }
                        this.firstPhase = false;
                        this.moveToPos = null;
                        offsetOf = target.position().subtract(this.ghost.position()).normalize();
                    }
                }
            }else{
                Vec3 fin = target.position();
                this.moveToPos = new Vec3(fin.x, target.getY() + target.getEyeHeight()/2, fin.z);
                this.ghost.getNavigation().moveTo(target, 1.2F);
                if(this.ghost.distanceToSqr(this.moveToPos.add(0.5D, 0.5D, 0.5D)) < 3D) {
                    this.stop();
                }
            }
        }
        /*if (ghost.getBoundingBox().intersects(lvt_1_1_.getBoundingBox())) {
            ghost.attackEntityAsMob(lvt_1_1_);
            ghost.setCharging(false);
        } else {
            double lvt_2_1_ = ghost.getDistanceSq(lvt_1_1_);
            if (lvt_2_1_ < 9.0D) {
                Vector3d lvt_4_1_ = lvt_1_1_.getEyePosition(1.0F);
                ghost.getMoveHelper().setMoveTo(lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z, 1.0D);
            }
        }*/

    }
}
