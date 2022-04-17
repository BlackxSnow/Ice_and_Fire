package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;

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

public class ContainerHippogryph extends AbstractContainerMenu {
    private final Container hippogryphInventory;
    private final EntityHippogryph hippogryph;
    private final Player player;

    public ContainerHippogryph(int i, Inventory playerInventory) {
        this(i, new SimpleContainer(18), playerInventory, null);
    }

    public ContainerHippogryph(int id, Container ratInventory, Inventory playerInventory, EntityHippogryph hippogryph) {
        super(IafContainerRegistry.HIPPOGRYPH_CONTAINER, id);
        this.hippogryphInventory = ratInventory;
        if(hippogryph == null && IceAndFire.PROXY.getReferencedMob() instanceof EntityHippogryph){
            hippogryph = (EntityHippogryph)IceAndFire.PROXY.getReferencedMob();
        }
        this.hippogryph = hippogryph;
        this.player = playerInventory.player;
        int i = 3;
        hippogryphInventory.startOpen(player);
        int j = -18;
        this.addSlot(new Slot(hippogryphInventory, 0, 8, 18) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.hasItem();
            }

            public void setChanged() {
                if (ContainerHippogryph.this.hippogryph != null) {
                    ContainerHippogryph.this.hippogryph.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
        });
        this.addSlot(new Slot(hippogryphInventory, 1, 8, 36) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Item.byBlock(Blocks.CHEST) && !this.hasItem();
            }

            public void setChanged() {
                if (ContainerHippogryph.this.hippogryph != null) {
                    ContainerHippogryph.this.hippogryph.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
        });
        this.addSlot(new Slot(hippogryphInventory, 2, 8, 52) {

            public boolean mayPlace(ItemStack stack) {
                return EntityHippogryph.getIntFromArmor(stack) != 0;
            }

            public int getMaxStackSize() {
                return 1;
            }

            public void setChanged() {
                if (ContainerHippogryph.this.hippogryph != null) {
                    ContainerHippogryph.this.hippogryph.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
        });

        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 5; ++l) {
                this.addSlot(new Slot(hippogryphInventory, 3 + l + k * 5, 80 + l * 18, 18 + k * 18) {
                    @OnlyIn(Dist.CLIENT)
                    public boolean isActive() {
                        return ContainerHippogryph.this.hippogryph != null && ContainerHippogryph.this.hippogryph.isChested();
                    }

                    public boolean mayPlace(ItemStack stack) {
                        return ContainerHippogryph.this.hippogryph != null && ContainerHippogryph.this.hippogryph.isChested();
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
            if (index < this.hippogryphInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.hippogryphInventory.getContainerSize(), this.slots.size(), true)) {
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
            } else if (this.hippogryphInventory.getContainerSize() <= 3 || !this.moveItemStackTo(itemstack1, 3, this.hippogryphInventory.getContainerSize(), false)) {
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
        return this.hippogryphInventory.stillValid(playerIn) && this.hippogryph.isAlive() && this.hippogryph.distanceTo(playerIn) < 8.0F;
    }

    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.hippogryphInventory.stopOpen(playerIn);
    }
}