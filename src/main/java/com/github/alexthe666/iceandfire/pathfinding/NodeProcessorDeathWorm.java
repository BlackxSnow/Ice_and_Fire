package com.github.alexthe666.iceandfire.pathfinding;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;

public class NodeProcessorDeathWorm extends NodeEvaluator {
    public Node getStart() {
        return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public Target getGoal(double x, double y, double z) {
        return new Target(this.getNode(Mth.floor(x - 0.4), Mth.floor(y + 0.5D), Mth.floor(z - 0.4)));
    }

    @Override
    public int getNeighbors(Node[] p_222859_1_, Node p_222859_2_) {
        return 0;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z, Mob entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return null;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z) {
        return null;
    }

    public int findPathOptions(Node[] pathOptions, Node currentPoint, Node targetPoint, float maxDistance) {
        int i = 0;

        for (Direction Direction : Direction.values()) {
            Node pathpoint = this.getWaterNode(currentPoint.x + Direction.getStepX(), currentPoint.y + Direction.getStepY(), currentPoint.z + Direction.getStepZ());

            if (pathpoint != null && !pathpoint.closed && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }


    @Nullable
    private Node getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        BlockPathTypes pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype == BlockPathTypes.OPEN ? this.getNode(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }

    private BlockPathTypes isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        return BlockPathTypes.OPEN;
    }
}