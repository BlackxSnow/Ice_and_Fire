package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;

public class EntityDreadScuttler extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {

    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(EntityDreadScuttler.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityDreadScuttler.class, EntityDataSerializers.BYTE);
    private static final float INITIAL_WIDTH = 1.5F;
    private static final float INITIAL_HEIGHT = 1.3F;
    public static Animation ANIMATION_SPAWN = Animation.create(40);
    public static Animation ANIMATION_BITE = Animation.create(15);
    private int animationTick;
    private Animation currentAnimation;
    private float firstWidth = -1.0F;
    private float firstHeight = -1.0F;

    public EntityDreadScuttler(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, IDreadMob.class));
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
                .add(Attributes.MAX_HEALTH, 40.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                //ARMOR
                .add(Attributes.ARMOR, 10.0D);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, Byte.valueOf((byte) 0));
        this.entityData.define(SCALE, Float.valueOf(1F));
    }

    public float getScale() {
        return Float.valueOf(this.entityData.get(SCALE).floatValue());
    }

    public void setScale(float scale) {
        this.entityData.set(SCALE, Float.valueOf(scale));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Scale", this.getScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setScale(compound.getFloat("Scale"));
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_BITE);
        }
        return true;
    }

    public void aiStep() {
        super.aiStep();
        if (Math.abs(firstWidth - INITIAL_WIDTH * getScale()) > 0.01F || Math.abs(firstHeight - INITIAL_HEIGHT * getScale()) > 0.01F) {
            firstWidth = INITIAL_WIDTH * getScale();
            firstHeight = INITIAL_HEIGHT * getScale();
        }
        if (!this.level.isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = level.getBlockState(this.blockPosition().below());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getBoundingBox().minY, this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }
        if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 4 && this.canSee(this.getTarget())) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_BITE);
            }
            this.lookAt(this.getTarget(), 360, 80);
            if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 6) {
                this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                this.getTarget().knockback(0.25F, this.getX() - this.getTarget().getX(), this.getZ() - this.getTarget().getZ());
            }
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    public void setInWeb() {
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return potioneffectIn.getEffect() != MobEffects.POISON && super.canBeAffected(potioneffectIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING).byteValue() & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING).byteValue();

        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(CLIMBING, Byte.valueOf(b0));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAnimation(ANIMATION_SPAWN);
        this.setScale(0.5F + random.nextFloat() * 1.15F);
        return data;
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
        return new Animation[]{ANIMATION_SPAWN, ANIMATION_BITE};
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
    public Entity getCommander() {
        return null;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        return entityIn instanceof IDreadMob || super.isAlliedTo(entityIn);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SPIDER_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    protected float getVoicePitch() {
        return super.getVoicePitch() * 0.70F;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(IafSoundRegistry.MYRMEX_WALK, 0.25F, 1.0F);
    }

    public float getScale() {
        return getScale();
    }

}