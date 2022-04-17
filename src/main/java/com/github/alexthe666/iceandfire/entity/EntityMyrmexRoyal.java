package com.github.alexthe666.iceandfire.entity;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.google.common.base.Predicate;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class EntityMyrmexRoyal extends EntityMyrmexBase {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_STING = Animation.create(15);
    public static final ResourceLocation DESERT_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_royal_desert");
    public static final ResourceLocation JUNGLE_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_royal_jungle");
    private static final ResourceLocation TEXTURE_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_royal.png");
    private static final ResourceLocation TEXTURE_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_royal.png");
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityMyrmexRoyal.class, EntityDataSerializers.BOOLEAN);
    public int releaseTicks = 0;
    public int daylightTicks = 0;
    public float flyProgress;
    public EntityMyrmexRoyal mate;
    private int hiveTicks = 0;
    private int breedingTicks = 0;
    private boolean isFlying;
    private boolean isLandNavigator;
    private boolean isMating = false;

    public EntityMyrmexRoyal(EntityType t, Level worldIn) {
        super(t, worldIn);
        this.switchNavigator(true);
    }

    @Override
    protected VillagerTrades.ItemListing[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_ROYAL.get(1) : MyrmexTrades.DESERT_ROYAL.get(1);
    }

    @Override
    protected VillagerTrades.ItemListing[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_ROYAL.get(2) : MyrmexTrades.DESERT_ROYAL.get(2);
    }

    public static BlockPos getPositionRelativetoGround(Entity entity, Level world, double x, double z, Random rand) {
        BlockPos pos = new BlockPos(x, entity.getY(), z);
        for (int yDown = 0; yDown < 10; yDown++) {
            if (!world.isEmptyBlock(pos.below(yDown))) {
                return pos.above(yDown);
            }
        }
        return pos;
    }

    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return isJungle() ? JUNGLE_LOOT : DESERT_LOOT;
    }

    protected int getExperienceReward(Player player) {
        return 10;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, Boolean.valueOf(false));
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = createNavigator(level, AdvancedPathNavigate.MovementType.CLIMBING);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new EntityMyrmexRoyal.FlyMoveHelper(this);
            this.navigation = createNavigator(level, AdvancedPathNavigate.MovementType.FLYING);
            this.isLandNavigator = false;
        }
    }

    public boolean isFlying() {
        if (level.isClientSide) {
            return this.isFlying = this.entityData.get(FLYING).booleanValue();
        }
        return isFlying;
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
        if (!level.isClientSide) {
            this.isFlying = flying;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("HiveTicks", hiveTicks);
        tag.putInt("ReleaseTicks", releaseTicks);
        tag.putBoolean("Flying", this.isFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hiveTicks = tag.getInt("HiveTicks");
        this.releaseTicks = tag.getInt("ReleaseTicks");
        this.setFlying(tag.getBoolean("Flying"));
    }

    public void aiStep() {
        super.aiStep();
        boolean flying = this.isFlying() && !this.onGround;
        if (flying && flyProgress < 20.0F) {
            flyProgress += 1F;
        } else if (!flying && flyProgress > 0.0F) {
            flyProgress -= 1F;
        }
        if (flying) {
            double up = isInWater() ? 0.16D : 0.08D;
            this.setDeltaMovement(this.getDeltaMovement().add(0, up, 0));
        }
        if (flying && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!flying && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.canSeeSky()) {
            this.daylightTicks++;
        } else {
            this.daylightTicks = 0;
        }
        if (flying && this.canSeeSky() && this.isBreedingSeason()) {
            this.releaseTicks++;
        }
        if (!flying && this.canSeeSky() && daylightTicks > 300 && this.isBreedingSeason() && this.getTarget() == null && this.canMove() && this.isOnGround() && !isMating) {
            this.setFlying(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.42D, 0));
        }
        if (this.getGrowthStage() >= 2) {
            hiveTicks++;
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            if (this.getAttackBounds().intersects(this.getTarget().getBoundingBox())) {
                this.getTarget().hurt(DamageSource.mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getTarget() != null && this.getAnimationTick() == 6) {
            this.playStingSound();
            if (this.getAttackBounds().intersects(this.getTarget().getBoundingBox())) {
                this.getTarget().hurt(DamageSource.mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                this.getTarget().addEffect(new MobEffectInstance(MobEffects.POISON, 70, 1));
            }
        }
        if (this.mate != null) {
            this.level.broadcastEntityEvent(this, (byte) 77);
            if (this.distanceTo(this.mate) < 10) {
                this.setFlying(false);
                this.mate.setFlying(false);
                isMating = true;
                if (this.isOnGround() && this.mate.onGround) {
                    breedingTicks++;
                    if (breedingTicks > 100) {
                        if (this.isAlive()) {
                            this.mate.remove();
                            this.remove();
                            EntityMyrmexQueen queen = new EntityMyrmexQueen(IafEntityRegistry.MYRMEX_QUEEN, this.level);
                            queen.copyPosition(this);
                            queen.setJungleVariant(this.isJungle());
                            queen.setMadeHome(false);
                            if (!level.isClientSide) {
                                level.addFreshEntity(queen);
                            }
                        }
                        isMating = false;
                    }
                }
            }
            this.mate.mate = this;
            if (!this.mate.isAlive()) {
                this.mate.mate = null;
                this.mate = null;
            }
        }
    }

    protected double attackDistance() {
        return 8;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAIMoveToMate(this, 1.0D));
        this.goalSelector.addGoal(1, new AIFlyAtTarget());
        this.goalSelector.addGoal(2, new AIFlyRandom());
        this.goalSelector.addGoal(3, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(4, new MyrmexAILeaveHive(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAIReEnterHive(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexAIMoveThroughHive(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexAIWanderHiveCenter(this, 1.0D));
        this.goalSelector.addGoal(6, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(2, new MyrmexAIFindMate(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, new Predicate<LivingEntity>() {
            public boolean apply(@Nullable LivingEntity entity) {
                if (entity instanceof EntityMyrmexBase && EntityMyrmexRoyal.this.isBreedingSeason() || entity instanceof EntityMyrmexRoyal) {
                    return false;
                }
                return entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexRoyal.this, entity) && DragonUtils.isAlive(entity)  && !(entity instanceof Enemy);
            }
        }));

    }

    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this || otherAnimal == null) {
            return false;
        } else if (otherAnimal.getClass() != this.getClass()) {
            return false;
        } else {
            if (otherAnimal instanceof EntityMyrmexBase) {
                if (((EntityMyrmexBase) otherAnimal).getHive() != null && this.getHive() != null) {
                    return !this.getHive().equals(((EntityMyrmexBase) otherAnimal).getHive());
                } else {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean shouldMoveThroughHive() {
        return false;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 50D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 2D)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                //ARMOR
                .add(Attributes.ARMOR, 9.0D);
    }

    @Override
    public ResourceLocation getAdultTexture() {
        return isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
    }

    @Override
    public float getModelScale() {
        return 1.25F;
    }

    @Override
    public int getCasteImportance() {
        return 2;
    }

    public boolean shouldLeaveHive() {
        return isBreedingSeason();
    }

    public boolean shouldEnterHive() {
        return !isBreedingSeason();
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
            return true;
        }
        return false;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING};
    }

    public boolean isBreedingSeason() {
        return this.getGrowthStage() >= 2 && (this.getHive() == null || this.getHive().reproduces);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 76) {
            this.playEffect(20);
        } else if (id == 77) {
            this.playEffect(7);
        } else {
            super.handleEntityEvent(id);
        }
    }

    protected void playEffect(int hearts) {
        for (int i = 0; i < hearts; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d0, d1, d2);
        }
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {

    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32)
    {
        Vec3 vector3d = Vec3.atCenterOf(posVec31);
        Vec3 vector3d1 = Vec3.atCenterOf(posVec32);
        return level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }
    class FlyMoveHelper extends MoveControl {
        public FlyMoveHelper(EntityMyrmexRoyal pixie) {
            super(pixie);
            this.speedModifier = 1.75F;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                if (EntityMyrmexRoyal.this.horizontalCollision) {
                    EntityMyrmexRoyal.this.yRot += 180.0F;
                    this.speedModifier = 0.1F;
                    BlockPos target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.level, EntityMyrmexRoyal.this.getX() + EntityMyrmexRoyal.this.random.nextInt(15) - 7, EntityMyrmexRoyal.this.getZ() + EntityMyrmexRoyal.this.random.nextInt(15) - 7, EntityMyrmexRoyal.this.random);
                    this.wantedX = target.getX();
                    this.wantedY = target.getY();
                    this.wantedZ = target.getZ();
                }
                double d0 = this.wantedX - EntityMyrmexRoyal.this.getX();
                double d1 = this.wantedY - EntityMyrmexRoyal.this.getY();
                double d2 = this.wantedZ - EntityMyrmexRoyal.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = Mth.sqrt(d3);

                if (d3 < EntityMyrmexRoyal.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    EntityMyrmexRoyal.this.setDeltaMovement(EntityMyrmexRoyal.this.getDeltaMovement().multiply(0.5D, 0.5D, 0.5D));
                } else {
                    EntityMyrmexRoyal.this.setDeltaMovement(EntityMyrmexRoyal.this.getDeltaMovement().add(d0 / d3 * 0.1D * this.speedModifier, d1 / d3 * 0.1D * this.speedModifier, d2 / d3 * 0.1D * this.speedModifier));

                    if (EntityMyrmexRoyal.this.getTarget() == null) {
                        EntityMyrmexRoyal.this.yRot = -((float) Mth.atan2(EntityMyrmexRoyal.this.getDeltaMovement().x, EntityMyrmexRoyal.this.getDeltaMovement().z)) * (180F / (float) Math.PI);
                        EntityMyrmexRoyal.this.yBodyRot = EntityMyrmexRoyal.this.yRot;
                    } else {
                        double d4 = EntityMyrmexRoyal.this.getTarget().getX() - EntityMyrmexRoyal.this.getX();
                        double d5 = EntityMyrmexRoyal.this.getTarget().getZ() - EntityMyrmexRoyal.this.getZ();
                        EntityMyrmexRoyal.this.yRot = -((float) Mth.atan2(d4, d5)) * (180F / (float) Math.PI);
                        EntityMyrmexRoyal.this.yBodyRot = EntityMyrmexRoyal.this.yRot;
                    }
                }
            }
        }
    }

    class AIFlyRandom extends Goal {
        BlockPos target;

        public AIFlyRandom() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityMyrmexRoyal.this.isFlying() && EntityMyrmexRoyal.this.getTarget() == null) {
                if (EntityMyrmexRoyal.this instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner() != null) {
                    Entity summon = ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner();
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.level, summon.getX() + EntityMyrmexRoyal.this.random.nextInt(10) - 5, summon.getZ() + EntityMyrmexRoyal.this.random.nextInt(10) - 5, EntityMyrmexRoyal.this.random);
                } else {
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.level, EntityMyrmexRoyal.this.getX() + EntityMyrmexRoyal.this.random.nextInt(30) - 15, EntityMyrmexRoyal.this.getZ() + EntityMyrmexRoyal.this.random.nextInt(30) - 15, EntityMyrmexRoyal.this.random);
                }
                return isDirectPathBetweenPoints(EntityMyrmexRoyal.this.blockPosition(), target) && !EntityMyrmexRoyal.this.getMoveControl().hasWanted() && EntityMyrmexRoyal.this.random.nextInt(2) == 0;
            } else {
                return false;
            }
        }


        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            if (!isDirectPathBetweenPoints(EntityMyrmexRoyal.this.blockPosition(), target)) {
                if (EntityMyrmexRoyal.this instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner() != null) {
                    Entity summon = ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner();
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.level, summon.getX() + EntityMyrmexRoyal.this.random.nextInt(10) - 5, summon.getZ() + EntityMyrmexRoyal.this.random.nextInt(10) - 5, EntityMyrmexRoyal.this.random);
                } else {
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.level, EntityMyrmexRoyal.this.getX() + EntityMyrmexRoyal.this.random.nextInt(30) - 15, EntityMyrmexRoyal.this.getZ() + EntityMyrmexRoyal.this.random.nextInt(30) - 15, EntityMyrmexRoyal.this.random);
                }
            }
            if (EntityMyrmexRoyal.this.level.isEmptyBlock(target)) {
                EntityMyrmexRoyal.this.moveControl.setWantedPosition((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 0.25D);
                if (EntityMyrmexRoyal.this.getTarget() == null) {
                    EntityMyrmexRoyal.this.getLookControl().setLookAt((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 180.0F, 20.0F);

                }
            }
        }
    }

    class AIFlyAtTarget extends Goal {
        public AIFlyAtTarget() {
        }

        public boolean canUse() {
            if (EntityMyrmexRoyal.this.getTarget() != null && !EntityMyrmexRoyal.this.getMoveControl().hasWanted() && EntityMyrmexRoyal.this.random.nextInt(7) == 0) {
                return EntityMyrmexRoyal.this.distanceToSqr(EntityMyrmexRoyal.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return EntityMyrmexRoyal.this.getMoveControl().hasWanted() && EntityMyrmexRoyal.this.getTarget() != null && EntityMyrmexRoyal.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity LivingEntity = EntityMyrmexRoyal.this.getTarget();
            Vec3 Vector3d = LivingEntity.getEyePosition(1.0F);
            EntityMyrmexRoyal.this.moveControl.setWantedPosition(Vector3d.x, Vector3d.y, Vector3d.z, 1.0D);
        }

        public void stop() {

        }

        public void tick() {
            LivingEntity LivingEntity = EntityMyrmexRoyal.this.getTarget();
            if (LivingEntity != null) {
                if (EntityMyrmexRoyal.this.getBoundingBox().intersects(LivingEntity.getBoundingBox())) {
                    EntityMyrmexRoyal.this.doHurtTarget(LivingEntity);
                } else {
                    double d0 = EntityMyrmexRoyal.this.distanceToSqr(LivingEntity);

                    if (d0 < 9.0D) {
                        Vec3 Vector3d = LivingEntity.getEyePosition(1.0F);
                        EntityMyrmexRoyal.this.moveControl.setWantedPosition(Vector3d.x, Vector3d.y, Vector3d.z, 1.0D);
                    }
                }
            }

        }
    }
}
