package com.github.alexthe666.iceandfire.entity.tile;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityDreadPortal extends BlockEntity implements TickableBlockEntity {
    private long age;
    private BlockPos exitPortal;
    private boolean exactTeleport;

    public TileEntityDreadPortal() {
        super(IafTileEntityRegistry.DREAD_PORTAL);
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putLong("Age", this.age);

        if (this.exitPortal != null) {
            //   compound.setTag("ExitPortal", NBTUtil.createPosTag(this.exitPortal));
        }

        if (this.exactTeleport) {
            compound.putBoolean("ExactTeleport", this.exactTeleport);
        }

        return compound;
    }

    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);
        this.age = compound.getLong("Age");

        if (compound.contains("ExitPortal", 10)) {
            this.exitPortal = BlockPos.ZERO;
        }

        this.exactTeleport = compound.getBoolean("ExactTeleport");
    }

    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return 65536.0D;
    }

    public void tick() {
        ++this.age;
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

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderFace(Direction face) {
        return true;
    }
}
