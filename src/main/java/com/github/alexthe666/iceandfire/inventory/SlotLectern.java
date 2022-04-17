package com.github.alexthe666.iceandfire.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotLectern extends Slot {
    private Player thePlayer;
    private int removeCount;

    public SlotLectern(Player player, Container inv, int slotIndex, int xPosition, int yPosition) {
        super(inv, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
    }

    @Override
    public void setChanged() {
        this.container.setChanged();
    }


    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    public ItemStack onTake(Player playerIn, ItemStack stack) {
        this.checkTakeAchievements(stack);
        return super.onTake(playerIn, stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
     * not ore and wood. Typically increases an internal count then calls
     * onCrafting(item).
     */
    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes,
     * not ore and wood.
     */
    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        // thePlayer.addStat(StatList.objectCraftStats[Item.getIdFromItem(stack.getItem())],
        // stack.stackSize);
        this.removeCount = 0;

    }
}