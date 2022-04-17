package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAIReEnterHive extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private PathResult path;
    private BlockPos nextEntrance = BlockPos.ZERO;
    private boolean first = true;
    private MyrmexHive hive;

    public MyrmexAIReEnterHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.myrmex.canMove() || this.myrmex.shouldLeaveHive() || !this.myrmex.shouldEnterHive() || !first) {
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            village = MyrmexWorldData.get(this.myrmex.level).getNearestHive(this.myrmex.blockPosition(), 500);
        }
        if (!(this.myrmex.getNavigation() instanceof AdvancedPathNavigate) || this.myrmex.isPassenger()){
            return false;
        }
        if (village == null || this.myrmex.isInHive()) {
            return false;
        } else {
            this.hive = village;
            nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.level, hive.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRandom(), false));
            this.path = ((AdvancedPathNavigate)this.myrmex.getNavigation()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(),  nextEntrance.getZ(), 1);
            first = true;
            return this.path != null;
        }
    }

    public void tick() {
        //Fallback for if for some reason the myrmex can't reach the entrance try a different one (random)
        if (first && !this.myrmex.pathReachesTarget(path,nextEntrance,12)) {
            nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.level, hive.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRandom(), true));
            this.path = ((AdvancedPathNavigate) this.myrmex.getNavigation()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(), nextEntrance.getZ(), movementSpeed);
        }
        if (first && this.myrmex.isCloseEnoughToTarget(nextEntrance,12)) {
            if (hive != null) {
                nextEntrance = hive.getClosestEntranceBottomToEntity(this.myrmex, this.myrmex.getRandom());
                first = false;
                this.path = ((AdvancedPathNavigate)this.myrmex.getNavigation()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(),  nextEntrance.getZ(), 1);
            }
        }
        this.myrmex.isEnteringHive = !this.myrmex.isCloseEnoughToTarget(nextEntrance,14) && !first;
    }

    public boolean canContinueToUse() {
        if (this.myrmex.isCloseEnoughToTarget(nextEntrance,9) && !first) {
            return false;
        }
        return true;
    }

    public void stop() {
        nextEntrance = BlockPos.ZERO;
        first = true;
    }
}