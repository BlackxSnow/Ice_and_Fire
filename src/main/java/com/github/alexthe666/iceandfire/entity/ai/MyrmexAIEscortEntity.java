package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAIEscortEntity extends Goal {
    private final EntityMyrmexSoldier myrmex;
    private final double movementSpeed;
    private Path path;

    public MyrmexAIEscortEntity(EntityMyrmexSoldier entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        return this.myrmex.canMove() && this.myrmex.getTarget() == null && this.myrmex.guardingEntity != null && (this.myrmex.guardingEntity.canSeeSky() || !this.myrmex.canSeeSky()) && !this.myrmex.isEnteringHive;
    }

    public void tick() {
        if (this.myrmex.guardingEntity != null && (this.myrmex.distanceTo(this.myrmex.guardingEntity) > 30 || this.myrmex.getNavigation().isDone())) {
            this.myrmex.getNavigation().moveTo(this.myrmex.guardingEntity, movementSpeed);
        }
    }

    public boolean canContinueToUse() {
        return this.myrmex.canMove() && this.myrmex.getTarget() == null && this.myrmex.guardingEntity != null && this.myrmex.guardingEntity.isAlive() && (this.myrmex.distanceTo(this.myrmex.guardingEntity) < 15 || !this.myrmex.getNavigation().isDone()) && (this.myrmex.canSeeSky() == this.myrmex.guardingEntity.canSeeSky() && !this.myrmex.guardingEntity.canSeeSky());
    }

}