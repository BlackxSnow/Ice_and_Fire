package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DeathWormAIGetInSand extends Goal {
    private final EntityDeathWorm creature;
    private final double movementSpeed;
    private final Level world;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public DeathWormAIGetInSand(EntityDeathWorm theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.world = theCreatureIn.level;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (creature.isVehicle() || creature.isInSand() || creature.getTarget() != null && !creature.getTarget().isInWater()) {
            return false;
        } else {
            Vec3 Vector3d = this.findPossibleShelter();

            if (Vector3d == null) {
                return false;
            } else {
                this.shelterX = Vector3d.x;
                this.shelterY = Vector3d.y;
                this.shelterZ = Vector3d.z;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    public boolean canContinueToUse() {
        return !this.creature.getNavigation().isDone();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.creature.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    @Nullable
    private Vec3 findPossibleShelter() {
        Random random = this.creature.getRandom();
        BlockPos blockpos = new BlockPos(this.creature.getX(), this.creature.getBoundingBox().minY, this.creature.getZ());

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (this.world.getBlockState(blockpos1).getMaterial() == Material.SAND) {
                return new Vec3(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
        }

        return null;
    }
}