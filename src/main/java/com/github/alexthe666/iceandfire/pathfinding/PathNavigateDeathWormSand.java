package com.github.alexthe666.iceandfire.pathfinding;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class PathNavigateDeathWormSand extends PathNavigation {
    private EntityDeathWorm worm;

    public PathNavigateDeathWormSand(EntityDeathWorm deathworm, Level worldIn) {
        super(deathworm, worldIn);
        worm = deathworm;
    }

    public boolean canFloat() {
        return this.nodeEvaluator.canFloat();
    }

    protected PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new NodeProcessorDeathWorm();
        this.nodeEvaluator.setCanPassDoors(true);
        this.nodeEvaluator.setCanFloat(true);
        return new PathFinder(this.nodeEvaluator, i);
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canUpdatePath() {
        return worm.isInSand();
    }

    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ());
    }

    protected void followThePath() {
        Vec3 Vector3d = this.getTempMobPos();
        float f = 0.65F;
        int i = 6;

        if (Vector3d.distanceToSqr(this.path.getEntityPosAtNode(this.mob, this.path.getNextNodeIndex())) < (double) f) {
            this.path.advance();
        }

        for (int j = Math.min(this.path.getNextNodeIndex() + 6, this.path.getNodeCount() - 1); j > this.path.getNextNodeIndex(); --j) {
            Vec3 Vector3d1 = this.path.getEntityPosAtNode(this.mob, j);

            if (Vector3d1.distanceToSqr(Vector3d) <= 36.0D && this.canMoveDirectly(Vector3d, Vector3d1, 0, 0, 0)) {
                this.path.setNextNodeIndex(j);
                break;
            }
        }

        this.doStuckDetection(Vector3d);
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
        HitResult raytraceresult = this.level.clip(new ClipContext(posVec31, posVec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.BLOCK) {
            return mob.level.getBlockState(new BlockPos(raytraceresult.getLocation())).getMaterial() == Material.SAND;
        }
        return false;
    }

    public boolean isStableDestination(BlockPos pos) {
        return this.level.getBlockState(pos).canOcclude();
    }
}