package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class TileEntityDragonforgeBrick extends BlockEntity {

    public TileEntityDragonforgeBrick() {
        super(IafTileEntityRegistry.DRAGONFORGE_BRICK);
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (getConnectedTileEntity() != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getConnectedTileEntity().getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

    private ICapabilityProvider getConnectedTileEntity() {
        for (Direction facing : Direction.values()) {
            if (level.getBlockEntity(worldPosition.relative(facing)) != null && level.getBlockEntity(worldPosition.relative(facing)) instanceof TileEntityDragonforge) {
                return level.getBlockEntity(worldPosition.relative(facing));
            }
        }
        return null;
    }

}
