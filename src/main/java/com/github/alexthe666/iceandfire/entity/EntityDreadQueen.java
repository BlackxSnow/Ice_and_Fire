package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerBossEvent;

public class EntityDreadQueen extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {

    public static Animation ANIMATION_SPAWN = Animation.create(40);
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS));
    private int animationTick;
    private Animation currentAnimation;

    public EntityDreadQueen(EntityType t, Level worldIn) {
        super(t, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10,true,false,new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity entity) {
                return DragonUtils.canHostilesTarget(entity);
            }
        }));
        this.targetSelector.addGoal(3, new DreadAITargetNonDread(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity entity) {
                return entity instanceof LivingEntity && DragonUtils.canHostilesTarget(entity);
            }
        }));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, IafConfig.dreadQueenMaxHealth)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, 256.0D)
                //ARMOR
                .add(Attributes.ARMOR, 30.0D);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }


    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }


    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAnimation(ANIMATION_SPAWN);
        this.populateDefaultEquipmentSlots(difficultyIn);
        return data;
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(IafItemRegistry.DREAD_QUEEN_SWORD));
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(IafItemRegistry.DREAD_QUEEN_STAFF));
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPAWN};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldFear() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
