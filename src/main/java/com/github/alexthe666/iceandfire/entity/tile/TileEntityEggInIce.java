package com.github.alexthe666.iceandfire.entity.tile;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TileEntityEggInIce extends BlockEntity implements TickableBlockEntity {
    public EnumDragonEgg type;
    public int age;
    public int ticksExisted;
    // boolean to prevent time in a bottle shenanigans
    private boolean spawned;
    @Nullable
    public UUID ownerUUID;

    public TileEntityEggInIce() {
        super(IafTileEntityRegistry.EGG_IN_ICE);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (type != null) {
            tag.putByte("Color", (byte) type.ordinal());
        } else {
            tag.putByte("Color", (byte) 0);
        }
        tag.putInt("Age", (byte) age);
        if (ownerUUID == null) {
            tag.putString("OwnerUUID", "");
        } else {
            tag.putString("OwnerUUID", ownerUUID.toString());
        }

        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state,tag);
        type = EnumDragonEgg.values()[tag.getByte("Color")];
        age = tag.getByte("Age");
        UUID s;

        if (tag.hasUUID("OwnerUUID")) {
            s = tag.getUUID("OwnerUUID");
        } else {
            String s1 = tag.getString("OwnerUUID");
            s = OldUsersConverter.convertMobOwnerIfNecessary(this.level.getServer(), s1);
        }
        if (s != null) {
            ownerUUID = s;
        }
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

    public void spawnEgg() {
        if (type != null) {
            EntityDragonEgg egg = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG, level);
            egg.setEggType(type);
            egg.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5);
            egg.setOwnerId(this.ownerUUID);
            if (!level.isClientSide) {
                level.addFreshEntity(egg);
            }
        }
    }

    @Override
    public void tick() {
        age++;
        if (age >= IafConfig.dragonEggTime && type != null && !spawned) {
            level.destroyBlock(worldPosition, false);
            level.setBlockAndUpdate(worldPosition, Blocks.WATER.defaultBlockState());
            EntityIceDragon dragon = new EntityIceDragon(level);
            dragon.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5);
            dragon.setVariant(type.ordinal() - 4);
            dragon.setGender(new Random().nextBoolean());
            dragon.setTame(true);
            dragon.setHunger(50);
            dragon.setOwnerUUID(ownerUUID);
            if (!level.isClientSide) {
                level.addFreshEntity(dragon);
                spawned = true;
            }

        }
        ticksExisted++;
    }
}
