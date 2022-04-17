package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerHippocampus extends AbstractContainerMenu {
    private final Container hippocampusInventory;
    private final EntityHippocampus hippocampus;
    private final Player player;

    public ContainerHippocampus(int i, Inventory playerInventory) {
        this(i, new SimpleContainer(18), playerInventory, null);
    }

    public ContainerHippocampus(int id, Container ratInventory, Inventory playerInventory, EntityHippocampus hippocampus) {
        super(IafContainerRegistry.HIPPOCAMPUS_CONTAINER, id);
        this.hippocampusInventory = ratInventory;
        if(hippocampus == null && IceAndFire.PROXY.getReferencedMob() instanceof EntityHippocampus){
            hippocampus = (EntityHippocampus)IceAndFire.PROXY.getReferencedMob();
        }
        this.hippocampus = hippocampus;
        this.player = playerInventory.player;
        int i = 3;
        hippocampusInventory.startOpen(player);
        int j = -18;
        this.addSlot(new Slot(hippocampusInventory, 0, 8, 18) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasItem();
            }

            public void setChanged() {
                if (ContainerHippocampus.this.hippocampus != null) {
                    ContainerHippocampus.this.hippocampus.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
        });
        this.addSlot(new Slot(hippocampusInventory, 1, 8, 36) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Item.byBlock(Blocks.CHEST) && !this.hasItem();
            }

            public void setChanged() {
                if (ContainerHippocampus.this.hippocampus != null) {
                    ContainerHippocampus.this.hippocampus.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
        });
        this.addSlot(new Slot(hippocampusInventory, 2, 8, 52) {

            public boolean mayPlace(ItemStack stack) {
                return EntityHippocampus.getIntFromArmor(stack) != 0;
            }

            public void setChanged() {
                if (ContainerHippocampus.this.hippocampus != null) {
                    ContainerHippocampus.this.hippocampus.refreshInventory();
                }
            }

            public int getMaxStackSize() {
                return 1;
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
        });

        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 5; ++l) {
                this.addSlot(new Slot(hippocampusInventory, 3 + l + k * 5, 80 + l * 18, 18 + k * 18) {
                    @OnlyIn(Dist.CLIENT)
                    public boolean isActive() {
                        return ContainerHippocampus.this.hippocampus != null && ContainerHippocampus.this.hippocampus.isChested();
                    }

                    public boolean mayPlace(ItemStack stack) {
                        return ContainerHippocampus.this.hippocampus != null && ContainerHippocampus.this.hippocampus.isChested();
                    }
                });
            }
        }

        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(player.inventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
        }

    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.hippocampusInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.hippocampusInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(2).mayPlace(itemstack1) && !this.getSlot(2).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(0).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.hippocampusInventory.getContainerSize() <= 3 || !this.moveItemStackTo(itemstack1, 3, this.hippocampusInventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public boolean stillValid(Player playerIn) {
        return this.hippocampusInventory.stillValid(playerIn) && this.hippocampus.isAlive() && this.hippocampus.distanceTo(playerIn) < 8.0F;
    }

    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.hippocampusInventory.stopOpen(playerIn);
    }
}