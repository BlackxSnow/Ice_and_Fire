package com.github.alexthe666.iceandfire.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.pathfinding.*;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs.*;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.pathfinding.*;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Region;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

/**
 * Minecolonies async PathNavigate.
 */
public class AdvancedPathNavigate extends AbstractAdvancedPathNavigate {
    public static final double MIN_Y_DISTANCE = 0.001;
    public static final int MAX_SPEED_ALLOWED = 2;
    public static final double MIN_SPEED_ALLOWED = 0.1;
    private static final double ON_PATH_SPEED_MULTIPLIER = 1.3D;
    private static final double PIRATE_SWIM_BONUS = 1.5;
    private static final double BARBARIAN_SWIM_BONUS = 1.2;
    private static final double CITIZEN_SWIM_BONUS = 1.1;
    @Nullable
    private PathResult pathResult;

    /**
     * The world time when a path was added.
     */
    private long pathStartTime = 0;

    /**
     * Spawn pos of minecart.
     */
    private BlockPos spawnedPos = BlockPos.ZERO;

    /**
     * Desired position to reach
     */
    private BlockPos desiredPos;

    /**
     * Timeout for the desired pos, resets when its no longer wanted
     */
    private int desiredPosTimeout = 0;

    /**
     * The stuck handler to use
     */
    private IStuckHandler stuckHandler;

    /**
     * Whether we did set sneaking
     */
    private boolean isSneaking = true;

    private float width = 1;

    private float height = 1;

    public enum MovementType {
        WALKING,
        FLYING,
        CLIMBING
    }
    /**
     * Instantiates the navigation of an ourEntity.
     *
     * @param entity the ourEntity.
     * @param world  the world it is in.
     */
    public AdvancedPathNavigate(final Mob entity, final Level world) {
        this(entity,world,MovementType.WALKING);
    }
    public AdvancedPathNavigate(final Mob entity, final Level world, MovementType type) {
       this(entity, world, type,1,1);
    }
    public AdvancedPathNavigate(final Mob entity, final Level world, MovementType type, float width, float height) {
        super(entity, world);
        switch (type){
            case FLYING:
                this.nodeEvaluator = new NodeProcessorFly();
                getPathingOptions().setIsFlying(true);
                break;
            case WALKING:
                this.nodeEvaluator = new NodeProcessorWalk();
                break;
            case CLIMBING:
                this.nodeEvaluator = new NodeProcessorWalk();
                getPathingOptions().setCanClimb(true);
                break;
        }
        this.nodeEvaluator.setCanPassDoors(true);
        getPathingOptions().setEnterDoors(true);
        this.nodeEvaluator.setCanOpenDoors(true);
        getPathingOptions().setCanOpenDoors(true);
        this.nodeEvaluator.setCanFloat(true);
        getPathingOptions().setCanSwim(true);
        this.width = width;
        this.height = height;
        stuckHandler = PathingStuckHandler.createStuckHandler().withTakeDamageOnStuck(0.2f).withTeleportSteps(6).withTeleportOnFullStuck();
    }

    public static boolean isEqual(final BlockPos coords, final int x, final int y, final int z) {
        return coords.getX() == x && coords.getY() == y && coords.getZ() == z;
    }

    @Override
    public BlockPos getDestination() {
        return destination;
    }

    @Nullable
    public PathResult moveAwayFromXYZ(final BlockPos avoid, final double range, final double speedFactor) {
        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);

        return setPathJob(new PathJobMoveAwayFromLocation(ourEntity.level,
                start,
                avoid,
                (int) range,
                (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                ourEntity), null, speedFactor);
    }

    @Nullable
    public RandomPathResult moveToRandomPos(final double range, final double speedFactor) {
        if (pathResult instanceof RandomPathResult && pathResult.isComputing()) {
            return (RandomPathResult) pathResult;
        }

        final int theRange = (int) (mob.getRandom().nextInt((int) range) + range / 2);
        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);

        return (RandomPathResult) setPathJob(new PathJobRandomPos(ourEntity.level,
                start,
                theRange,
                (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                ourEntity), null, speedFactor);
    }

    @Nullable
    public PathResult setPathJob(
            final AbstractPathJob job,
            final BlockPos dest,
            final double speedFactor) {
        if (dest != null && dest.equals(desiredPos) && calculationFuture != null && pathResult != null) {
            return pathResult;
        }

        stop();

        this.destination = dest;
        this.originalDestination = dest;
        if (dest != null) {
            desiredPos = dest;
            desiredPosTimeout = 50 * 20;
        }
        this.walkSpeedFactor = speedFactor;

        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            IceAndFire.LOGGER.error("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity, new Exception());
            return null;
        }

        job.setPathingOptions(getPathingOptions());
        calculationFuture = Pathfinding.enqueue(job);
        pathResult = job.getResult();
        return pathResult;
    }

    @Override
    public boolean isDone() {
        return calculationFuture == null && super.isDone();
    }

    @Override
    public void tick() {
        if (nodeEvaluator instanceof  NodeProcessorWalk){
            ((NodeProcessorWalk)nodeEvaluator).setEntitySize(width, height);
        }
        else{
            ((NodeProcessorFly)nodeEvaluator).setEntitySize(width, height);
        }
        if (desiredPosTimeout > 0) {
            if (desiredPosTimeout-- <= 0) {
                desiredPos = null;
            }
        }

        if (calculationFuture != null) {
            if (!calculationFuture.isDone()) {
                return;
            }

            try {
                if (processCompletedCalculationResult()) {
                    return;
                }
            } catch (InterruptedException | ExecutionException e) {
                IceAndFire.LOGGER.catching(e);
            }

            calculationFuture = null;
        }

        int oldIndex = this.isDone() ? 0 : this.getPath().getNextNodeIndex();

        if (isSneaking) {
            isSneaking = false;
            mob.setShiftKeyDown(false);
        }
        //this.ourEntity.setMoveVertical(0);
        if (handleLadders(oldIndex)) {
            followThePath();
            return;
        }
        if (handleRails()) {
            return;
        }
        super.tick();

        if (pathResult != null && isDone()) {
            pathResult.setStatus(PathFindingStatus.COMPLETE);
            pathResult = null;
        }
        //Make sure the entity isn't sleeping, tamed or chained when checking if it's stuck
        if (this.mob instanceof TamableAnimal){
            if (((TamableAnimal)this.mob).isTame())
                return;
            if (this.mob instanceof EntityDragonBase){
                if (((EntityDragonBase) this.mob).isChained())
                    return;
                if (((EntityDragonBase) this.mob).isInSittingPose())
                    return;
            }

        }

        stuckHandler.checkStuck(this);
    }

    @Nullable
    public PathResult moveToXYZ(final double x, final double y, final double z, final double speedFactor) {
        final int newX = Mth.floor(x);
        final int newY = (int) y;
        final int newZ = Mth.floor(z);

        if (pathResult != null &&
                (
                        pathResult.isComputing()
                                || (destination != null && isEqual(destination, newX, newY, newZ))
                                || (originalDestination != null && isEqual(originalDestination, newX, newY, newZ))
                )
        ) {
            return pathResult;
        }

        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);
        desiredPos = new BlockPos(newX, newY, newZ);

        return setPathJob(
                new PathJobMoveToLocation(ourEntity.level,
                        start,
                        desiredPos,
                        (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                        ourEntity),
                desiredPos, speedFactor);
    }

    @Override
    public boolean tryMoveToBlockPos(final BlockPos pos, final double speedFactor) {
        moveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speedFactor);
        return true;
    }
    //Return a new WalkNodeProcessor for safety reasons eg if the entity
    //has a passenger this method get's called and returning null is not a great idea
    @Override
    protected PathFinder createPathFinder(final int p_179679_1_) {
        return new PathFinder(new WalkNodeEvaluator(),p_179679_1_);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }


    @Override
    protected Vec3 getTempMobPos() {
        return this.ourEntity.position();
    }

    @Override
    public Path createPath(final BlockPos pos, final int accuracy) {
        return null;
    }

    @Override
    protected boolean canMoveDirectly(final Vec3 start, final Vec3 end, final int sizeX, final int sizeY, final int sizeZ) {
        // TODO improve road walking. This is better in some situations, but still not great.
        return super.canMoveDirectly(start, end, sizeX, sizeY, sizeZ);
    }

    public double getSpeedFactor() {

        speedModifier = walkSpeedFactor;
        return walkSpeedFactor;
    }

    @Override
    public void setSpeedModifier(final double speedFactor) {
        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            IceAndFire.LOGGER.debug("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity);
            return;
        }
        walkSpeedFactor = speedFactor;
    }

    /**
     * Deprecated - try to use BlockPos instead
     */
    @Override
    public boolean moveTo(final double x, final double y, final double z, final double speedFactor) {
        if (x == 0 && y == 0 && z == 0) {
            return false;
        }

        moveToXYZ(x, y, z, speedFactor);
        return true;
    }

    @Override
    public boolean moveTo(final Entity entityIn, final double speedFactor) {
        return tryMoveToBlockPos(entityIn.blockPosition(), speedFactor);
    }

    // Removes stupid vanilla stuff, causing our pathpoints to occasionally be replaced by vanilla ones.
    @Override
    protected void trimPath() {
    }

    @Override
    public boolean moveTo(@Nullable final Path path, final double speedFactor) {
        if (path == null) {
            stop();
            return false;
        }
        pathStartTime = level.getGameTime();
        return super.moveTo(convertPath(path), speedFactor);
    }

    /**
     * Converts the given path to a minecolonies path if needed.
     *
     * @param path given path
     * @return resulting path
     */
    private Path convertPath(final Path path) {
        final int pathLength = path.getNodeCount();
        Path tempPath = null;
        if (pathLength > 0 && !(path.getNode(0) instanceof PathPointExtended)) {
            //  Fix vanilla PathPoints to be PathPointExtended
            final PathPointExtended[] newPoints = new PathPointExtended[pathLength];

            for (int i = 0; i < pathLength; ++i) {
                final Node point = path.getNode(i);
                if (!(point instanceof PathPointExtended)) {
                    newPoints[i] = new PathPointExtended(new BlockPos(point.x, point.y, point.z));
                } else {
                    newPoints[i] = (PathPointExtended) point;
                }
            }

            tempPath = new Path(Arrays.asList(newPoints), path.getTarget(), path.canReach());

            final PathPointExtended finalPoint = newPoints[pathLength - 1];
            destination = new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
        }

        return tempPath == null ? path : tempPath;
    }

    private boolean processCompletedCalculationResult() throws InterruptedException, ExecutionException {
        if (calculationFuture.get() == null) {
            calculationFuture = null;
            return true;
        }

        moveTo(calculationFuture.get(), getSpeedFactor());

        pathResult.setPathLength(getPath().getNodeCount());
        pathResult.setStatus(PathFindingStatus.IN_PROGRESS_FOLLOWING);

        final Node p = getPath().getEndNode();
        if (p != null && destination == null) {
            destination = new BlockPos(p.x, p.y, p.z);

            //  AbstractPathJob with no destination, did reach it's destination
            pathResult.setPathReachesDestination(true);
        }
        return false;
    }

    private boolean handleLadders(int oldIndex) {
        //  Ladder Workaround
        if (!this.isDone()) {
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            final PathPointExtended pExNext = getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
                    ? (PathPointExtended) this.getPath()
                    .getNode(this.getPath()
                            .getNextNodeIndex() + 1)
                    : null;

            for (int i = this.path.getNextNodeIndex(); i < Math.min(this.path.getNodeCount(), this.path.getNextNodeIndex() + 3); i++) {
                final PathPointExtended nextPoints = (PathPointExtended) this.getPath().getNode(i);
                if (nextPoints.isOnLadder()) {
                    Vec3 motion = this.mob.getDeltaMovement();
                    double x = motion.x < -0.1 ? -0.1 : Math.min(motion.x, 0.1);
                    double z = motion.x < -0.1 ? -0.1 : Math.min(motion.z, 0.1);

                    this.ourEntity.setDeltaMovement(x, motion.y, z);
                    break;
                }
            }

            if (ourEntity.isInWater()) {
                return handleEntityInWater(oldIndex, pEx);
            } else if (level.random.nextInt(10) == 0) {
                speedModifier = getSpeedFactor();
            }
        }
        return false;
    }

    /**
     * Determine what block the entity stands on
     *
     * @param parEntity the entity that stands on the block
     * @return the Blockstate.
     */
    private BlockPos findBlockUnderEntity(final Entity parEntity) {
        int blockX = (int) Math.round(parEntity.getX());
        int blockY = Mth.floor(parEntity.getY() - 0.2D);
        int blockZ = (int) Math.round(parEntity.getZ());
        return new BlockPos(blockX, blockY, blockZ);
    }

    /**
     * Handle rails navigation.
     *
     * @return true if block.
     */
    private boolean handleRails() {
        if (!this.isDone()) {
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getNode(this.getPath().getNextNodeIndex());
            final PathPointExtended pExNext = getPath().getNodeCount() > this.getPath().getNextNodeIndex() + 1
                    ? (PathPointExtended) this.getPath()
                    .getNode(this.getPath()
                            .getNextNodeIndex() + 1)
                    : null;
        }
        return false;
    }

    private boolean handleEntityInWater(int oldIndex, final PathPointExtended pEx) {
        //  Prevent shortcuts when swimming
        final int curIndex = this.getPath().getNextNodeIndex();
        if (curIndex > 0
                && (curIndex + 1) < this.getPath().getNodeCount()
                && this.getPath().getNode(curIndex - 1).y != pEx.y) {
            //  Work around the initial 'spin back' when dropping into water
            oldIndex = curIndex + 1;
        }

        this.getPath().setNextNodeIndex(oldIndex);

        Vec3 vec3d = this.getPath().getNextEntityPos(this.ourEntity);

        if (vec3d.distanceToSqr(new Vec3(ourEntity.getX(), vec3d.y, ourEntity.getZ())) < 0.1
                && Math.abs(ourEntity.getY() - vec3d.y) < 0.5) {
            this.getPath().advance();
            if (this.isDone()) {
                return true;
            }

            vec3d = this.getPath().getNextEntityPos(this.ourEntity);
        }

        this.ourEntity.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, getSpeedFactor());
        return false;
    }

    @Override
    protected void followThePath() {
        getSpeedFactor();
        final int curNode = path.getNextNodeIndex();
        final int curNodeNext = curNode + 1;
        if (curNodeNext < path.getNodeCount()) {
            if (!(path.getNode(curNode) instanceof PathPointExtended)) {
                path = convertPath(path);
            }

            final PathPointExtended pEx = (PathPointExtended) path.getNode(curNode);
            final PathPointExtended pExNext = (PathPointExtended) path.getNode(curNodeNext);

            //  If current node is bottom of a ladder, then stay on this node until
            //  the ourEntity reaches the bottom, otherwise they will try to head out early
            if (pEx.isOnLadder() && pEx.getLadderFacing() == Direction.DOWN
                    && !pExNext.isOnLadder()) {
                final Vec3 vec3 = getTempMobPos();
                if ((vec3.y - (double) pEx.y) < MIN_Y_DISTANCE) {
                    this.path.setNextNodeIndex(curNodeNext);
                }
                return;
            }
        }

        Vec3 vec3d = this.getTempMobPos();
        this.maxDistanceToWaypoint = Math.max(this.mob.getBbWidth() / 2.0F, 1.3F);
        double maxYDistance = 0;
        // Look at multiple points, incase we're too fast
        for (int i = this.path.getNextNodeIndex(); i < Math.min(this.path.getNodeCount(), this.path.getNextNodeIndex() + 4); i++) {
            Vec3 vec3d2 = this.path.getEntityPosAtNode(this.mob, i);
            double yDist = Math.abs(this.mob.getY() - vec3d2.y);
            if (Math.abs(this.mob.getX() - vec3d2.x) < (double) this.maxDistanceToWaypoint
                    && Math.abs(this.mob.getZ() - vec3d2.z) < (double) this.maxDistanceToWaypoint &&
                    yDist <= Math.min(1.0F, Math.ceil(this.mob.getBbHeight() / 2.0F))) {
                this.path.advance();
                // Mark reached nodes for debug path drawing
                if (AbstractPathJob.lastDebugNodesPath != null) {
                    final Node point = path.getNode(i);
                    final BlockPos pos = new BlockPos(point.x, point.y, point.z);
                    for (final Node node : AbstractPathJob.lastDebugNodesPath) {
                        if (node.pos.equals(pos)) {
                            node.setReachedByWorker();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void recomputePath() {
    }

    /**
     * Don't let vanilla rapidly discard paths, set a timeout before its allowed to use stuck.
     */
    @Override
    protected void doStuckDetection(final Vec3 positionVec3) {
        // Do nothing, unstuck is checked on tick, not just when we have a path
    }

    public boolean entityOnOrBelowPath(Entity entity, Vec3 slack){
        Path path = getPath();
        if (path == null){
            return false;
        }
        //getIndex doesn't return an 0-indexed index
        int closest = path.getNextNodeIndex() - 1;
        if (closest < 0) {
            return true;
        }
        //Search through path from the current index outwards to improve performance
        for (int i = 0; i < path.getNodeCount(); i++) {
            if (closest + i < path.getNodeCount()) {
                Node currentPoint = path.getNode(closest + i);
                if (entityNearOrBelowPoint(currentPoint, entity, slack)) {
                    return true;
                }
            }
            if (closest - i >= 0) {
                Node currentPoint = path.getNode(closest - i);
                if (entityNearOrBelowPoint(currentPoint, entity, slack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean entityNearOrBelowPoint(Node currentPoint, Entity entity, Vec3 slack) {
        return Math.abs(currentPoint.x - entity.getX()) < slack.x()
                && currentPoint.y - entity.getY() > -slack.y()
                && Math.abs(currentPoint.z - entity.getZ()) < slack.z();
    }



    @Override
    public void stop() {
        if (calculationFuture != null) {
            calculationFuture.cancel(true);
            calculationFuture = null;
        }

        if (pathResult != null) {
            pathResult.setStatus(PathFindingStatus.CANCELLED);
            pathResult = null;
        }

        destination = null;
        super.stop();
    }

    @Nullable
    @Override
    public PathResult moveToLivingEntity(final Entity e, final double speed) {
        return moveToXYZ(e.getX(), e.getY(), e.getZ(), speed);
    }

    @Nullable
    @Override
    public PathResult moveAwayFromLivingEntity(final Entity e, final double distance, final double speed) {
        return moveAwayFromXYZ(e.blockPosition(), distance, speed);
    }

    @Override
    public void setCanFloat(boolean canSwim) {
        super.setCanFloat(canSwim);
        getPathingOptions().setCanSwim(canSwim);
    }

    public BlockPos getDesiredPos() {
        return desiredPos;
    }

    /**
     * Sets the stuck handler
     *
     * @param stuckHandler handler to set
     */
    @Override
    public void setStuckHandler(final IStuckHandler stuckHandler) {
        this.stuckHandler = stuckHandler;
    }
}
