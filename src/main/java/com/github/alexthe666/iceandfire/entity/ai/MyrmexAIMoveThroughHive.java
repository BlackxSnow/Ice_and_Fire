package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAIMoveThroughHive extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private BlockPos nextRoom = BlockPos.ZERO;
    private PathResult path;

    public MyrmexAIMoveThroughHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.myrmex.canMove() || this.myrmex instanceof EntityMyrmexWorker && ((EntityMyrmexWorker) this.myrmex).holdingSomething() || !this.myrmex.shouldMoveThroughHive() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigation().isDone() || this.myrmex.canSeeSky()) {
            return false;
        }
        MyrmexHive village = MyrmexWorldData.get(this.myrmex.level).getNearestHive(this.myrmex.blockPosition(), 300);
        if (village == null) {
            village = this.myrmex.getHive();
        }
        if (!(this.myrmex.getNavigation() instanceof AdvancedPathNavigate) ||this.myrmex.isPassenger()){
            return false;
        }
        if (village == null) {
            return false;
        } else {
            nextRoom = MyrmexHive.getGroundedPos(this.myrmex.level, village.getRandomRoom(this.myrmex.getRandom(), this.myrmex.blockPosition()));
            this.path = ((AdvancedPathNavigate)this.myrmex.getNavigation()).moveToXYZ(nextRoom.getX(), nextRoom.getY(),  nextRoom.getZ(), movementSpeed);
            return this.path != null;
        }
    }

    public boolean canContinueToUse() {
        return  !myrmex.shouldLeaveHive() && !this.myrmex.isCloseEnoughToTarget(nextRoom,3) && this.myrmex.shouldEnterHive() && !(this.myrmex instanceof EntityMyrmexWorker && ((EntityMyrmexWorker) this.myrmex).holdingBaby());
    }

    public void start() {
    }

    public void stop() {
        nextRoom = BlockPos.ZERO;

    }
}