package com.github.alexthe666.iceandfire.entity.tile;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.SpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BaseSpawner;

public class TileEntityDreadSpawner extends BlockEntity implements TickableBlockEntity {
    private final DreadSpawnerBaseLogic spawnerLogic = new DreadSpawnerBaseLogic() {
        public void broadcastEvent(int id) {
            TileEntityDreadSpawner.this.level.blockEvent(TileEntityDreadSpawner.this.worldPosition, Blocks.SPAWNER, id, 0);
        }

        public Level getLevel() {
            return TileEntityDreadSpawner.this.level;
        }

        public BlockPos getPos() {
            return TileEntityDreadSpawner.this.worldPosition;
        }

        public void setNextSpawnData(SpawnData nextSpawnData) {
            super.setNextSpawnData(nextSpawnData);

            if (this.getLevel() != null) {
                BlockState BlockState = this.getLevel().getBlockState(this.getPos());
                this.getLevel().sendBlockUpdated(TileEntityDreadSpawner.this.worldPosition, BlockState, BlockState, 4);
            }
        }
    };

    public TileEntityDreadSpawner() {
        super(IafTileEntityRegistry.DREAD_SPAWNER);
    }

    public void load(BlockState blockstate, CompoundTag compound) {
        super.load(blockstate, compound);
        this.spawnerLogic.load(compound);
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        this.spawnerLogic.save(compound);
        return compound;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void tick() {
        this.spawnerLogic.updateSpawner();
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
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


    public boolean triggerEvent(int id, int type) {
        return this.spawnerLogic.onEventTriggered(id) || super.triggerEvent(id, type);
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public BaseSpawner getSpawnerBaseLogic() {
        return this.spawnerLogic;
    }
}