package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerDragonForge extends AbstractContainerMenu {

    private final Container tileFurnace;
    public int isFire;
    private int cookTime;

    public ContainerDragonForge(int i, Inventory playerInventory) {
        this(i, new SimpleContainer(3), playerInventory, new SimpleContainerData(0));
    }


    public ContainerDragonForge(int id, Container furnaceInventory, Inventory playerInventory, ContainerData vars) {
        super(IafContainerRegistry.DRAGON_FORGE_CONTAINER, id);
        this.tileFurnace = furnaceInventory;
        if(furnaceInventory instanceof TileEntityDragonforge){
            isFire = ((TileEntityDragonforge) furnaceInventory).isFire;
        }else if(IceAndFire.PROXY.getRefrencedTE() instanceof TileEntityDragonforge){
            isFire = ((TileEntityDragonforge) IceAndFire.PROXY.getRefrencedTE()).isFire;
        }
        this.addSlot(new Slot(furnaceInventory, 0, 68, 34));
        this.addSlot(new Slot(furnaceInventory, 1, 86, 34));
        this.addSlot(new FurnaceResultSlot(playerInventory.player, furnaceInventory, 2, 148, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public boolean stillValid(Player playerIn) {
        return this.tileFurnace.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (isFire == 0 && IafRecipeRegistry.getFireForgeRecipe(itemstack1) != null || isFire == 1 && IafRecipeRegistry.getIceForgeRecipe(itemstack1) != null || isFire == 2 && IafRecipeRegistry.getLightningForgeRecipe(itemstack1) != null) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isFire == 0 && IafRecipeRegistry.getFireForgeRecipeForBlood(itemstack1) != null || isFire == 1 && IafRecipeRegistry.getIceForgeRecipeForBlood(itemstack1) != null || isFire == 2 && IafRecipeRegistry.getLightningForgeRecipeForBlood(itemstack1) != null) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
