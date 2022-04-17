package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;

import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class AmphithereAIFleePlayer extends Goal {
    private final double farSpeed;
    private final double nearSpeed;
    private final float avoidDistance;
    protected EntityAmphithere entity;
    protected Player closestLivingEntity;
    private Path path;

    public AmphithereAIFleePlayer(EntityAmphithere entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this.entity = entityIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }


    public boolean canUse() {
        if (!this.entity.isFlying() && !this.entity.isTame()) {
            List<Player> list = this.entity.level.getEntitiesOfClass(Player.class, this.entity.getBoundingBox().inflate(this.avoidDistance, 6D, this.avoidDistance), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            if (list.isEmpty()) {
                return false;
            } else {
                this.closestLivingEntity = list.get(0);
                Vec3 Vector3d = DefaultRandomPos.getPosAway(this.entity, 20, 7, new Vec3(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));

                if (Vector3d == null) {
                    return false;
                } else if (this.closestLivingEntity.distanceToSqr(Vector3d) < this.closestLivingEntity.distanceToSqr(this.entity)) {
                    return false;
                } else {
                    this.path = this.entity.getNavigation().createPath(Vector3d.x, Vector3d.y, Vector3d.z, 0);
                    return this.path != null;
                }
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone();
    }

    public void start() {
        this.entity.getNavigation().moveTo(this.path, this.farSpeed);
    }

    public void stop() {
        this.closestLivingEntity = null;
    }

    public void tick() {
        if (this.entity.distanceToSqr(this.closestLivingEntity) < 49.0D) {
            this.entity.getNavigation().setSpeedModifier(this.nearSpeed);
        } else {
            this.entity.getNavigation().setSpeedModifier(this.farSpeed);
        }
    }
}