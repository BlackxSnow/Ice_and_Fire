package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAIStoreBabies extends Goal {
    private final EntityMyrmexWorker myrmex;
    private final double movementSpeed;
    private Path path;
    private BlockPos nextRoom = BlockPos.ZERO;

    public MyrmexAIStoreBabies(EntityMyrmexWorker entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.myrmex.canMove() || !this.myrmex.holdingBaby() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigation().isDone() || this.myrmex.canSeeSky()) {
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            return false;
        } else {
            nextRoom = MyrmexHive.getGroundedPos(this.myrmex.level, village.getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.myrmex.getRandom(), this.myrmex.blockPosition())).above();
            return true;
        }
    }

    public boolean canContinueToUse() {
        return this.myrmex.holdingBaby() && !this.myrmex.getNavigation().isDone() && this.myrmex.distanceToSqr(nextRoom.getX() + 0.5D, nextRoom.getY() + 0.5D, nextRoom.getZ() + 0.5D) > 3 && this.myrmex.shouldEnterHive();
    }

    public void start() {
        this.myrmex.getNavigation().moveTo(this.nextRoom.getX(), this.nextRoom.getY(), this.nextRoom.getZ(), 1.5F);
    }

    @Override
    public void tick() {
        if (nextRoom != null && this.myrmex.distanceToSqr(nextRoom.getX() + 0.5D, nextRoom.getY() + 0.5D, nextRoom.getZ() + 0.5D) < 4 && this.myrmex.holdingBaby()) {
            if (!this.myrmex.getPassengers().isEmpty()) {
                for (Entity entity : this.myrmex.getPassengers()) {
                    entity.stopRiding();
                    stop();
                    entity.copyPosition(this.myrmex);
                }
            }
        }
    }

    public void stop() {
        nextRoom = BlockPos.ZERO;
        this.myrmex.getNavigation().stop();
    }

}