package com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.*;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Callable;

import static com.github.alexthe666.iceandfire.pathfinding.raycoms.PathfindingConstants.*;

import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Abstract class for Jobs that run in the multithreaded path finder.
 */
public abstract class AbstractPathJob implements Callable<Path> {
    @Nullable
    public static Set<RayNode> lastDebugNodesVisited;
    @Nullable
    public static Set<RayNode> lastDebugNodesNotVisited;
    @Nullable
    public static Set<RayNode> lastDebugNodesPath;

    protected final BlockPos start;
    protected final BlockPos end;

    protected final LevelReader world;
    protected final PathResult result;
    private int maxRange;
    private final Queue<RayNode> nodesOpen = new PriorityQueue<>(500);
    private final Map<Integer, RayNode> nodesVisited = new HashMap<>();
    //  Debug Rendering
    protected boolean debugDrawEnabled = false;
    @Nullable
    protected Set<RayNode> debugNodesVisited = null;
    @Nullable
    protected Set<RayNode> debugNodesNotVisited = null;
    @Nullable
    protected Set<RayNode> debugNodesPath = null;
    /**
     * The entity this job belongs to.
     */
    protected WeakReference<LivingEntity> entity;
    //  May be faster, but can produce strange results
    private boolean allowJumpPointSearchTypeWalk;
    private int totalNodesAdded = 0;
    private int totalNodesVisited = 0;
    private float entitySizeXZ = 1;
    private int entitySizeY = 1;
    /**
     * Are there hard xz restrictions.
     */
    private boolean xzRestricted = false;
    /**
     * The cost values for certain nodes.
     */
    private PathingOptions pathingOptions = new PathingOptions();
    /**
     * The restriction parameters
     */
    private int maxX;
    private int minX;
    private int maxZ;
    private int minZ;
    private boolean circumventSizeCheck = false;
    private IPassabilityNavigator passabilityNavigator;
    private float jumpHeight = 1.3f;

    /**
     * AbstractPathJob constructor.
     *
     * @param world  the world within which to path.
     * @param start  the start position from which to path from.
     * @param end    the end position to path to.
     * @param range  maximum path range.
     * @param entity the entity.
     */
    public AbstractPathJob(final Level world, final BlockPos start, final BlockPos end, final int range, final LivingEntity entity) {
        this(world, start, end, range, new PathResult(), entity);
        setEntitySizes(entity);
        if (entity instanceof IPassabilityNavigator) {
            passabilityNavigator = (IPassabilityNavigator) entity;
            maxRange = passabilityNavigator.maxSearchNodes();
        }
        jumpHeight = (float) Math.floor(entity.maxUpStep - 0.2F) + 1.3F;
    }

    /**
     * AbstractPathJob constructor.
     *
     * @param world  the world within which to path.
     * @param start  the start position from which to path from.
     * @param end    the end position to path to
     * @param range  maximum path range.
     * @param result path result.
     * @param entity the entity.
     //* @see AbstractPathJob#AbstractPathJob(World, BlockPos, BlockPos, int, LivingEntity)
     */
    public AbstractPathJob(final Level world, final BlockPos start, final BlockPos end, final int range, final PathResult result, final LivingEntity entity) {
        final int minX = Math.min(start.getX(), end.getX()) - (range / 2);
        final int minZ = Math.min(start.getZ(), end.getZ()) - (range / 2);
        final int maxX = Math.max(start.getX(), end.getX()) + (range / 2);
        final int maxZ = Math.max(start.getZ(), end.getZ()) + (range / 2);

        this.world = new ChunkCache(world, new BlockPos(minX, MIN_Y, minZ), new BlockPos(maxX, MAX_Y, maxZ), range);

        this.start = new BlockPos(start);
        this.end = new BlockPos(end);
        this.maxRange = range;

        this.result = result;

        allowJumpPointSearchTypeWalk = false;
        if (Pathfinding.isDebug()) {
            debugDrawEnabled = true;
            debugNodesVisited = new HashSet<>();
            debugNodesNotVisited = new HashSet<>();
            debugNodesPath = new HashSet<>();
        }
        this.entity = new WeakReference<>(entity);
    }

    /**
     * AbstractPathJob constructor.
     *
     * @param world            the world within which to path.
     * @param startRestriction start of restricted area.
     * @param endRestriction   end of restricted area.
     * @param result           path result.
     * @param entity           the entity.
     //* @see AbstractPathJob#AbstractPathJob(World, BlockPos, BlockPos, int, LivingEntity)
     */
    public AbstractPathJob(final Level world, final BlockPos startRestriction, final BlockPos endRestriction, final PathResult result, final LivingEntity entity) {
        this.minX = Math.min(startRestriction.getX(), endRestriction.getX());
        this.minZ = Math.min(startRestriction.getZ(), endRestriction.getZ());
        this.maxX = Math.max(startRestriction.getX(), endRestriction.getX());
        this.maxZ = Math.max(startRestriction.getZ(), endRestriction.getZ());

        xzRestricted = true;


        final int range = (int) Math.sqrt(Math.pow(maxX - minX, 2) + Math.pow(maxZ - minZ, 2)) * 2;

        this.world = new ChunkCache(world, new BlockPos(minX, MIN_Y, minZ), new BlockPos(maxX, MAX_Y, maxZ), range);

        this.start = startRestriction;
        this.end = new BlockPos(endRestriction);
        this.maxRange = range;

        this.result = result;

        allowJumpPointSearchTypeWalk = false;
        if (Pathfinding.isDebug()) {
            debugDrawEnabled = true;
            debugNodesVisited = new HashSet<>();
            debugNodesNotVisited = new HashSet<>();
            debugNodesPath = new HashSet<>();
        }
        this.entity = new WeakReference<>(entity);
    }

    private static boolean onLadderGoingUp(final RayNode currentRayNode, final BlockPos dPos) {
        return currentRayNode.isLadder() && (dPos.getY() >= 0 || dPos.getX() != 0 || dPos.getZ() != 0);
    }

    /**
     * Generates a good path starting location for the entity to path from, correcting for the following conditions. - Being in water: pathfinding in water occurs along the
     * surface; adjusts position to surface. - Being in a fence space: finds correct adjacent position which is not a fence space, to prevent starting path. from within the fence
     * block.
     *
     * @param entity Entity for the pathfinding operation.
     * @return ChunkCoordinates for starting location.
     */
    public static BlockPos prepareStart(final LivingEntity entity) {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(entity.getX()),
                Mth.floor(entity.getY()),
                Mth.floor(entity.getZ()));
        BlockState bs = entity.level.getBlockState(pos);

        // 1 Up when we're standing within this collision shape
        final VoxelShape collisionShape = bs.getCollisionShape(entity.level, pos);
        if (bs.getMaterial().blocksMotion() && collisionShape.max(Direction.Axis.Y) > 0) {
            final double relPosX = Math.abs(entity.getX() % 1);
            final double relPosZ = Math.abs(entity.getZ() % 1);

            for (final AABB box : collisionShape.toAabbs()) {
                if (relPosX >= box.minX && relPosX <= box.maxX
                        && relPosZ >= box.minZ && relPosZ <= box.maxZ
                        && box.maxY > 0) {
                    pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                    bs = entity.level.getBlockState(pos);
                    break;
                }
            }
        }

        final Block b = bs.getBlock();

        if (entity.isInWater()) {
            while (bs.getMaterial().isLiquid()) {
                pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                bs = entity.level.getBlockState(pos);
            }
        } else if (b instanceof FenceBlock || b instanceof WallBlock || bs.getMaterial().isSolid()) {
            //Push away from fence
            final double dX = entity.getX() - Math.floor(entity.getX());
            final double dZ = entity.getZ() - Math.floor(entity.getZ());

            if (dX < ONE_SIDE) {
                pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
            } else if (dX > OTHER_SIDE) {
                pos.set(pos.getX() + 1, pos.getY(), pos.getZ());
            }

            if (dZ < ONE_SIDE) {
                pos.set(pos.getX(), pos.getY(), pos.getZ() - 1);
            } else if (dZ > OTHER_SIDE) {
                pos.set(pos.getX(), pos.getY(), pos.getZ() + 1);
            }
        }

        return pos.immutable();
    }

    /**
     * Sets the direction where the ladder is facing.
     *
     * @param world the world in.
     * @param pos   the position.
     * @param p     the path.
     */
    private static void setLadderFacing(final LevelReader world, final BlockPos pos, final PathPointExtended p) {
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();
        if (block instanceof VineBlock) {
            if (state.getValue(VineBlock.SOUTH)) {
                p.setLadderFacing(Direction.NORTH);
            } else if (state.getValue(VineBlock.WEST)) {
                p.setLadderFacing(Direction.EAST);
            } else if (state.getValue(VineBlock.NORTH)) {
                p.setLadderFacing(Direction.SOUTH);
            } else if (state.getValue(VineBlock.EAST)) {
                p.setLadderFacing(Direction.WEST);
            }
        } else if (block instanceof LadderBlock) {
            p.setLadderFacing(state.getValue(LadderBlock.FACING));
        } else {
            p.setLadderFacing(Direction.UP);
        }
    }

    /**
     * Checks if entity is on a ladder.
     *
     * @param rayNode       the path node.
     * @param nextInPath the next path point.
     * @param pos        the position.
     * @return true if on a ladder.
     */
    private static boolean onALadder(final RayNode rayNode, @Nullable final RayNode nextInPath, final BlockPos pos) {
        return nextInPath != null && rayNode.isLadder()
                &&
                (nextInPath.pos.getX() == pos.getX() && nextInPath.pos.getZ() == pos.getZ());
    }

    /**
     * Generate a pseudo-unique key for identifying a given node by it's coordinates Encodes the lowest 12 bits of x,z and all useful bits of y. This creates unique keys for all
     * blocks within a 4096x256x4096 cube, which is FAR bigger volume than one should attempt to pathfind within This version takes a BlockPos
     *
     * @param pos BlockPos to generate key from
     * @return key for node in map
     */
    private static int computeNodeKey(final BlockPos pos) {
        return ((pos.getX() & 0xFFF) << SHIFT_X_BY)
                | ((pos.getY() & 0xFF) << SHIFT_Y_BY)
                | (pos.getZ() & 0xFFF);
    }

    private static boolean nodeClosed(@Nullable final RayNode rayNode) {
        return rayNode != null && rayNode.isClosed();
    }

    private static boolean calculateSwimming(final LevelReader world, final BlockPos pos, @Nullable final RayNode rayNode) {
        return (rayNode == null) ? world.getBlockState(pos.below()).getMaterial().isLiquid() : rayNode.isSwimming();
    }

    public static Direction getXZFacing(final BlockPos pos, final BlockPos neighbor) {
        final BlockPos vector = neighbor.subtract(pos);
        return Direction.getNearest(vector.getX(), 0, vector.getZ());
    }

    public void setEntitySizes(LivingEntity entity) {
        if (entity instanceof ICustomSizeNavigator) {
            entitySizeXZ = ((ICustomSizeNavigator) entity).getXZNavSize();
            entitySizeY = ((ICustomSizeNavigator) entity).getYNavSize();
            circumventSizeCheck = ((ICustomSizeNavigator) entity).isSmallerThanBlock();
        }
        entitySizeXZ = entity.getBbWidth() / 2.0F;
        entitySizeY = Mth.ceil(entity.getBbHeight());
        allowJumpPointSearchTypeWalk = true;
    }

    /**
     * Compute the cost (immediate 'g' value) of moving from the parent space to the new space.
     *
     * @param dPos       The delta from the parent to the new space; assumes dx,dy,dz in range of [-1..1].
     * @param isSwimming true is the current node would require the citizen to swim.
     * @param onPath     checks if the node is on a path.
     * @param onRails    checks if the node is a rail block.
     * @param railsExit  the exit of the rails.
     * @param blockPos   the position.
     * @param swimStart  if its the swim start.
     * @return cost to move from the parent to the new position.
     */
    protected double computeCost(
            final BlockPos dPos,
            final boolean isSwimming,
            final boolean onPath,
            final boolean onRails,
            final boolean railsExit,
            final boolean swimStart,
            final BlockPos blockPos) {
        double cost = Math.sqrt(dPos.getX() * dPos.getX() + dPos.getY() * dPos.getY() + dPos.getZ() * dPos.getZ());

        if (dPos.getY() != 0 && (dPos.getX() != 0 || dPos.getZ() != 0) && !(Math.abs(dPos.getY()) <= 1 && world.getBlockState(blockPos).getBlock() instanceof StairBlock)) {
            //  Tax the cost for jumping, dropping
            cost *= pathingOptions.jumpDropCost * Math.abs(dPos.getY());
        }
        if (dPos.getY() >= 2){
            cost *= dPos.getY();
        }
        if (onPath) {
            cost *= pathingOptions.onPathCost;
        }

        if (onRails) {
            cost *= pathingOptions.onRailCost;
        }

        if (railsExit) {
            cost *= pathingOptions.railsExitCost;
        }

        if (isSwimming) {
            if (swimStart) {
                cost *= pathingOptions.swimCostEnter;
            } else {
                cost *= pathingOptions.swimCost;
            }
        }

        return cost;
    }

    public PathResult getResult() {
        return result;
    }

    /**
     * Callable method for initiating asynchronous task.
     *
     * @return path to follow or null.
     */
    @Override
    public final Path call() {
        try {
            return search();
        } catch (final Exception e) {
            // Log everything, so exceptions of the pathfinding-thread show in Log
            IceAndFire.LOGGER.warn("Pathfinding Exception", e);
        }

        return null;
    }

    /**
     * Perform the search.
     *
     * @return Path of a path to the given location, a best-effort, or null.
     */
    @Nullable
    protected Path search() {
        RayNode bestRayNode = getAndSetupStartNode();

        double bestNodeResultScore = Double.MAX_VALUE;

        while (!nodesOpen.isEmpty()) {
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }

            final RayNode currentRayNode = nodesOpen.poll();

            totalNodesVisited++;

            // Limiting max amount of nodes mapped
            if (totalNodesVisited > IafConfig.maxDragonPathingNodes || totalNodesVisited > maxRange * maxRange) {
                break;
            }
            currentRayNode.setCounterVisited(totalNodesVisited);

            handleDebugOptions(currentRayNode);
            currentRayNode.isClosed();

            if (isAtDestination(currentRayNode)) {
                bestRayNode = currentRayNode;
                result.setPathReachesDestination(true);
                break;
            }

            //  If this is the closest node to our destination, treat it as our best node
            final double nodeResultScore =
                    getNodeResultScore(currentRayNode);
            if (nodeResultScore < bestNodeResultScore) {
                bestRayNode = currentRayNode;
                bestNodeResultScore = nodeResultScore;
            }

            if (!xzRestricted || (currentRayNode.pos.getX() >= minX && currentRayNode.pos.getX() <= maxX && currentRayNode.pos.getZ() >= minZ && currentRayNode.pos.getZ() <= maxZ)) {
                walkCurrentNode(currentRayNode);
            }
        }

        final Path path = finalizePath(bestRayNode);

        handleDebugDraw();

        return path;
    }

    private void handleDebugOptions(final RayNode currentRayNode) {
        if (debugDrawEnabled) {
            addNodeToDebug(currentRayNode);
        }

        if (Pathfinding.isDebug()) {
            IceAndFire.LOGGER.info(String.format("Examining node [%d,%d,%d] ; g=%f ; f=%f",
                    currentRayNode.pos.getX(), currentRayNode.pos.getY(), currentRayNode.pos.getZ(), currentRayNode.getCost(), currentRayNode.getScore()));
        }
    }

    private void addNodeToDebug(final RayNode currentRayNode) {
        debugNodesNotVisited.remove(currentRayNode);
        debugNodesVisited.add(currentRayNode);
    }

    private void addPathNodeToDebug(final RayNode rayNode) {
        debugNodesVisited.remove(rayNode);
        debugNodesPath.add(rayNode);
    }

    private void walkCurrentNode(final RayNode currentRayNode) {
        BlockPos dPos = BLOCKPOS_IDENTITY;
        if (currentRayNode.parent != null) {
            dPos = currentRayNode.pos.subtract(currentRayNode.parent.pos);
        }

        //  On a ladder, we can go 1 straight-up
        if (onLadderGoingUp(currentRayNode, dPos)) {
            walk(currentRayNode, BLOCKPOS_UP);
        }

        //  We can also go down 1, if the lower block is a ladder
        if (onLadderGoingDown(currentRayNode, dPos)) {
            walk(currentRayNode, BLOCKPOS_DOWN);
        }
        if (pathingOptions.canClimb()){
            //If the entity can climb and it needs to climb a block higher than 1 block
            if (getHighest(currentRayNode.pos).getFirst()>1){
                walk(currentRayNode,BLOCKPOS_IDENTITY.above(getHighest(currentRayNode.pos).getFirst()));
            }
            //After entity has climbed something step forward
            if (currentRayNode.parent != null && dPos.getX() == 0 && dPos.getZ() == 0 && dPos.getY() > 1){
                    //Step forwards into the direction we climbed from
                if (getHighest(currentRayNode.parent.pos).getSecond() != null)
                    walk(currentRayNode, getHighest(currentRayNode.parent.pos).getSecond());
            }
        }

        // Only explore downwards when dropping
        if ((currentRayNode.parent == null || !currentRayNode.parent.pos.equals(currentRayNode.pos.below())) && currentRayNode.isCornerNode()) {
            walk(currentRayNode, BLOCKPOS_DOWN);
            return;
        }

        // Walk downwards node if passable
        if ((circumventSizeCheck && isPassable(currentRayNode.pos.below(),true))
                || currentRayNode.parent != null && isPassableBBDown(currentRayNode.parent.pos, currentRayNode.pos.below())) {
            walk(currentRayNode, BLOCKPOS_DOWN);
        }

        // N
        if (dPos.getZ() <= 0) {
            walk(currentRayNode, BLOCKPOS_NORTH);
        }

        // E
        if (dPos.getX() >= 0) {
            walk(currentRayNode, BLOCKPOS_EAST);
        }

        // S
        if (dPos.getZ() >= 0) {
            walk(currentRayNode, BLOCKPOS_SOUTH);
        }

        // W
        if (dPos.getX() <= 0) {
            walk(currentRayNode, BLOCKPOS_WEST);
        }


    }
    private boolean onLadderGoingDown(final RayNode currentRayNode, final BlockPos dPos) {
        return (dPos.getY() <= 0 || dPos.getX() != 0 || dPos.getZ() != 0) && isLadder(currentRayNode.pos.below());
    }

    private void handleDebugDraw() {
        if (debugDrawEnabled) {
            synchronized (debugNodeMonitor) {
                lastDebugNodesNotVisited = debugNodesNotVisited;
                lastDebugNodesVisited = debugNodesVisited;
                lastDebugNodesPath = debugNodesPath;
            }
        }
    }

    private RayNode getAndSetupStartNode() {
        RayNode startRayNode = new RayNode(start, computeHeuristic(start));
        //If the entity is Flying set the start node to the end node
        //Basically letting it's pathfinder do the pathfinding
        if (pathingOptions.isFlying()){
            startRayNode = new RayNode(end, computeHeuristic(end));
        }
        if (isLadder(start)) {
            startRayNode.setLadder();
        } else if (world.getBlockState(start.below()).getMaterial().isLiquid()) {
            startRayNode.setSwimming();
        }

        startRayNode.setOnRails(pathingOptions.canUseRails() && world.getBlockState(start).getBlock() instanceof BaseRailBlock);

        nodesOpen.offer(startRayNode);
        nodesVisited.put(computeNodeKey(start), startRayNode);

        ++totalNodesAdded;

        return startRayNode;
    }

    /**
     * Generate the path to the target node.
     *
     * @param targetRayNode the node to path to.
     * @return the path.
     */

    private Path finalizePath(final RayNode targetRayNode) {
        //  Compute length of path, since we need to allocate an array.  This is cheaper/faster than building a List
        //  and converting it.  Yes, we have targetNode.steps, but I do not want to rely on that being accurate (I might
        //  fudge that value later on for cutoff purposes
        int pathLength = 1;
        int railsLength = 0;
        @Nullable RayNode rayNode = targetRayNode;
        while (rayNode.parent != null) {
            ++pathLength;
            if (rayNode.isOnRails()) {
                ++railsLength;
            }
            rayNode = rayNode.parent;
        }

        final Node[] points = new Node[pathLength];
        points[0] = new PathPointExtended(rayNode.pos);

        @Nullable RayNode nextInPath = null;
        rayNode = targetRayNode;
        while (rayNode.parent != null) {
            if (debugDrawEnabled) {
                addPathNodeToDebug(rayNode);
            }

            --pathLength;

            final BlockPos pos = rayNode.pos;

            if (rayNode.isSwimming()) {
                //  Not truly necessary but helps prevent them spinning in place at swimming nodes
                pos.offset(BLOCKPOS_DOWN);
            }

            final PathPointExtended p = new PathPointExtended(pos);
            if (railsLength >= 8) {
                p.setOnRails(rayNode.isOnRails());
                if (p.isOnRails() && (!rayNode.parent.isOnRails() || rayNode.parent.parent == null)) {
                    p.setRailsEntry();
                } else if (p.isOnRails() && points.length > pathLength + 1) {
                    final PathPointExtended point = ((PathPointExtended) points[pathLength + 1]);
                    if (!point.isOnRails()) {
                        point.setRailsExit();
                    }
                }
            }


            points[pathLength] = p;

            nextInPath = rayNode;
            rayNode = rayNode.parent;
        }

        doDebugPrinting(points);

        return new Path(Arrays.asList(points), getPathTargetPos(targetRayNode), isAtDestination(targetRayNode));
    }

    /**
     * Creates the path for the given points
     *
     * @param finalRayNode
     * @return
     */
    protected BlockPos getPathTargetPos(final RayNode finalRayNode) {
        return finalRayNode.pos;
    }

    /**
     * Turns on debug printing.
     *
     * @param points the points to print.
     */
    private void doDebugPrinting(final Node[] points) {
        if (Pathfinding.isDebug()) {
            IceAndFire.LOGGER.info("Path found:");

            for (final Node p : points) {
                IceAndFire.LOGGER.info(String.format("Step: [%d,%d,%d]", p.x, p.y, p.z));
            }

            IceAndFire.LOGGER.info(String.format("Total Nodes Visited %d / %d", totalNodesVisited, totalNodesAdded));
        }
    }

    /**
     * Compute the heuristic cost ('h' value) of a given position x,y,z.
     * <p>
     * Returning a value of 0 performs a breadth-first search. Returning a value less than actual possible cost to goal guarantees shortest path, but at computational expense.
     * Returning a value exactly equal to the cost to the goal guarantees shortest path and least expense (but generally. only works when path is straight and unblocked). Returning
     * a value greater than the actual cost to goal produces good, but not perfect paths, and is fast. Returning a very high value (such that 'h' is very high relative to 'g') then
     * only 'h' (the heuristic) matters as the search will be a very fast greedy best-first-search, ignoring cost weighting and distance.
     *
     * @param pos Position to compute heuristic from.
     * @return the heuristic.
     */
    protected abstract double computeHeuristic(BlockPos pos);

    /**
     * Return true if the given node is a viable final destination, and the path should generate to here.
     *
     * @param n Node to test.
     * @return true if the node is a viable destination.
     */
    protected abstract boolean isAtDestination(RayNode n);

    /**
     * Compute a 'result score' for the Node; if no destination is determined, the node that had the highest 'result' score is used.
     *
     * @param n Node to test.
     * @return score for the node.
     */
    protected abstract double getNodeResultScore(RayNode n);

    /**
     * "Walk" from the parent in the direction specified by the delta, determining the new x,y,z position for such a move and adding or updating a node, as appropriate.
     *
     * @param parent Node being walked from.
     * @param dPos   Delta from parent, expected in range of [-1..1].
     * @return true if a node was added or updated when attempting to move in the given direction.
     */
    protected final boolean walk(final RayNode parent, BlockPos dPos) {
        BlockPos pos = parent.pos.offset(dPos);

        //  Can we traverse into this node?  Fix the y up
        final int newY = getGroundHeight(parent, pos);

        if (newY < 0) {
            return false;
        }

        boolean corner = false;
        if (pos.getY() != newY) {
            // if the new position is above the current node, we're taking the node directly above
            if (!parent.isCornerNode() && newY - pos.getY() > 0 && (parent.parent == null || !parent.parent.pos.equals(parent.pos.offset(new BlockPos(0, newY - pos.getY(), 0))))) {
                dPos = new BlockPos(0, newY - pos.getY(), 0);
                pos = parent.pos.offset(dPos);
                corner = true;
            }
            // If we're going down, take the air-corner before going to the lower node
            else if (!parent.isCornerNode() && newY - pos.getY() < 0 && (dPos.getX() != 0 || dPos.getZ() != 0) && (parent.parent == null || !parent.pos.below()
                    .equals(parent.parent.pos))) {
                dPos = new BlockPos(dPos.getX(), 0, dPos.getZ());
                pos = parent.pos.offset(dPos);
                corner = true;
            }
            // Fix up normal y but only if we aren't climbing
            else if (!(pathingOptions.canClimb() && dPos.getY() > 1)){
                dPos = dPos.offset(0, newY - pos.getY(), 0);
                pos = new BlockPos(pos.getX(), newY, pos.getZ());
            }
        }

        int nodeKey = computeNodeKey(pos);
        RayNode rayNode = nodesVisited.get(nodeKey);
        if (nodeClosed(rayNode)) {
            //  Early out on closed nodes (closed = expanded from)
            return false;
        }

        final boolean isSwimming = calculateSwimming(world, pos, rayNode);

        if (isSwimming && !pathingOptions.canSwim()) {
            return false;
        }

        final boolean swimStart = isSwimming && !parent.isSwimming();
        final boolean onRoad = false;
        final boolean onRails = pathingOptions.canUseRails() && world.getBlockState(pos).getBlock() instanceof BaseRailBlock;
        final boolean railsExit = !onRails && parent != null && parent.isOnRails();
        //  Cost may have changed due to a jump up or drop
        final double stepCost = computeCost(dPos, isSwimming, onRoad, onRails, railsExit, swimStart, pos);
        final double heuristic = computeHeuristic(pos);
        final double cost = parent.getCost() + stepCost;
        final double score = cost + heuristic;

        if (rayNode == null) {
            rayNode = createNode(parent, pos, nodeKey, isSwimming, heuristic, cost, score);
            rayNode.setOnRails(onRails);
            rayNode.setCornerNode(corner);
        } else if (updateCurrentNode(parent, rayNode, heuristic, cost, score)) {
            return false;
        }

        nodesOpen.offer(rayNode);

        //If we climbed something skip jumpPointSearch
        //This is a workaround so that the path generated doesn't go through blocks
        if (pathingOptions.canClimb() && dPos.getY() >1)
            return true;
        //  Jump Point Search-ish optimization:
        // If this node was a (heuristic-based) improvement on our parent,
        // lets go another step in the same direction...
        //performJumpPointSearch(parent, dPos, node);//this line should be disabled until we can gurantee that infinite recursion won't happen due to walk and performJumpPointSearch calling each other.

        return true;
    }
    private void performJumpPointSearch(final RayNode parent, final BlockPos dPos, final RayNode rayNode) {
        if (allowJumpPointSearchTypeWalk && rayNode.getHeuristic() <= parent.getHeuristic()) {
            walk(rayNode, dPos);
        }
    }
    private RayNode createNode(
            final RayNode parent, final BlockPos pos, final int nodeKey,
            final boolean isSwimming, final double heuristic, final double cost, final double score) {
        final RayNode rayNode;
        rayNode = new RayNode(parent, pos, cost, heuristic, score);
        nodesVisited.put(nodeKey, rayNode);
        if (debugDrawEnabled) {
            debugNodesNotVisited.add(rayNode);
        }

        if (isLadder(pos)) {
            rayNode.setLadder();
        } else if (isSwimming) {
            rayNode.setSwimming();
        }

        totalNodesAdded++;
        rayNode.setCounterAdded(totalNodesAdded);
        return rayNode;
    }

    private boolean updateCurrentNode(final RayNode parent, final RayNode rayNode, final double heuristic, final double cost, final double score) {
        //  This node already exists
        if (score >= rayNode.getScore()) {
            return true;
        }

        if (!nodesOpen.remove(rayNode)) {
            return true;
        }

        rayNode.parent = parent;
        rayNode.setSteps(parent.getSteps() + 1);
        rayNode.setCost(cost);
        rayNode.setHeuristic(heuristic);
        rayNode.setScore(score);
        return false;
    }

    /**
     * Get the height of the ground at the given x,z coordinate, within 1 step of y.
     *
     * @param parent parent node.
     * @param pos    coordinate of block.
     * @return y height of first open, viable block above ground, or -1 if blocked or too far a drop.
     */
    protected int getGroundHeight(final RayNode parent, final BlockPos pos) {
        //  Check (y+1) first, as it's always needed, either for the upper body (level),
        //  lower body (headroom drop) or lower body (jump up)
        if (checkHeadBlock(parent, pos)) {
            return handleTargetNotPassable(parent, pos.above(), world.getBlockState(pos.above()));
        }

        //  Now check the block we want to move to
        final BlockState target = world.getBlockState(pos);
        if (parent != null && !isPassableBB(parent.pos, pos)) {
            return handleTargetNotPassable(parent, pos, target);
        }

        //  Do we have something to stand on in the target space?
        final BlockState below = world.getBlockState(pos.below());
        if(pathingOptions.isFlying()){
             final SurfaceType flyability = isFlyable(below, pos);
             if (flyability == SurfaceType.FLYABLE){
                 return pos.getY();
             }
             else if(flyability == SurfaceType.NOT_PASSABLE){
                 return -1;
             }
        }
        else{
            final SurfaceType walkability = isWalkableSurface(below, pos);
            if (walkability == SurfaceType.WALKABLE) {
                //  Level path
                return pos.getY();
            } else if (walkability == SurfaceType.NOT_PASSABLE) {
                return -1;
            }
        }
        return handleNotStanding(parent, pos, below);
    }

    private int handleNotStanding(@Nullable final RayNode parent, final BlockPos pos, final BlockState below) {
        final boolean isSwimming = parent != null && parent.isSwimming();

        if (below.getMaterial().isLiquid()) {
            return handleInLiquid(pos, below, isSwimming);
        }

        if (isLadder(below.getBlock(), pos.below())) {
            return pos.getY();
        }

        return checkDrop(parent, pos, isSwimming);
    }

    private int checkDrop(@Nullable final RayNode parent, final BlockPos pos, final boolean isSwimming) {
        final boolean canDrop = parent != null && !parent.isLadder();
        boolean isChonker = true;

        if (pathingOptions.canClimb() && parent != null && pos.getY() > parent.pos.getY()+1){
                return pos.getY();
        }
        //  Nothing to stand on
        if(!isChonker){
            if (!canDrop || isSwimming || ((parent.pos.getX() != pos.getX() || parent.pos.getZ() != pos.getZ()) && isPassableBBFull(parent.pos.below())
                    && isWalkableSurface(world.getBlockState(parent.pos.below()), parent.pos.below()) == SurfaceType.DROPABLE)) {
                return -1;
            }
        }

        for (int i = 2; i <= 10; i++) {
            final BlockState below = world.getBlockState(pos.below(i));
            if (isWalkableSurface(below, pos) == SurfaceType.WALKABLE && i <= 4 || below.getMaterial().isLiquid()) {
                //  Level path
                return pos.getY() - i + 1;
            } else if (below.getMaterial() != Material.AIR) {
                return -1;
            }
        }

        return -1;
    }

    private int handleInLiquid(final BlockPos pos, final BlockState below, final boolean isSwimming) {
        if (isSwimming) {
            //  Already swimming in something, or allowed to swim and this is water
            return pos.getY();
        }

        if (pathingOptions.canSwim() && below.getMaterial() == Material.WATER) {
            //  This is water, and we are allowed to swim
            return pos.getY();
        }

        //  Not allowed to swim or this isn't water, and we're on dry land
        return -1;
    }

    private int handleTargetNotPassable(@Nullable final RayNode parent, final BlockPos pos, final BlockState target) {
        final boolean canJump = parent != null && !parent.isLadder() && !parent.isSwimming();
        //  Need to try jumping up one, if we can
        if (!canJump || isWalkableSurface(target, pos) != SurfaceType.WALKABLE) {
            return -1;
        }

        //  Check for headroom in the target space
        if (!isPassableBB(parent.pos, pos.above())) {
            return -1;
        }


        final BlockState parentBelow = world.getBlockState(parent.pos.below());
        final VoxelShape parentBB = parentBelow.getCollisionShape(world, parent.pos.below());

        double parentY = parentBB.max(Direction.Axis.Y);
        double parentMaxY = parentY + parent.pos.below().getY();
        final double targetMaxY = target.getCollisionShape(world, pos).max(Direction.Axis.Y) + pos.getY();
        if (targetMaxY - parentMaxY < jumpHeight) {
            return pos.getY() + 1;
        }
        if (target.getBlock() instanceof StairBlock
                && parentY - HALF_A_BLOCK < jumpHeight
                && target.getValue(StairBlock.HALF) == Half.BOTTOM
                && getXZFacing(parent.pos, pos) == target.getValue(StairBlock.FACING)) {
            return pos.getY() + 1;
        }
        return -1;
    }
    /*
    Get's the maximum height of a climbable column and the direction the column can be climbed from
     */
    private Pair<Integer, BlockPos> getHighest(BlockPos pos){
        int max = 1;
        BlockPos direction = null;
        if (world.getBlockState(pos.north()).canOcclude()) {
            if(climbableTop(pos.north(),Direction.SOUTH)>max){
                max = climbableTop(pos.north(),Direction.SOUTH);
                direction = BLOCKPOS_NORTH;
            }
        }
        if (world.getBlockState(pos.east()).canOcclude()) {
            if(climbableTop(pos.east(),Direction.WEST)>max){
                max = climbableTop(pos.east(),Direction.WEST);
                direction = BLOCKPOS_EAST;
            }
        }
        if (world.getBlockState(pos.south()).canOcclude()) {
            if(climbableTop(pos.south(),Direction.NORTH)>max){
                max = climbableTop(pos.south(),Direction.NORTH);
                direction = BLOCKPOS_SOUTH;
            }
        }
        if (world.getBlockState(pos.west()).canOcclude()) {
            if(climbableTop(pos.west(),Direction.EAST)>max){
                max = climbableTop(pos.west(),Direction.EAST);
                direction = BLOCKPOS_WEST;
            }
        }
        return new Pair<>(max,direction);
    }
    /*
    Keeps going up a column centered at pos checking if in the direction there's free space
     */
    private int climbableTop(BlockPos pos, Direction direction){
        BlockState target = world.getBlockState(pos);
        BlockState origin;
        int i = 0;
        while (target.canOcclude()){
            pos = pos.above();
            target = world.getBlockState(pos);
            origin = world.getBlockState(pos.relative(direction));
            //If the climbable side can't be passed reset height to 0
            if(!isPassable(origin,pos.relative(direction))){
                i = 0;
                break;
            }
            i++;
        }
        return i;
    }
    private boolean checkHeadBlock(@Nullable final RayNode parent, final BlockPos pos) {
        BlockPos localPos = pos;
        final VoxelShape bb = world.getBlockState(localPos).getCollisionShape(world, localPos);
        if (bb.max(Direction.Axis.Y) < 1) {
            localPos = pos.above();
        }

        if (parent == null || !isPassableBB(parent.pos, pos.above())) {
            final VoxelShape bb1 = world.getBlockState(pos.below()).getCollisionShape(world, pos.below());
            final VoxelShape bb2 = world.getBlockState(pos.above()).getCollisionShape(world, pos.above());
            if ((pos.above().getY() + getStartY(bb2, 1)) - (pos.below().getY() + getEndY(bb1, 0)) < entitySizeY) {
                return true;
            }
            if (parent != null) {
                final VoxelShape bb3 = world.getBlockState(parent.pos.below()).getCollisionShape(world, pos.below());
                if ((pos.above().getY() + getStartY(bb2, 1)) - (parent.pos.below().getY() + getEndY(bb3, 0)) < 1.75) {
                    return true;
                }
            }
        }

        if (parent != null) {
            final BlockState hereState = world.getBlockState(localPos.below());
            final VoxelShape bb1 = world.getBlockState(pos).getCollisionShape(world, pos);
            final VoxelShape bb2 = world.getBlockState(localPos.above()).getCollisionShape(world, localPos.above());
            if ((localPos.above().getY() + getStartY(bb2, 1)) - (pos.getY() + getEndY(bb1, 0)) >= 2) {
                return false;
            }

            return hereState.getMaterial().isLiquid() && parent != null && !isPassableBB(parent.pos, pos);
        }
        return false;
    }

    /**
     * Get the start y of a voxelshape.
     *
     * @param bb  the voxelshape.
     * @param def the default if empty.
     * @return the start y.
     */
    private double getStartY(final VoxelShape bb, final int def) {
        return bb.isEmpty() ? def : bb.min(Direction.Axis.Y);
    }

    /**
     * Get the end y of a voxelshape.
     *
     * @param bb  the voxelshape.
     * @param def the default if empty.
     * @return the end y.
     */
    private double getEndY(final VoxelShape bb, final int def) {
        return bb.isEmpty() ? def : bb.max(Direction.Axis.Y);
    }

    /**
     * Is the space passable.
     *
     * @param block the block we are checking.
     * @return true if the block does not block movement.
     */
    protected boolean isPassable(final BlockState block, final BlockPos pos) {
        if (block.getMaterial() != Material.AIR) {
            if (block.getMaterial().blocksMotion()) {
                return pathingOptions.canEnterDoors() && (block.getBlock() instanceof DoorBlock
                        || block.getBlock() instanceof FenceGateBlock
                        || (block.getBlock() instanceof TrapDoorBlock && !(world.getBlockState(pos.above()).getBlock() instanceof TrapDoorBlock)))
                        || block.getBlock() instanceof PressurePlateBlock
                        || block.getBlock() instanceof SignBlock
                        || block.getBlock() instanceof AbstractBannerBlock;
            } else if (block.getBlock() instanceof FireBlock) {
                return false;
            } else {
                final VoxelShape shape = block.getCollisionShape(world, pos);
                return isLadder(block.getBlock(), pos) ||
                        ((shape.isEmpty() || shape.max(Direction.Axis.Y) <= 0.1)
                                && !block.getMaterial().isLiquid()
                                && (block.getBlock() != Blocks.SNOW || block.getValue(SnowLayerBlock.LAYERS) == 1)
                                && block.getBlock() != Blocks.SWEET_BERRY_BUSH);
            }
        }

        return true;
    }

    protected boolean isPassableBBFull(final BlockPos pos) {
        if (circumventSizeCheck) {
            return isPassable(pos, false) && isPassable(pos.above(), true);
        } else {
            for (int i = 0; i <= entitySizeXZ; i++) {
                for (int j = 0; j <= entitySizeY; j++) {
                    for (int k = 0; k <= entitySizeXZ; k++) {
                        if (!isPassable(pos.offset(i, j, k), false)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    protected boolean isPassableBB(final BlockPos parentPos, final BlockPos pos) {
        if (circumventSizeCheck) {
            return isPassable(pos, false) && isPassable(pos.above(), true);
        } else {
            Direction facingDir = getXZFacing(parentPos, pos).getClockWise();
            for (int i = 0; i <= entitySizeXZ; i++) {
                for (int j = 0; j <= entitySizeY; j++) {
                    if (!isPassable(pos.relative(facingDir, i).above(j), false)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /*
        Mobs that break blocks may consider the ground passable.
     */
    protected boolean isPassableBBDown(final BlockPos parentPos, final BlockPos pos) {
        if (circumventSizeCheck) {
            return isPassable(pos, true);
        } else {
            Direction facingDir = getXZFacing(parentPos, pos).getClockWise();
            for (int i = 0; i <= entitySizeXZ; i++) {
                for (int j = 0; j <= entitySizeY; j++) {
                    if (!isPassable(pos.relative(facingDir, i).above(j), false) || pos.getY() <= parentPos.getY()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    protected boolean isPassable(final BlockPos pos, final boolean head) {
        final BlockState state = world.getBlockState(pos);
        final VoxelShape shape = state.getCollisionShape(world, pos);
        if (!shape.isEmpty() && passabilityNavigator != null && passabilityNavigator.isBlockPassable(state, pos, pos)) {
            return true;
        }
        if (shape.isEmpty() || shape.max(Direction.Axis.Y) <= 0.1) {
            return !head || !(state.getBlock() instanceof WoolCarpetBlock) || isLadder(state.getBlock(), pos);
        }

        return isPassable(state, pos);
    }

    /**
     * Is the block solid and can be stood upon.
     *
     * @param blockState Block to check.
     * @param pos        the position.
     * @return true if the block at that location can be walked on.
     */

    protected SurfaceType isFlyable(final BlockState blockState, final BlockPos pos){
        final Block block = blockState.getBlock();
        if (block instanceof FenceBlock
                || block instanceof FenceGateBlock
                || block instanceof WallBlock
                || block instanceof FireBlock
                || block instanceof CampfireBlock
                || block instanceof BambooBlock
                || (blockState.getShape(world, pos).max(Direction.Axis.Y) > 1.0)) {
            return SurfaceType.NOT_PASSABLE;
        }
        final FluidState fluid = world.getFluidState(pos);
        if (fluid != null && !fluid.isEmpty() && (fluid.getType() == Fluids.LAVA || fluid.getType() == Fluids.FLOWING_LAVA)) {
            return SurfaceType.NOT_PASSABLE;
        }
        if (isPassable(blockState,pos)){
            return SurfaceType.FLYABLE;
        }
        return SurfaceType.DROPABLE;
    }

    protected SurfaceType isWalkableSurface(final BlockState blockState, final BlockPos pos) {
        final Block block = blockState.getBlock();
        if (block instanceof FenceBlock
                || block instanceof FenceGateBlock
                || block instanceof WallBlock
                || block instanceof FireBlock
                || block instanceof CampfireBlock
                || block instanceof BambooBlock
                || (blockState.getShape(world, pos).max(Direction.Axis.Y) > 1.0)) {
            return SurfaceType.NOT_PASSABLE;
        }

        final FluidState fluid = world.getFluidState(pos);
        if (fluid != null && !fluid.isEmpty() && (fluid.getType() == Fluids.LAVA || fluid.getType() == Fluids.FLOWING_LAVA)) {
            return SurfaceType.NOT_PASSABLE;
        }

        if (block instanceof SignBlock) {
            return SurfaceType.DROPABLE;
        }

        if (blockState.getMaterial().isSolid()
                || (blockState.getBlock() == Blocks.SNOW && blockState.getValue(SnowLayerBlock.LAYERS) > 1)
                || block instanceof WoolCarpetBlock) {
            return SurfaceType.WALKABLE;
        }

        return SurfaceType.DROPABLE;
    }

    /**
     * Is the block a ladder.
     *
     * @param block block to check.
     * @param pos   location of the block.
     * @return true if the block is a ladder.
     */
    protected boolean isLadder(final Block block, final BlockPos pos) {
        return block.isLadder(this.world.getBlockState(pos), world, pos, entity.get());
    }

    protected boolean isLadder(final BlockPos pos) {
        return isLadder(world.getBlockState(pos).getBlock(), pos);
    }

    /**
     * Sets the pathing options
     *
     * @param pathingOptions the pathing options to set.
     */
    public void setPathingOptions(final PathingOptions pathingOptions) {
        this.pathingOptions = pathingOptions;
    }

    /**
     * Check if we can walk on a surface, drop into, or neither.
     */
    protected enum SurfaceType {
        WALKABLE,
        DROPABLE,
        NOT_PASSABLE,
        FLYABLE
    }
}
