package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;

public class SeaSerpentPathNavigator  extends PathNavigation {

    public SeaSerpentPathNavigator(Mob entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder createPathFinder(int p_179679_1_) {
        this.nodeEvaluator = new SwimNodeEvaluator(true);
        return new PathFinder(this.nodeEvaluator, p_179679_1_);
    }

    protected boolean canUpdatePath() {
        return true;
    }

    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY(0.5D), this.mob.getZ());
    }

    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }

        if (!this.isDone()) {
            Vec3 lvt_1_2_;
            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                lvt_1_2_ = this.path.getNextEntityPos(this.mob);
                if (Mth.floor(this.mob.getX()) == Mth.floor(lvt_1_2_.x) && Mth.floor(this.mob.getY()) == Mth.floor(lvt_1_2_.y) && Mth.floor(this.mob.getZ()) == Mth.floor(lvt_1_2_.z)) {
                    this.path.advance();
                }
            }

            DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
                lvt_1_2_ = this.path.getNextEntityPos(this.mob);
                this.mob.getMoveControl().setWantedPosition(lvt_1_2_.x, lvt_1_2_.y, lvt_1_2_.z, this.speedModifier);
            }
        }
    }

    protected void followThePath() {
        if (this.path != null) {
            Vec3 lvt_1_1_ = this.getTempMobPos();
            float lvt_2_1_ = this.mob.getBbWidth();
            float lvt_3_1_ = lvt_2_1_ > 0.75F ? lvt_2_1_ / 2.0F : 0.75F - lvt_2_1_ / 2.0F;
            Vec3 lvt_4_1_ = this.mob.getDeltaMovement();
            if (Math.abs(lvt_4_1_.x) > 0.2D || Math.abs(lvt_4_1_.z) > 0.2D) {
                lvt_3_1_ = (float)((double)lvt_3_1_ * lvt_4_1_.length() * 6.0D);
            }
            Vec3 lvt_6_1_ = Vec3.atCenterOf(this.path.getNextNodePos());
            if (Math.abs(this.mob.getX() - lvt_6_1_.x) < (double)lvt_3_1_ && Math.abs(this.mob.getZ() - lvt_6_1_.z) < (double)lvt_3_1_ && Math.abs(this.mob.getY() - lvt_6_1_.y) < (double)(lvt_3_1_ * 2.0F)) {
                this.path.advance();
            }

            for(int lvt_7_1_ = Math.min(this.path.getNextNodeIndex() + 6, this.path.getNodeCount() - 1); lvt_7_1_ > this.path.getNextNodeIndex(); --lvt_7_1_) {
                lvt_6_1_ = this.path.getEntityPosAtNode(this.mob, lvt_7_1_);
                if (lvt_6_1_.distanceToSqr(lvt_1_1_) <= 36.0D && this.canMoveDirectly(lvt_1_1_, lvt_6_1_, 0, 0, 0)) {
                    this.path.setNextNodeIndex(lvt_7_1_);
                    break;
                }
            }

            this.doStuckDetection(lvt_1_1_);
        }
    }

    protected void doStuckDetection(Vec3 positionVec3) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (positionVec3.distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
                this.stop();
            }

            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = positionVec3;
        }

        if (this.path != null && !this.path.isDone()) {
            Vec3i lvt_2_1_ = this.path.getNextNodePos();
            if (lvt_2_1_.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = lvt_2_1_;
                double lvt_3_1_ = positionVec3.distanceTo(Vec3.atCenterOf(this.timeoutCachedNode));
                this.timeoutLimit = this.mob.getSpeed() > 0.0F ? lvt_3_1_ / (double)this.mob.getSpeed() * 100.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
                this.timeoutCachedNode = Vec3i.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0D;
                this.stop();
            }

            this.lastTimeoutCheck = Util.getMillis();
        }

    }

    protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
        Vec3 lvt_6_1_ = new Vec3(posVec32.x, posVec32.y + (double)this.mob.getBbHeight() * 0.5D, posVec32.z);
        return this.level.clip(new ClipContext(posVec31, lvt_6_1_, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
    }

    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos).isSolidRender(this.level, pos);
    }

    public void setCanFloat(boolean canSwim) {
    }
}
