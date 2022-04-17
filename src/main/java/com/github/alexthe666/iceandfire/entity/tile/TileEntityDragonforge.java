package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockDragonforgeBricks;
import com.github.alexthe666.iceandfire.block.BlockDragonforgeCore;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.inventory.ContainerDragonForge;
import com.github.alexthe666.iceandfire.message.MessageUpdateDragonforge;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class TileEntityDragonforge extends BaseContainerBlockEntity implements TickableBlockEntity, WorldlyContainer {
    private static final int[] SLOTS_TOP = new int[]{0, 1};
    private static final int[] SLOTS_BOTTOM = new int[]{2};
    private static final int[] SLOTS_SIDES = new int[]{0, 1};
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public int isFire;
    public int cookTime;
    net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.core.Direction.UP);
    net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.core.Direction.DOWN);
    net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.core.Direction.WEST);
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    private NonNullList<ItemStack> forgeItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    public int lastDragonFlameTimer = 0;
    private boolean prevAssembled;
    private boolean canAddFlameAgain = true;

    public TileEntityDragonforge() {
        super(IafTileEntityRegistry.DRAGONFORGE_CORE);
    }

    public TileEntityDragonforge(int isFire) {
        super(IafTileEntityRegistry.DRAGONFORGE_CORE);
        this.isFire = isFire;
    }

    public int getContainerSize() {
        return this.forgeItemStacks.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.forgeItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private void updateGrills(boolean grill) {
        for (Direction facing : HORIZONTALS) {
            BlockPos grillPos = this.getBlockPos().relative(facing);
            if (grillMatches(level.getBlockState(grillPos).getBlock())) {
                BlockState grillState = getGrillBlock().defaultBlockState().setValue(BlockDragonforgeBricks.GRILL, grill);
                if (level.getBlockState(grillPos) != grillState) {
                    level.setBlockAndUpdate(grillPos, grillState);
                }
            }
        }
    }

    public Block getGrillBlock(){
        if(isFire == 0){
            return IafBlockRegistry.DRAGONFORGE_FIRE_BRICK;
        }
        if(isFire == 1){
            return IafBlockRegistry.DRAGONFORGE_ICE_BRICK;
        }
        if(isFire == 2){
            return IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK;
        }
        return IafBlockRegistry.DRAGONFORGE_FIRE_BRICK;
    }

    public boolean grillMatches(Block block){
        if(isFire == 0 && block == IafBlockRegistry.DRAGONFORGE_FIRE_BRICK){
            return true;
        }
        if(isFire == 1 && block == IafBlockRegistry.DRAGONFORGE_ICE_BRICK){
            return true;
        }
        if(isFire == 2 && block == IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK){
            return true;
        }
        return false;
    }

    public ItemStack getItem(int index) {
        return this.forgeItemStacks.get(index);
    }

    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.forgeItemStacks, index, count);
    }

    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.forgeItemStacks, index);
    }

    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.forgeItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.sameItem(itemstack) && ItemStack.tagMatches(stack, itemstack);
        this.forgeItemStacks.set(index, stack);

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (index == 0 && !flag || this.cookTime > this.getMaxCookTime(forgeItemStacks.get(0), forgeItemStacks.get(1))) {
            this.cookTime = 0;
            this.setChanged();
        }
    }

    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);
        this.forgeItemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.forgeItemStacks);
        this.cookTime = compound.getInt("CookTime");
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putInt("CookTime", (short) this.cookTime);
        ContainerHelper.saveAllItems(compound, this.forgeItemStacks);
        return compound;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean isBurning() {
        return this.cookTime > 0;
    }

    public int getFireType(Block block){
        if(block == IafBlockRegistry.DRAGONFORGE_FIRE_CORE || block == IafBlockRegistry.DRAGONFORGE_FIRE_CORE_DISABLED){
            return 0;
        }
        if(block == IafBlockRegistry.DRAGONFORGE_ICE_CORE || block == IafBlockRegistry.DRAGONFORGE_ICE_CORE_DISABLED){
            return 1;
        }
        if(block == IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE || block == IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE_DISABLED){
            return 2;
        }
        return 0;
    }

    public String getTypeID(){
        switch (getFireType(this.getBlockState().getBlock())){
            case 0:
                return "fire";
            case 1:
                return "ice";
            case 2:
                return "lightning";
        }
        return "";
    }

    public void tick() {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        isFire = getFireType(this.getBlockState().getBlock());
        if (lastDragonFlameTimer > 0) {
            lastDragonFlameTimer--;
        }
        updateGrills(assembled());
        if (!level.isClientSide) {
            if (prevAssembled != assembled()) {
                BlockDragonforgeCore.setState(isFire, prevAssembled, level, worldPosition);
            }
            prevAssembled = this.assembled();
            if (!assembled()) {
                return;
            }
        }
        if (cookTime > 0 && this.canSmelt() && lastDragonFlameTimer == 0) {
            this.cookTime--;
        }
        if(this.getItem(0).isEmpty() && !level.isClientSide){
            this.cookTime = 0;
        }
        if (!this.level.isClientSide) {
            if (this.isBurning()) {
                if (this.canSmelt()) {
                    ++this.cookTime;
                    if (this.cookTime >= getMaxCookTime(forgeItemStacks.get(0), forgeItemStacks.get(1))) {
                        this.cookTime = 0;
                        this.smeltItem();
                        flag1 = true;
                    }
                } else {
                    if(cookTime > 0){
                        IceAndFire.sendMSGToAll(new MessageUpdateDragonforge(worldPosition.asLong(), cookTime));
                        this.cookTime = 0;
                    }
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = Mth.clamp(this.cookTime - 2, 0, getMaxCookTime(forgeItemStacks.get(0), forgeItemStacks.get(1)));
            }

            if (flag != this.isBurning()) {
                flag1 = true;
            }
        }

        if (flag1) {
            this.setChanged();
        }
        if (!canAddFlameAgain) {
            canAddFlameAgain = true;
        }
    }

    public int getMaxCookTime(ItemStack cookStack, ItemStack bloodStack) {
        ItemStack stack = getCurrentResult(cookStack, bloodStack);
        if (stack.getItem() == Item.byBlock(IafBlockRegistry.ASH) || stack.getItem() == Item.byBlock(IafBlockRegistry.DRAGON_ICE)) {
            return 100;
        }
        return 1000;
    }

    private DragonForgeRecipe getRecipeForInput(ItemStack cookStack) {
        switch (this.isFire) {
            case 0: return IafRecipeRegistry.getFireForgeRecipe(cookStack);
            case 1: return IafRecipeRegistry.getIceForgeRecipe(cookStack);
            case 2: return IafRecipeRegistry.getLightningForgeRecipe(cookStack);
        }

        return null;
    }

    private DragonForgeRecipe getRecipeForBlood(ItemStack bloodStack) {
        switch (this.isFire) {
            case 0: return IafRecipeRegistry.getFireForgeRecipeForBlood(bloodStack);
            case 1: return IafRecipeRegistry.getIceForgeRecipeForBlood(bloodStack);
            case 2: return IafRecipeRegistry.getLightningForgeRecipeForBlood(bloodStack);
        }

        return null;
    }

    private Block getDefaultOutput() {
        if (this.isFire == 1) {
            return IafBlockRegistry.DRAGON_ICE;
        }

        return IafBlockRegistry.ASH;
    }

    private DragonForgeRecipe getCurrentRecipe(ItemStack cookStack, ItemStack bloodStack) {
        DragonForgeRecipe forgeRecipe = getRecipeForInput(cookStack);
        if (
            forgeRecipe != null &&
            // Item input and quantity match
                    forgeRecipe.getInput().test(cookStack) && cookStack.getCount() > 0 &&
            // Blood item and quantity match
                    forgeRecipe.getBlood().test(bloodStack) && bloodStack.getCount() > 0
        ) {
            return forgeRecipe;
        }

        return new DragonForgeRecipe(
            Ingredient.of(cookStack),
                Ingredient.of(bloodStack),
            new ItemStack(getDefaultOutput()),
                getTypeID()
        );
    }

    private ItemStack getCurrentResult(ItemStack cookStack, ItemStack bloodStack) {
        return getCurrentRecipe(cookStack, bloodStack).getOutput();
    }

    public boolean canSmelt() {
        ItemStack cookStack = this.forgeItemStacks.get(0);
        if (cookStack.isEmpty()) {
            return false;
        }

        ItemStack bloodStack = this.forgeItemStacks.get(1);
        DragonForgeRecipe forgeRecipe = getCurrentRecipe(cookStack, bloodStack);
        ItemStack forgeRecipeOutput = forgeRecipe.getOutput();

        if (forgeRecipeOutput.isEmpty()) {
            return false;
        }

        ItemStack outputStack = this.forgeItemStacks.get(2);
        if (
            !outputStack.isEmpty() &&
            !outputStack.sameItem(forgeRecipeOutput)
        ) {
            return false;
        }

        int calculatedOutputCount = outputStack.getCount() + forgeRecipeOutput.getCount();
        return (
            calculatedOutputCount <= this.getMaxStackSize() &&
            calculatedOutputCount <= outputStack.getMaxStackSize()
        );
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }

        ItemStack cookStack = this.forgeItemStacks.get(0);
        ItemStack bloodStack = this.forgeItemStacks.get(1);
        ItemStack outputStack = this.forgeItemStacks.get(2);

        DragonForgeRecipe forgeRecipe = getCurrentRecipe(cookStack, bloodStack);

        if (outputStack.isEmpty()) {
            this.forgeItemStacks.set(2, forgeRecipe.getOutput().copy());
        } else {
            outputStack.grow(forgeRecipe.getOutput().getCount());
        }

        cookStack.shrink(1);
        bloodStack.shrink(1);
    }

    public void startOpen(Player player) {
    }

    public void stopOpen(Player player) {
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        }

        if (index == 1) {
            return getRecipeForBlood(stack) != null;
        }

        return index == 0;
    }

    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        } else {
            return side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (direction == Direction.DOWN && index == 1) {
            Item item = stack.getItem();

            return item == Items.WATER_BUCKET || item == Items.BUCKET;
        }

        return true;
    }

    public int getField(int id) {
        return cookTime;
    }

    public void setField(int id, int value) {
        cookTime = value;
    }

    public int getFieldCount() {
        return 1;
    }

    public void clearContent() {
        this.forgeItemStacks.clear();
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.dragonforge_fire" + DragonType.getNameFromInt(isFire));
    }

    public void transferPower(int i) {
        if(!level.isClientSide){
            if (this.canSmelt()) {
                if (canAddFlameAgain) {
                    cookTime = Math.min(this.getMaxCookTime(forgeItemStacks.get(0), forgeItemStacks.get(1)) + 1, cookTime + i);
                    canAddFlameAgain = false;
                }
            } else {
                cookTime = 0;
            }
            IceAndFire.sendMSGToAll(new MessageUpdateDragonforge(worldPosition.asLong(), cookTime));
        }
        lastDragonFlameTimer = 40;
    }

    private boolean checkBoneCorners(BlockPos pos) {
        return doesBlockEqual(pos.north().east(), IafBlockRegistry.DRAGON_BONE_BLOCK) &&
                doesBlockEqual(pos.north().west(), IafBlockRegistry.DRAGON_BONE_BLOCK) &&
                doesBlockEqual(pos.south().east(), IafBlockRegistry.DRAGON_BONE_BLOCK) &&
                doesBlockEqual(pos.south().west(), IafBlockRegistry.DRAGON_BONE_BLOCK);
    }

    private boolean checkBrickCorners(BlockPos pos) {
        return doesBlockEqual(pos.north().east(), getBrick()) &&
                doesBlockEqual(pos.north().west(), getBrick()) &&
                doesBlockEqual(pos.south().east(), getBrick()) &&
                doesBlockEqual(pos.south().west(), getBrick());
    }

    private boolean checkBrickSlots(BlockPos pos) {
        return doesBlockEqual(pos.north(), getBrick()) &&
                doesBlockEqual(pos.east(), getBrick()) &&
                doesBlockEqual(pos.west(), getBrick()) &&
                doesBlockEqual(pos.south(), getBrick());
    }

    private boolean checkY(BlockPos pos) {
        return doesBlockEqual(pos.above(), getBrick()) &&
                doesBlockEqual(pos.below(), getBrick());
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

    public boolean assembled() {
        return checkBoneCorners(worldPosition.below()) && checkBrickSlots(worldPosition.below()) &&
                checkBrickCorners(worldPosition) && atleastThreeAreBricks(worldPosition) && checkY(worldPosition) &&
                checkBoneCorners(worldPosition.above()) && checkBrickSlots(worldPosition.above());
    }

    private Block getBrick() {
        if(isFire == 0){
            return IafBlockRegistry.DRAGONFORGE_FIRE_BRICK;
        }
        if(isFire == 1){
            return IafBlockRegistry.DRAGONFORGE_ICE_BRICK;
        }
        return IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK;
    }

    private boolean doesBlockEqual(BlockPos pos, Block block) {
        return level.getBlockState(pos).getBlock() == block;
    }

    private boolean atleastThreeAreBricks(BlockPos pos) {
        int count = 0;
        for (Direction facing : HORIZONTALS) {
            if (level.getBlockState(pos.relative(facing)).getBlock() == getBrick()) {
                count++;
            }
        }
        return count > 2;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ContainerDragonForge(id, this, playerInventory, new SimpleContainerData(0));
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new ContainerDragonForge(id, this, player, new SimpleContainerData(0));
    }
}
