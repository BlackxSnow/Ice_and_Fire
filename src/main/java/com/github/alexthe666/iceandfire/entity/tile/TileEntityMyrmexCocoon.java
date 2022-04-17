package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class TileEntityMyrmexCocoon extends RandomizableContainerBlockEntity {


    private NonNullList<ItemStack> chestContents = NonNullList.withSize(18, ItemStack.EMPTY);

    public TileEntityMyrmexCocoon() {
        super(IafTileEntityRegistry.MYRMEX_COCOON);
    }

    public int getContainerSize() {
        return 18;
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.chestContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    public void load(BlockState bs, CompoundTag compound) {
        super.load(bs, compound);
        this.chestContents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, this.chestContents);
        }
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        if (!this.trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, this.chestContents);
        }

        return compound;
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.myrmex_cocoon");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new ChestMenu(MenuType.GENERIC_9x2, id, player, this, 2);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x2, id, playerInventory, this, 2);
    }


    public int getMaxStackSize() {
        return 64;
    }


    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {

    }

    public void startOpen(Player player) {
        this.unpackLootTable(null);
        player.level.playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), SoundEvents.SLIME_JUMP, SoundSource.BLOCKS, 1, 1, false);
    }

    public void stopOpen(Player player) {
        this.unpackLootTable(null);
        player.level.playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), SoundEvents.SLIME_SQUISH, SoundSource.BLOCKS, 1, 1, false);
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

    public boolean isFull(ItemStack heldStack) {
        for (ItemStack itemstack : chestContents) {
            if (itemstack.isEmpty() || heldStack != null && !heldStack.isEmpty() && itemstack.sameItem(heldStack) && itemstack.getCount() + heldStack.getCount() < itemstack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
}
