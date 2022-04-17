package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DragonAIEscort extends Goal {
    private final EntityDragonBase dragon;
    private final double movementSpeed;
    private Path path;
    private BlockPos previousPosition;
    private int maxRange = 2000;

    public DragonAIEscort(EntityDragonBase entityIn, double movementSpeedIn) {
        this.dragon = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        return this.dragon.canMove() && this.dragon.getTarget() == null && this.dragon.getOwner() != null && this.dragon.getCommand() == 2;
    }

    public void tick() {
        if (this.dragon.getOwner() != null) {
            double dist = this.dragon.distanceTo(this.dragon.getOwner());
            if (dist > maxRange){
                return;
            }
            if (dist > this.dragon.getBoundingBox().getSize() && (!this.dragon.isFlying() && !this.dragon.isHovering() || !dragon.isAllowedToTriggerFlight())) {
                if(previousPosition == null || previousPosition.distSqr(this.dragon.getOwner().blockPosition()) > 9) {
                    this.dragon.getNavigation().moveTo(this.dragon.getOwner(), 1F);
                    previousPosition = this.dragon.getOwner().blockPosition();
                }
            }
            if ((dist > 30 || this.dragon.getOwner().getY() - this.dragon.getY() > 8) && !this.dragon.isFlying() && !this.dragon.isHovering() && dragon.isAllowedToTriggerFlight()) {
                this.dragon.setHovering(true);
                this.dragon.setInSittingPose(false);
                this.dragon.setOrderedToSit(false);
                this.dragon.flyTicks = 0;
            }
        }

    }

    public boolean canContinueToUse() {
        return this.dragon.canMove() && this.dragon.getTarget() == null && this.dragon.getOwner() != null && this.dragon.getOwner().isAlive() && (this.dragon.distanceTo(this.dragon.getOwner()) > 15 || !this.dragon.getNavigation().isDone());
    }

}
