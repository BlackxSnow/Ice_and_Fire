package com.github.alexthe666.iceandfire.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIAggroLook;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIFollowOwner;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIStareAttack;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAITarget;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.CockatriceAIWander;
import com.github.alexthe666.iceandfire.entity.ai.EntityAIAttackMeleeNoCooldown;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.AgableMob;
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
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntityCockatrice extends TamableAnimal implements IAnimatedEntity, IBlacklistedFromStatues, IVillagerFear {

    public static final Animation ANIMATION_JUMPAT = Animation.create(30);
    public static final Animation ANIMATION_WATTLESHAKE = Animation.create(20);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    public static final Animation ANIMATION_EAT = Animation.create(20);
    public static final float VIEW_RADIUS = 0.6F;
    private static final EntityDataAccessor<Boolean> HEN = SynchedEntityData.defineId(EntityCockatrice.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STARING = SynchedEntityData.defineId(EntityCockatrice.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityCockatrice.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TAMING_PLAYER = SynchedEntityData.defineId(EntityCockatrice.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TAMING_LEVEL = SynchedEntityData.defineId(EntityCockatrice.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCockatrice.class, EntityDataSerializers.INT);
    public float sitProgress;
    public float stareProgress;
    public int ticksStaring = 0;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isSitting;
    private boolean isStaring;
    private CockatriceAIStareAttack aiStare;
    private MeleeAttackGoal aiMelee;
    private boolean isMeleeMode = false;
    private LivingEntity targetedEntity;
    private int clientSideAttackTime;

    public EntityCockatrice(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    protected int getExperienceReward(Player player) {
        return 10;
    }

    public boolean getCanSpawnHere() {
        return this.getRandom().nextInt(IafConfig.cockatriceSpawnCheckChance + 1) == 0;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, aiStare = new CockatriceAIStareAttack(this, 1.0D, 0, 15.0F));
        this.goalSelector.addGoal(2, aiMelee = new EntityAIAttackMeleeNoCooldown(this, 1.5D, false));
        this.goalSelector.addGoal(3, new CockatriceAIFollowOwner(this, 1.0D, 7.0F, 2.0F));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new CockatriceAIWander(this, 1.0D));
        this.goalSelector.addGoal(5, new CockatriceAIAggroLook(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CockatriceAITargetItems(this, false));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(5, new CockatriceAITarget(this, LivingEntity.class, true, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                if (entity instanceof Player ) {
                    return !((Player) entity).isCreative() && !entity.isSpectator();
                }else{
                    return ((entity instanceof Enemy) && EntityCockatrice.this.isTame() && !(entity instanceof Creeper) && !(entity instanceof ZombifiedPiglin) && !(entity instanceof EnderMan) ||
                            ServerEvents.doesScareCockatrice(entity) && !ServerEvents.isChicken(entity));
                }
            }
        }));
        this.goalSelector.removeGoal(aiMelee);
    }
    //TODO: Make cockatrice patrol an area
    public boolean hasRestriction() {
        return this.hasHomePosition && this.getCommand() == 3 || super.hasRestriction();
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        if(worldIn instanceof ServerLevelAccessor && !IafWorldRegistry.isDimensionListedForMobs((ServerLevelAccessor)level)){
            return false;
        }
        return super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    public BlockPos getRestrictCenter() {
        if (this.hasHomePosition && this.getCommand() == 3) {
            return this.homePos;
        }
        return super.getRestrictCenter();
    }

    public boolean isAlliedTo(Entity entityIn) {
        return ServerEvents.isChicken(entityIn) || super.isAlliedTo(entityIn);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source.getEntity() != null && ServerEvents.doesScareCockatrice(source.getEntity())) {
            damage *= 5;
        }
        if (source == DamageSource.IN_WALL) {
            return false;
        }
        return super.hurt(source, damage);
    }

    private boolean canUseStareOn(Entity entity) {
        return (!(entity instanceof IBlacklistedFromStatues) || ((IBlacklistedFromStatues) entity).canBeTurnedToStone()) && !ServerEvents.doesScareCockatrice(entity);
    }

    private void switchAI(boolean melee) {
        if (melee) {
            this.goalSelector.removeGoal(aiStare);
            if (aiMelee != null) {
                this.goalSelector.addGoal(2, aiMelee);
            }
            this.isMeleeMode = true;
        } else {
            this.goalSelector.removeGoal(aiMelee);
            if (aiStare != null) {
                this.goalSelector.addGoal(2, aiStare);
            }
            this.isMeleeMode = false;
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (this.isStaring()) {
            return false;
        }
        if (this.getRandom().nextBoolean()) {
            if (this.getAnimation() != ANIMATION_JUMPAT && this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_JUMPAT);
            }
            return false;
        } else {
            if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_JUMPAT) {
                this.setAnimation(ANIMATION_BITE);
            }
            return false;
        }

    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, IafConfig.cockatriceMaxHealth)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                //ARMOR
                .add(Attributes.ARMOR, 2.0D);
    }

    public boolean canMove() {
        return !this.isOrderedToSit() && !(this.getAnimation() == ANIMATION_JUMPAT && this.getAnimationTick() < 7);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEN, Boolean.valueOf(false));
        this.entityData.define(STARING, Boolean.valueOf(false));
        this.entityData.define(TARGET_ENTITY, Integer.valueOf(0));
        this.entityData.define(TAMING_PLAYER, Integer.valueOf(0));
        this.entityData.define(TAMING_LEVEL, Integer.valueOf(0));
        this.entityData.define(COMMAND, Integer.valueOf(0));
    }

    public boolean hasTargetedEntity() {
        return this.entityData.get(TARGET_ENTITY).intValue() != 0;
    }

    public boolean hasTamingPlayer() {
        return this.entityData.get(TAMING_PLAYER).intValue() != 0;
    }

    @Nullable
    public Entity getTamingPlayer() {
        if (!this.hasTamingPlayer()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(TAMING_PLAYER).intValue());
                if (entity instanceof LivingEntity) {
                    this.targetedEntity = (LivingEntity) entity;
                    return this.targetedEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.level.getEntity(this.entityData.get(TAMING_PLAYER).intValue());
        }
    }

    public void setTamingPlayer(int entityId) {
        this.entityData.set(TAMING_PLAYER, Integer.valueOf(entityId));
    }

    @Nullable
    public LivingEntity getTargetedEntity() {
        boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget().hasEffect(MobEffects.BLINDNESS) || EntityGorgon.isBlindfolded(this.getTarget());
        if (blindness) {
            return null;
        }
        if (!this.hasTargetedEntity()) {
            return null;
        } else if (this.level.isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            } else {
                Entity entity = this.level.getEntity(this.entityData.get(TARGET_ENTITY).intValue());
                if (entity instanceof LivingEntity) {
                    this.targetedEntity = (LivingEntity) entity;
                    return this.targetedEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public void setTargetedEntity(int entityId) {
        this.entityData.set(TARGET_ENTITY, Integer.valueOf(entityId));
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.clientSideAttackTime = 0;
            this.targetedEntity = null;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Hen", this.isHen());
        tag.putBoolean("Staring", this.isStaring());
        tag.putInt("TamingLevel", this.getTamingLevel());
        tag.putInt("TamingPlayer", this.entityData.get(TAMING_PLAYER).intValue());
        tag.putInt("Command", this.getCommand());
        tag.putBoolean("HasHomePosition", this.hasHomePosition);
        if (homePos != null && this.hasHomePosition) {
            tag.putInt("HomeAreaX", homePos.getX());
            tag.putInt("HomeAreaY", homePos.getY());
            tag.putInt("HomeAreaZ", homePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHen(tag.getBoolean("Hen"));
        this.setStaring(tag.getBoolean("Staring"));
        this.setTamingLevel(tag.getInt("TamingLevel"));
        this.setTamingPlayer(tag.getInt("TamingPlayer"));
        this.setCommand(tag.getInt("Command"));
        this.hasHomePosition = tag.getBoolean("HasHomePosition");
        if (hasHomePosition && tag.getInt("HomeAreaX") != 0 && tag.getInt("HomeAreaY") != 0 && tag.getInt("HomeAreaZ") != 0) {
            homePos = new BlockPos(tag.getInt("HomeAreaX"), tag.getInt("HomeAreaY"), tag.getInt("HomeAreaZ"));
        }
    }

    public boolean isOrderedToSit() {
        if (level.isClientSide) {
            boolean isSitting = (this.entityData.get(DATA_FLAGS_ID).byteValue() & 1) != 0;
            this.isSitting = isSitting;
            return isSitting;
        }
        return isSitting;
    }

    public void setOrderedToSit(boolean sitting) {
        super.setSwimming(sitting);
        if (!level.isClientSide) {
            this.isSitting = sitting;
        }
    }

    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setHen(this.getRandom().nextBoolean());
        return spawnDataIn;
    }


    public boolean isHen() {
        return this.entityData.get(HEN).booleanValue();
    }

    public void setHen(boolean hen) {
        this.entityData.set(HEN, Boolean.valueOf(hen));
    }

    public int getTamingLevel() {
        return Integer.valueOf(this.entityData.get(TAMING_LEVEL).intValue());
    }

    public void setTamingLevel(int level) {
        this.entityData.set(TAMING_LEVEL, Integer.valueOf(level));
    }

    public int getCommand() {
        return Integer.valueOf(this.entityData.get(COMMAND).intValue());
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
        if (command == 1) {
            this.setOrderedToSit(true);
        } else {
            this.setOrderedToSit(false);
        }
    }

    public boolean isStaring() {
        if (level.isClientSide) {
            return this.isStaring = Boolean.valueOf(this.entityData.get(STARING).booleanValue());
        }
        return isStaring;
    }

    public void setStaring(boolean staring) {
        this.entityData.set(STARING, Boolean.valueOf(staring));
        if (!level.isClientSide) {
            this.isStaring = staring;
        }
    }

    public void forcePreyToLook(Mob mob) {
        mob.getLookControl().setLookAt(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ(), (float) mob.getMaxHeadYRot(), (float) mob.getMaxHeadXRot());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean flag = player.getItemInHand(hand).getItem() == Items.NAME_TAG || player.getItemInHand(hand).getItem() == Items.LEAD;
        if (flag) {
            return super.mobInteract(player, hand);
        }
        if (player.getItemInHand(hand).getItem() == Items.POISONOUS_POTATO) {
            return super.mobInteract(player, hand);
        }
        if (this.isTame() && this.isOwnedBy(player)) {
            if (FoodUtils.isSeeds(player.getItemInHand(hand)) || player.getItemInHand(hand).getItem() == Items.ROTTEN_FLESH) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(8);
                    this.playSound(SoundEvents.GENERIC_EAT, 1, 1);
                    player.getItemInHand(hand).shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (player.getItemInHand(hand).isEmpty()) {
                if (player.isShiftKeyDown()) {
                    if (this.hasHomePosition) {
                        this.hasHomePosition = false;
                        player.displayClientMessage(new TranslatableComponent("cockatrice.command.remove_home"), true);
                        return InteractionResult.SUCCESS;
                    } else {
                        this.homePos = this.blockPosition();
                        this.hasHomePosition = true;
                        player.displayClientMessage(new TranslatableComponent("cockatrice.command.new_home", homePos.getX(), homePos.getY(), homePos.getZ()), true);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() > 3) {
                        this.setCommand(0);
                    }
                    player.displayClientMessage(new TranslatableComponent("cockatrice.command." + this.getCommand()), true);
                    this.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1);
                    return InteractionResult.SUCCESS;
                }
            }

        }
        return InteractionResult.FAIL;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.getTarget() != null && this.getTarget() instanceof Player) {
            this.setTarget(null);
        }
        if (this.isOrderedToSit() && this.getCommand() != 1) {
            this.setOrderedToSit(false);
        }
        if (this.isOrderedToSit() && this.getTarget() != null) {
            this.setTarget(null);
        }
        if (this.getTarget() != null && this.isAlliedTo(this.getTarget())) {
            this.setTarget(null);
        }
        if (!level.isClientSide) {
            if (this.getTarget() == null || !this.getTarget().isAlive()) {
                this.setTargetedEntity(0);
            } else if (this.isStaring() || this.shouldStareAttack(this.getTarget())) {
                this.setTargetedEntity(this.getTarget().getId());
            }
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && this.getAnimationTick() == 7) {
            double dist = this.distanceToSqr(this.getTarget());
            if (dist < 8) {
                this.getTarget().hurt(DamageSource.mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_JUMPAT && this.getTarget() != null) {
            double dist = this.distanceToSqr(this.getTarget());
            double d0 = this.getTarget().getX() - this.getX();
            double d1 = this.getTarget().getZ() - this.getZ();
            float leap = Mth.sqrt(d0 * d0 + d1 * d1);
            if (dist <= 16.0D && this.isOnGround() && this.getAnimationTick() > 7 && this.getAnimationTick() < 12) {
                Vec3 Vector3d = this.getDeltaMovement();
                Vec3 Vector3d1 = new Vec3(this.getTarget().getX() - this.getX(), 0.0D, this.getTarget().getZ() - this.getZ());
                if (Vector3d1.lengthSqr() > 1.0E-7D) {
                    Vector3d1 = Vector3d1.normalize().scale(0.4D).add(Vector3d.scale(0.2D));
                }
            }
            if (dist < 4 && this.getAnimationTick() > 10) {
                this.getTarget().hurt(DamageSource.mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                if ((double) leap >= 1.0E-4D) {
                    this.getTarget().setDeltaMovement(this.getTarget().getDeltaMovement().add(d0 / (double) leap * 0.800000011920929D + this.getDeltaMovement().x * 0.20000000298023224D, 0, d1 / (double) leap * 0.800000011920929D + this.getDeltaMovement().z * 0.20000000298023224D));
                }
            }
        }
        boolean sitting = isOrderedToSit();
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
        }

        boolean staring = isStaring();
        if (staring && stareProgress < 20.0F) {
            stareProgress += 0.5F;
        } else if (!staring && stareProgress > 0.0F) {
            stareProgress -= 0.5F;
        }
        if (!level.isClientSide) {
            if (staring) {
                ticksStaring++;
            } else {
                ticksStaring = 0;
            }
        }
        if (!level.isClientSide && staring && (this.getTarget() == null || this.shouldMelee())) {
            this.setStaring(false);
        }
        if (this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget().getX(), this.getTarget().getY() + (double) this.getTarget().getEyeHeight(), this.getTarget().getZ(), (float) this.getMaxHeadYRot(), (float) this.getMaxHeadXRot());
            if (!shouldMelee() && this.getTarget() instanceof Mob && !(this.getTarget() instanceof Player)) {
                forcePreyToLook((Mob) this.getTarget());
            }
        }
        boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget().hasEffect(MobEffects.BLINDNESS);
        if (blindness) {
            this.setStaring(false);
        }
        if (!this.level.isClientSide && !blindness && this.getTarget() != null && EntityGorgon.isEntityLookingAt(this, this.getTarget(), VIEW_RADIUS) && EntityGorgon.isEntityLookingAt(this.getTarget(), this, VIEW_RADIUS) && !EntityGorgon.isBlindfolded(this.getTarget())) {
            if (!shouldMelee()) {
                if (!this.isStaring()) {
                    this.setStaring(true);
                } else {
                    int attackStrength = this.getFriendsCount(this.getTarget());
                    if (this.level.getDifficulty() == Difficulty.HARD) {
                        attackStrength++;
                    }
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.WITHER, 10, 2 + Math.min(1, attackStrength)));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, Math.min(4, attackStrength)));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                    if (attackStrength >= 2 && this.getTarget().tickCount % 40 == 0) {
                        this.getTarget().hurt(DamageSource.WITHER, attackStrength - 1);
                    }
                    this.getTarget().setLastHurtByMob(this);
                    if (!this.isTame() && this.getTarget() instanceof Player) {
                        this.setTamingPlayer(this.getTarget().getId());
                        this.setTamingLevel(this.getTamingLevel() + 1);
                        if (this.getTamingLevel() % 100 == 0) {
                            this.level.broadcastEntityEvent(this, (byte) 46);
                        }
                        if (this.getTamingLevel() >= 1000) {
                            this.level.broadcastEntityEvent(this, (byte) 45);
                            if (this.getTamingPlayer() != null && this.getTamingPlayer() instanceof Player)
                                this.tame((Player) this.getTamingPlayer());
                            this.setTarget(null);
                            this.setTamingPlayer(0);
                            this.setTargetedEntity(0);
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide && this.getTarget() == null && this.getRandom().nextInt(300) == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_WATTLESHAKE);
        }
        if (!this.level.isClientSide) {
            if (shouldMelee() && !this.isMeleeMode) {
                switchAI(true);
            }
            if (!shouldMelee() && this.isMeleeMode) {
                switchAI(false);
            }
        }

        if (this.level.isClientSide && this.getTargetedEntity() != null && EntityGorgon.isEntityLookingAt(this, this.getTargetedEntity(), VIEW_RADIUS) && EntityGorgon.isEntityLookingAt(this.getTargetedEntity(), this, VIEW_RADIUS) && this.isStaring()) {
            if (this.hasTargetedEntity()) {
                if (this.clientSideAttackTime < this.getAttackDuration()) {
                    ++this.clientSideAttackTime;
                }

                LivingEntity LivingEntity = this.getTargetedEntity();

                if (LivingEntity != null) {
                    this.getLookControl().setLookAt(LivingEntity, 90.0F, 90.0F);
                    this.getLookControl().tick();
                    double d5 = this.getAttackAnimationScale(0.0F);
                    double d0 = LivingEntity.getX() - this.getX();
                    double d1 = LivingEntity.getY() + (double) (LivingEntity.getBbHeight() * 0.5F) - (this.getY() + (double) this.getEyeHeight());
                    double d2 = LivingEntity.getZ() - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 = d0 / d3;
                    d1 = d1 / d3;
                    d2 = d2 / d3;
                    double d4 = this.random.nextDouble();

                    while (d4 < d3) {
                        d4 += 1.8D - d5 + this.random.nextDouble() * (1.7D - d5);
                        this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + d0 * d4, this.getY() + d1 * d4 + (double) this.getEyeHeight(), this.getZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private int getFriendsCount(LivingEntity attackTarget) {
        if (this.getTarget() == null) {
            return 0;
        }
        float dist = IafConfig.cockatriceChickenSearchLength;
        List<EntityCockatrice> list = level.getEntitiesOfClass(EntityCockatrice.class, this.getBoundingBox().expandTowards(dist, dist, dist));
        int i = 0;
        for (EntityCockatrice cockatrice : list) {
            if (!cockatrice.is(this) && cockatrice.getTarget() != null && cockatrice.getTarget() == this.getTarget()) {
                boolean bothLooking = EntityGorgon.isEntityLookingAt(cockatrice, cockatrice.getTarget(), VIEW_RADIUS) && EntityGorgon.isEntityLookingAt(cockatrice.getTarget(), cockatrice, VIEW_RADIUS);
                if (bothLooking) {
                    i++;
                }
            }
        }
        return i;
    }

    public float getAttackAnimationScale(float f) {
        return ((float) this.clientSideAttackTime + f) / (float) this.getAttackDuration();
    }

    public boolean shouldStareAttack(Entity entity) {
        return this.distanceTo(entity) > 5;
    }

    public int getAttackDuration() {
        return 80;
    }

    private boolean shouldMelee() {
        boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget().hasEffect(MobEffects.BLINDNESS);
        if (this.getTarget() != null) {
            return this.distanceTo(this.getTarget()) < 4D || ServerEvents.doesScareCockatrice(this.getTarget()) || blindness || !this.canUseStareOn(this.getTarget());
        }
        return false;
    }

    @Override
    public void travel(Vec3 motionVec) {
        if (!this.canMove() && !this.isVehicle()) {
            motionVec = motionVec.multiply(0, 1, 0);
        }
        super.travel(motionVec);
    }

    public void playAmbientSound() {
        if (this.getAnimation() == this.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playAmbientSound();
    }

    protected void playHurtSound(DamageSource source) {
        if (this.getAnimation() == this.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playHurtSound(source);
    }

    @Nullable
    @Override
    public AgableMob getBreedOffspring(ServerLevel serverWorld, AgableMob ageable) {
        return null;
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
        return new Animation[]{NO_ANIMATION, ANIMATION_JUMPAT, ANIMATION_WATTLESHAKE, ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_EAT};
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.COCKATRICE_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.COCKATRICE_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.COCKATRICE_DIE;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 45) {
            this.playEffect(true);
        } else if (id == 46) {
            this.playEffect(false);
        } else {
            super.handleEntityEvent(id);
        }
    }

    protected void playEffect(boolean play) {
        ParticleOptions enumparticletypes = ParticleTypes.HEART;

        if (!play) {
            enumparticletypes = ParticleTypes.DAMAGE_INDICATOR;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(enumparticletypes, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
        }
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
