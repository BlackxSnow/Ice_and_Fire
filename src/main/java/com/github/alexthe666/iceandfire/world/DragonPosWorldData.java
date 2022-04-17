package com.github.alexthe666.iceandfire.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;

public class DragonPosWorldData extends SavedData {

    private static final String IDENTIFIER = "iceandfire_dragonPositions";
    protected final Map<UUID, BlockPos> lastDragonPositions = new HashMap<>();
    private Level world;
    private int tickCounter;

    public DragonPosWorldData() {
        super(IDENTIFIER);
    }

    public DragonPosWorldData(Level world) {
        super(IDENTIFIER);
        this.world = world;
        this.setDirty();
    }

    public static DragonPosWorldData get(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(world.dimension());

            DimensionDataStorage storage = overworld.getDataStorage();
            DragonPosWorldData data = storage.computeIfAbsent(DragonPosWorldData::new, IDENTIFIER);
            if (data != null) {
                data.world = world;
                data.setDirty();
            }
            return data;
        }
        return null;
    }

    public void addDragon(UUID uuid, BlockPos pos) {
        lastDragonPositions.put(uuid, pos);
        this.setDirty();
    }

    public void removeDragon(UUID uuid) {
        lastDragonPositions.remove(uuid);
        this.setDirty();
    }

    public BlockPos getDragonPos(UUID uuid) {
        return lastDragonPositions.get(uuid);
    }

    public void debug() {
        IceAndFire.LOGGER.warn(lastDragonPositions.toString());
    }


    public void tick() {
        ++this.tickCounter;
    }

    public void load(CompoundTag nbt) {
        this.tickCounter = nbt.getInt("Tick");
        ListTag nbttaglist = nbt.getList("DragonMap", 10);
        this.lastDragonPositions.clear();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundTag CompoundNBT = nbttaglist.getCompound(i);
            UUID uuid = CompoundNBT.getUUID("DragonUUID");
            BlockPos pos = new BlockPos(CompoundNBT.getInt("DragonPosX"), CompoundNBT.getInt("DragonPosY"), CompoundNBT.getInt("DragonPosZ"));
            this.lastDragonPositions.put(uuid, pos);
        }
    }

    public CompoundTag save(CompoundTag compound) {
        compound.putInt("Tick", this.tickCounter);
        ListTag nbttaglist = new ListTag();
        for (Map.Entry<UUID, BlockPos> pair : lastDragonPositions.entrySet()) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.putUUID("DragonUUID", pair.getKey());
            CompoundNBT.putInt("DragonPosX", pair.getValue().getX());
            CompoundNBT.putInt("DragonPosY", pair.getValue().getY());
            CompoundNBT.putInt("DragonPosZ", pair.getValue().getZ());
            nbttaglist.add(CompoundNBT);
        }
        compound.put("DragonMap", nbttaglist);
        return compound;
    }
}
