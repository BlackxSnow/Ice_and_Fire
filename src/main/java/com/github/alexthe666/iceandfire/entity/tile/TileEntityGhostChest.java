package com.github.alexthe666.iceandfire.entity.tile;

import java.util.Random;

import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerLevel;

public class TileEntityGhostChest extends ChestBlockEntity {

    public TileEntityGhostChest() {
        super(IafTileEntityRegistry.GHOST_CHEST);
    }

    public void load(BlockState state, CompoundTag nbt) {
        super.load(state, nbt);
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        return compound;
    }
    public void startOpen(Player player) {
        super.startOpen(player);
        if(this.level.getDifficulty() != Difficulty.PEACEFUL){
            EntityGhost ghost = IafEntityRegistry.GHOST.create(level);
            Random random = new Random();
            ghost.absMoveTo(this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F, random.nextFloat() * 360F, 0);
            if(!this.level.isClientSide){
                ghost.finalizeSpawn((ServerLevel)level, level.getCurrentDifficultyAt(this.worldPosition), MobSpawnType.SPAWNER, null, null);
                if(!player.isCreative()){
                    ghost.setTarget(player);
                }
                ghost.setPersistenceRequired();
                level.addFreshEntity(ghost);
            }
            ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
            ghost.restrictTo(this.worldPosition, 4);
            ghost.setFromChest(true);
        }
    }

    protected void signalOpenCount() {
        super.signalOpenCount();
        this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());

    }
}
