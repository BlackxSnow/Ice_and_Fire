package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DragonAIReturnToRoost  extends Goal {
    private final EntityDragonBase dragon;
    private final double movementSpeed;
    private Path path;

    public DragonAIReturnToRoost(EntityDragonBase entityIn, double movementSpeedIn) {
        this.dragon = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        return this.dragon.canMove() && this.dragon.lookingForRoostAIFlag && (dragon.getTarget() == null || !dragon.getTarget().isAlive()) && dragon.getRestrictCenter() != null && dragon.getDistanceSquared(Vec3.atCenterOf(dragon.getRestrictCenter())) > dragon.getBbWidth() * dragon.getBbWidth();
    }

    public void tick() {
        if (this.dragon.getRestrictCenter() != null) {
            double dist = Math.sqrt(dragon.getDistanceSquared(Vec3.atCenterOf(dragon.getRestrictCenter())));
            double xDist = Math.abs(dragon.getX() - dragon.getRestrictCenter().getX() - 0.5F);
            double zDist = Math.abs(dragon.getZ() - dragon.getRestrictCenter().getZ() - 0.5F);
            double xzDist = Math.sqrt(xDist * xDist + zDist * zDist);

            if (dist < this.dragon.getBbWidth()) {
                this.dragon.setFlying(false);
                this.dragon.setHovering(false);
                this.dragon.getNavigation().moveTo(this.dragon.getRestrictCenter().getX(), this.dragon.getRestrictCenter().getY(), this.dragon.getRestrictCenter().getZ(), 1.0F);
            }else{
                double yAddition = 15 + dragon.getRandom().nextInt(3);
                if(xzDist < 40){
                    yAddition = 0;
                    if(this.dragon.isOnGround()){
                        this.dragon.setFlying(false);
                        this.dragon.setHovering(false);
                        this.dragon.flightManager.setFlightTarget(Vec3.upFromBottomCenterOf(this.dragon.getRestrictCenter(), yAddition));
                        this.dragon.getNavigation().moveTo(this.dragon.getRestrictCenter().getX(), this.dragon.getRestrictCenter().getY(), this.dragon.getRestrictCenter().getZ(), 1.0F);
                        return;
                    }
                }
                if(!this.dragon.isFlying() && !this.dragon.isHovering() && xzDist > 40){
                    this.dragon.setHovering(true);
                }
                if(this.dragon.isFlying()){
                    this.dragon.flightManager.setFlightTarget(Vec3.upFromBottomCenterOf(this.dragon.getRestrictCenter(), yAddition));
                    this.dragon.getNavigation().moveTo(this.dragon.getRestrictCenter().getX(), yAddition + this.dragon.getRestrictCenter().getY(), this.dragon.getRestrictCenter().getZ(), 1F);
                }
                this.dragon.flyTicks = 0;
            }

        }

    }

    public boolean canContinueToUse() {
        return canUse();
    }

}
