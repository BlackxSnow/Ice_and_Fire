package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.block.BlockDragonforgeInput;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TileEntityDragonforgeInput extends BlockEntity implements TickableBlockEntity {
    private static final int LURE_DISTANCE = 50;
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private int ticksSinceDragonFire;
    private TileEntityDragonforge core = null;

    public TileEntityDragonforgeInput() {
        super(IafTileEntityRegistry.DRAGONFORGE_INPUT);
    }

    public void onHitWithFlame() {
        if (core != null) {
            core.transferPower(1);
        }
    }

    @Override
    public void tick() {
        if (core == null) {
            core = getConnectedTileEntity();
        }
        if (ticksSinceDragonFire > 0) {
            ticksSinceDragonFire--;
        }
        if ((ticksSinceDragonFire == 0 || core == null) && this.isActive()) {
            BlockEntity tileentity = level.getBlockEntity(worldPosition);
            level.setBlockAndUpdate(worldPosition, getDeactivatedState());
            if (tileentity != null) {
                tileentity.clearRemoved();
                level.setBlockEntity(worldPosition, tileentity);
            }
        }
        if (isAssembled())
            lureDragons();

    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        load(this.getBlockState(), packet.getTag());
    }

    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    protected void lureDragons() {
        Vec3 targetPosition = new Vec3(
            this.getBlockPos().getX() + 0.5F,
            this.getBlockPos().getY() + 0.5F,
            this.getBlockPos().getZ() + 0.5F
        );

        AABB searchArea = new AABB(
            (double) worldPosition.getX() - LURE_DISTANCE,
            (double) worldPosition.getY() - LURE_DISTANCE,
            (double) worldPosition.getZ() - LURE_DISTANCE,
            (double) worldPosition.getX() + LURE_DISTANCE,
            (double) worldPosition.getY() + LURE_DISTANCE,
            (double) worldPosition.getZ() + LURE_DISTANCE
        );

        boolean dragonSelected = false;
        for (EntityDragonBase dragon : level.getEntitiesOfClass(EntityDragonBase.class, searchArea)) {
            if (!dragonSelected &&
                // Dragon Checks
                getDragonType() == DragonType.getIntFromType(dragon.dragonType) &&
                (dragon.isChained() || dragon.isTame()) &&
                canSeeInput(dragon, targetPosition)
            ) {
                dragon.burningTarget = this.worldPosition;
                dragonSelected = true;

            } else if(dragon.burningTarget == this.worldPosition) {
                dragon.burningTarget = null;
                dragon.setBreathingFire(false);
            }
        }
    }

    public boolean isAssembled() {
        return (core != null &&
            core.assembled() &&
            core.canSmelt());
    }

    public void resetCore() {
        core = null;
    }

    private boolean canSeeInput(EntityDragonBase dragon, Vec3 target) {
        if (target != null) {
            HitResult rayTrace = this.level.clip(new ClipContext(dragon.getHeadPosition(), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, dragon));
            if (rayTrace != null && rayTrace.getLocation() != null) {
                double distance = target.distanceTo(rayTrace.getLocation());
                return distance < 1.0F;
            }
        }
        return false;
    }

    private BlockState getDeactivatedState() {
        switch (getDragonType()){
            case 0:
                return IafBlockRegistry.DRAGONFORGE_FIRE_INPUT.defaultBlockState().setValue(BlockDragonforgeInput.ACTIVE, false);
            case 1:
                return IafBlockRegistry.DRAGONFORGE_ICE_INPUT.defaultBlockState().setValue(BlockDragonforgeInput.ACTIVE, false);
            case 2:
                return IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT.defaultBlockState().setValue(BlockDragonforgeInput.ACTIVE, false);

        }
        return IafBlockRegistry.DRAGONFORGE_FIRE_INPUT.defaultBlockState().setValue(BlockDragonforgeInput.ACTIVE, false);
    }

    private int getDragonType() {
        if(level.getBlockState(worldPosition).getBlock() == IafBlockRegistry.DRAGONFORGE_FIRE_INPUT){
            return 0;
        }
        if(level.getBlockState(worldPosition).getBlock() == IafBlockRegistry.DRAGONFORGE_ICE_INPUT){
            return 1;
        }
        if(level.getBlockState(worldPosition).getBlock() == IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT){
            return 2;
        }
        return 0;
    }

    private boolean isActive() {
        return level.getBlockState(worldPosition).getBlock() instanceof BlockDragonforgeInput && level.getBlockState(worldPosition).getValue(BlockDragonforgeInput.ACTIVE);
    }

    private void setActive() {
        BlockEntity tileentity = level.getBlockEntity(worldPosition);
        level.setBlockAndUpdate(this.worldPosition, getDeactivatedState().setValue(BlockDragonforgeInput.ACTIVE, true));
        if (tileentity != null) {
            tileentity.clearRemoved();
            level.setBlockEntity(worldPosition, tileentity);
        }
    }

    private TileEntityDragonforge getConnectedTileEntity() {
        for (Direction facing : HORIZONTALS) {
            if (level.getBlockEntity(worldPosition.relative(facing)) != null && level.getBlockEntity(worldPosition.relative(facing)) instanceof TileEntityDragonforge) {
                return (TileEntityDragonforge) level.getBlockEntity(worldPosition.relative(facing));
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    @Override
    @javax.annotation.Nullable
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (core != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return core.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

}
