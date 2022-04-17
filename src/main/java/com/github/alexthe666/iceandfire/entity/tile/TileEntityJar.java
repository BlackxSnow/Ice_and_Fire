package com.github.alexthe666.iceandfire.entity.tile;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouse;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouseModel;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieJar;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class TileEntityJar extends BlockEntity implements TickableBlockEntity {

    private static final float PARTICLE_WIDTH = 0.3F;
    private static final float PARTICLE_HEIGHT = 0.6F;
    public boolean hasPixie;
    public boolean prevHasProduced;
    public boolean hasProduced;
    public boolean tamedPixie;
    public UUID pixieOwnerUUID;
    public int pixieType;
    public int ticksExisted;
    public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
    public float rotationYaw;
    public float prevRotationYaw;
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler> downHandler = PixieJarInvWrapper.create(this, Direction.DOWN);
    private Random rand;

    public TileEntityJar() {
        super(IafTileEntityRegistry.PIXIE_JAR);
        this.rand = new Random();
        this.hasPixie = true;
    }

    public TileEntityJar(boolean empty) {
        super(IafTileEntityRegistry.PIXIE_JAR);
        this.rand = new Random();
        this.hasPixie = !empty;
    }

    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putBoolean("HasPixie", hasPixie);
        compound.putInt("PixieType", pixieType);
        compound.putBoolean("HasProduced", hasProduced);
        compound.putBoolean("TamedPixie", tamedPixie);
        if (pixieOwnerUUID != null) {
            compound.putUUID("PixieOwnerUUID", pixieOwnerUUID);
        }
        compound.putInt("TicksExisted", ticksExisted);
        ContainerHelper.saveAllItems(compound, this.pixieItems);
        return compound;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        load(this.getBlockState(), packet.getTag());
        if (!level.isClientSide) {
            IceAndFire.sendMSGToAll(new MessageUpdatePixieHouseModel(worldPosition.asLong(), packet.getTag().getInt("PixieType")));
        }
    }

    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    public void load(BlockState state, CompoundTag compound) {
        hasPixie = compound.getBoolean("HasPixie");
        pixieType = compound.getInt("PixieType");
        hasProduced = compound.getBoolean("HasProduced");
        ticksExisted = compound.getInt("TicksExisted");
        tamedPixie = compound.getBoolean("TamedPixie");
        if (compound.hasUUID("PixieOwnerUUID")){
            pixieOwnerUUID = compound.getUUID("PixieOwnerUUID");
        }
        this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, pixieItems);
        super.load(state, compound);
    }

    @Override
    public void tick() {
        ticksExisted++;
        if (this.level.isClientSide && this.hasPixie) {
            IceAndFire.PROXY.spawnParticle("if_pixie", this.worldPosition.getX() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, this.worldPosition.getY() + (double) (this.rand.nextFloat() * PARTICLE_HEIGHT), this.worldPosition.getZ() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, EntityPixie.PARTICLE_RGB[this.pixieType][0], EntityPixie.PARTICLE_RGB[this.pixieType][1], EntityPixie.PARTICLE_RGB[this.pixieType][2]);
        }
        if (ticksExisted % 24000 == 0 && !this.hasProduced && this.hasPixie) {
            this.hasProduced = true;
            if (!this.getLevel().isClientSide) {
                IceAndFire.sendMSGToAll(new MessageUpdatePixieJar(worldPosition.asLong(), hasProduced));
            }
        }
        if (this.hasPixie && hasProduced != prevHasProduced && ticksExisted > 5) {
            if (!this.getLevel().isClientSide) {
                IceAndFire.sendMSGToAll(new MessageUpdatePixieJar(worldPosition.asLong(), hasProduced));
            }else{
                level.playLocalSound(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5, IafSoundRegistry.PIXIE_HURT, SoundSource.BLOCKS, 1, 1, false);
            }
        }
        prevRotationYaw = rotationYaw;
        if (rand.nextInt(30) == 0) {
            this.rotationYaw = (rand.nextFloat() * 360F) - 180F;
        }
        if (this.hasPixie && ticksExisted % 40 == 0 && this.rand.nextInt(6) == 0 && level.isClientSide) {
            this.level.playLocalSound(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5, IafSoundRegistry.PIXIE_IDLE, SoundSource.BLOCKS, 1, 1, false);
        }
        prevHasProduced = hasProduced;
    }

    public void releasePixie() {
        EntityPixie pixie = new EntityPixie(IafEntityRegistry.PIXIE, this.level);
        pixie.absMoveTo(this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 1F, this.worldPosition.getZ() + 0.5F, new Random().nextInt(360), 0);
        pixie.setItemInHand(InteractionHand.MAIN_HAND, pixieItems.get(0));
        pixie.setColor(this.pixieType);
        level.addFreshEntity(pixie);
        this.hasPixie = false;
        this.pixieType = 0;
        pixie.ticksUntilHouseAI = 500;
        pixie.setTame(this.tamedPixie);
        pixie.setOwnerUUID(this.pixieOwnerUUID);

        if (!level.isClientSide) {
            IceAndFire.sendMSGToAll(new MessageUpdatePixieHouse(worldPosition.asLong(), false, 0));
        }
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return downHandler.cast();
        return super.getCapability(capability, facing);
    }

    private float updateRotation(float float1, float float2, float float3) {
        float f = Mth.wrapDegrees(float2 - float1);

        if (f > float3) {
            f = float3;
        }

        if (f < -float3) {
            f = -float3;
        }

        return float1 + f;
    }
}
