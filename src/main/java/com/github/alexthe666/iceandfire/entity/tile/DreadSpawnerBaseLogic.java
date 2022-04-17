package com.github.alexthe666.iceandfire.entity.tile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.google.common.collect.Lists;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class DreadSpawnerBaseLogic extends BaseSpawner {

    private final List<SpawnData> potentialSpawns = Lists.newArrayList();
    private int spawnDelay = 20;
    private SpawnData spawnData = new SpawnData();
    private double mobRotation;
    private double prevMobRotation;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private Entity cachedEntity;
    private int maxNearbyEntities = 6;
    private int activatingRangeFromPlayer = 16;
    private int spawnRange = 4;

    @Nullable
    private ResourceLocation getEntityId() {
        String s = this.spawnData.getTag().getString("id");
        return StringUtil.isNullOrEmpty(s) ? null : new ResourceLocation(s);
    }

    public void setEntityId(@Nullable ResourceLocation id) {
        if (id != null) {
            this.spawnData.getTag().putString("id", id.toString());
        }
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    private boolean isActivated() {
        BlockPos blockpos = this.getPos();
        return this.getLevel().hasNearbyAlivePlayer((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, this.activatingRangeFromPlayer);
    }

    public void updateSpawner() {
        if (!this.isActivated()) {
            this.prevMobRotation = this.mobRotation;
        } else {
            Level world = this.getLevel();
            BlockPos blockpos = this.getPos();

            if (this.getLevel().isClientSide) {
                double d3 = (float) blockpos.getX() + this.getLevel().random.nextFloat();
                double d4 = (float) blockpos.getY() + this.getLevel().random.nextFloat();
                double d5 = (float) blockpos.getZ() + this.getLevel().random.nextFloat();
                this.getLevel().addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                IceAndFire.PROXY.spawnParticle("dread_torch", d3, d4, d5, 0.0D, 0.0D, 0.0D);
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
            } else {
                if (this.spawnDelay == -1) {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    CompoundTag compoundnbt = this.spawnData.getTag();
                    Optional<EntityType<?>> optional = EntityType.by(compoundnbt);
                    if (!optional.isPresent()) {
                        this.resetTimer();
                        return;
                    }

                    ListTag listnbt = compoundnbt.getList("Pos", 6);
                    int j = listnbt.size();
                    double d0 = j >= 1 ? listnbt.getDouble(0) : (double) blockpos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double d1 = j >= 2 ? listnbt.getDouble(1) : (double) (blockpos.getY() + world.random.nextInt(3) - 1);
                    double d2 = j >= 3 ? listnbt.getDouble(2) : (double) blockpos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    if (world.noCollision(optional.get().getAABB(d0, d1, d2)) && SpawnPlacements.checkSpawnRules(optional.get(), (ServerLevelAccessor)world, MobSpawnType.SPAWNER, new BlockPos(d0, d1, d2), world.getRandom())) {
                        ServerLevel serverworld = (ServerLevel)world;
                        Entity entity = EntityType.loadEntityRecursive(compoundnbt, world, (p_221408_6_) -> {
                            p_221408_6_.moveTo(d0, d1, d2, p_221408_6_.yRot, p_221408_6_.xRot);
                            return p_221408_6_;
                        });
                        if (entity == null) {
                            this.resetTimer();
                            return;
                        }

                        int k = world.getEntitiesOfClass(entity.getClass(), (new AABB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() + 1, blockpos.getY() + 1, blockpos.getZ() + 1)).inflate(this.spawnRange)).size();
                        if (k >= this.maxNearbyEntities) {
                            this.resetTimer();
                            return;
                        }

                        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof Mob) {
                            Mob mobentity = (Mob) entity;
                            if (!net.minecraftforge.event.ForgeEventFactory.canEntitySpawnSpawner(mobentity, world, (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), this)) {
                                continue;
                            }

                            if (this.spawnData.getTag().size() == 1 && this.spawnData.getTag().contains("id", 8)){
                                ((Mob) entity).finalizeSpawn(serverworld, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, null);
                            }
                        }

                        this.func_221409_a(entity);
                        world.levelEvent(2004, blockpos, 0);
                        if (entity instanceof Mob) {
                            ((Mob) entity).spawnAnim();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.resetTimer();
                }
            }
        }
    }

    private void func_221409_a(Entity entityIn) {
        if (this.getLevel().addFreshEntity(entityIn)) {
            for (Entity entity : entityIn.getPassengers()) {
                this.func_221409_a(entity);
            }

        }
    }

    private void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getLevel().random.nextInt(i);
        }

        if (!this.potentialSpawns.isEmpty()) {
            this.setNextSpawnData(spawnData);
        }

        this.broadcastEvent(1);
    }

    public void load(CompoundTag nbt) {
        this.spawnDelay = nbt.getShort("Delay");
        this.potentialSpawns.clear();
        if (nbt.contains("SpawnPotentials", 9)) {
            ListTag listnbt = nbt.getList("SpawnPotentials", 10);

            for (int i = 0; i < listnbt.size(); ++i) {
                this.potentialSpawns.add(new SpawnData(listnbt.getCompound(i)));
            }
        }

        if (nbt.contains("SpawnData", 10)) {
            this.setNextSpawnData(new SpawnData(1, nbt.getCompound("SpawnData")));
        } else if (!this.potentialSpawns.isEmpty()) {
            this.setNextSpawnData(WeighedRandom.getRandomItem(this.getLevel().random, this.potentialSpawns));
        }

        if (nbt.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.contains("SpawnRange", 99)) {
            this.spawnRange = nbt.getShort("SpawnRange");
        }

        if (this.getLevel() != null) {
            this.cachedEntity = null;
        }

    }

    public CompoundTag save(CompoundTag compound) {
        ResourceLocation resourcelocation = this.getEntityId();
        if (resourcelocation == null) {
            return compound;
        } else {
            compound.putShort("Delay", (short) this.spawnDelay);
            compound.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
            compound.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
            compound.putShort("SpawnCount", (short) this.spawnCount);
            compound.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
            compound.putShort("RequiredPlayerRange", (short) this.activatingRangeFromPlayer);
            compound.putShort("SpawnRange", (short) this.spawnRange);
            compound.put("SpawnData", this.spawnData.getTag().copy());
            ListTag listnbt = new ListTag();
            if (this.potentialSpawns.isEmpty()) {
                listnbt.add(this.spawnData.save());
            } else {
                for (SpawnData weightedspawnerentity : this.potentialSpawns) {
                    listnbt.add(weightedspawnerentity.save());
                }
            }

            compound.put("SpawnPotentials", listnbt);
            return compound;
        }
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean onEventTriggered(int delay) {
        if (delay == 1 && this.getLevel().isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getOrCreateDisplayEntity() {
        if (this.cachedEntity == null) {
            this.cachedEntity = EntityType.loadEntityRecursive(this.spawnData.getTag(), this.getLevel(), Function.identity());
            if (this.spawnData.getTag().size() == 1 && this.spawnData.getTag().contains("id", 8) && this.cachedEntity instanceof Mob) {
            }
        }

        return this.cachedEntity;
    }

    public void setNextSpawnData(SpawnData nextSpawnData) {
        this.spawnData = nextSpawnData;
    }

    public abstract void broadcastEvent(int id);

    public abstract BlockPos getPos();

    @OnlyIn(Dist.CLIENT)
    public double getSpin() {
        return this.mobRotation;
    }

    @OnlyIn(Dist.CLIENT)
    public double getoSpin() {
        return this.prevMobRotation;
    }

}
