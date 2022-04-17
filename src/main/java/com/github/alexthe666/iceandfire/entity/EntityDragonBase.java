package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.client.model.util.LegSolverQuadruped;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.props.ChainProperties;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeInput;
import com.github.alexthe666.iceandfire.entity.util.*;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.inventory.ContainerDragon;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDragonArmor;
import com.github.alexthe666.iceandfire.item.ItemSummoningCrystal;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.message.MessageDragonSetBurnBlock;
import com.github.alexthe666.iceandfire.message.MessageStartRidingMob;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.IPassabilityNavigator;
import com.github.alexthe666.iceandfire.world.DragonPosWorldData;
import com.google.common.base.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.*;
import net.minecraft.util.Mth.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class EntityDragonBase extends TamableAnimal implements IPassabilityNavigator, ISyncMount, IFlyingMount, IMultipartEntity, IAnimatedEntity, IDragonFlute, IDeadMob, IVillagerFear, IAnimalFear, IDropArmor {

    public static final int FLIGHT_CHANCE_PER_TICK = 1500;
    protected static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HUNGER = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AGE_TICKS = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FIREBREATHING = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOVERING = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MODEL_DEAD = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DEATH_STAGE = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> CONTROL_STATE = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> TACKLE = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AGINGDISABLED = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DRAGON_PITCH = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CRYSTAL_BOUND = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> CUSTOM_POSE = SynchedEntityData.defineId(EntityDragonBase.class, EntityDataSerializers.STRING);
    public static Animation ANIMATION_FIRECHARGE;
    public static Animation ANIMATION_EAT;
    public static Animation ANIMATION_SPEAK;
    public static Animation ANIMATION_BITE;
    public static Animation ANIMATION_SHAKEPREY;
    public static Animation ANIMATION_WINGBLAST;
    public static Animation ANIMATION_ROAR;
    public static Animation ANIMATION_EPIC_ROAR;
    public static Animation ANIMATION_TAILWHACK;
    public DragonType dragonType;
    public double minimumDamage;
    public double maximumDamage;
    public double minimumHealth;
    public double maximumHealth;
    public double minimumSpeed;
    public double maximumSpeed;
    public double minimumArmor;
    public double maximumArmor;
    public float sitProgress;
    public float sleepProgress;
    public float hoverProgress;
    public float flyProgress;
    public float fireBreathProgress;
    public float diveProgress;
    public float prevDiveProgress;
    public float prevFireBreathProgress;
    public int fireStopTicks;
    public int flyTicks;
    public float modelDeadProgress;
    public float prevModelDeadProgress;
    public float ridingProgress;
    public float tackleProgress;
    /*
    0 = sit
    1 = sleep
    2 = hover
    3 = fly
    4 = fireBreath
    5 = riding
    6 = tackle
     */
    public boolean isSwimming;
    public float prevSwimProgress;
    public float swimProgress;
    public int ticksSwiming;
    public int swimCycle;
    public float[] prevAnimationProgresses = new float[10];
    public boolean isDaytime;
    public int flightCycle;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer roll_buffer;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer pitch_buffer;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer pitch_buffer_body;
    @OnlyIn(Dist.CLIENT)
    public ReversedBuffer turn_buffer;
    @OnlyIn(Dist.CLIENT)
    public ChainBuffer tail_buffer;
    public int spacebarTicks;
    public float[][] growth_stages;
    public LegSolverQuadruped legSolver;
    public int walkCycle;
    public BlockPos burningTarget;
    public int burnProgress;
    public double burnParticleX;
    public double burnParticleY;
    public double burnParticleZ;
    public float prevDragonPitch;
    public IafDragonAttacks.Air airAttack;
    public IafDragonAttacks.Ground groundAttack;
    public boolean usingGroundAttack = true;
    public IafDragonLogic logic;
    public int hoverTicks;
    public int tacklingTicks;
    public int ticksStill;
    /*
        0 = ground/walking
        1 = ai flight
        2 = controlled flight
     */
    public int navigatorType;
    public SimpleContainer dragonInventory;
    public String prevArmorResLoc = "0|0|0|0";
    public String armorResLoc = "0|0|0|0";
    public IafDragonFlightManager flightManager;
    public boolean lookingForRoostAIFlag = false;
    protected int flyHovering;
    protected boolean hasHadHornUse = false;
    protected int fireTicks;
    protected int blockBreakCounter;
    private int prevFlightCycle;
    private boolean isModelDead;
    private int animationTick;
    private Animation currentAnimation;
    private float lastScale;
    private EntityDragonPart headPart;
    private EntityDragonPart neckPart;
    private EntityDragonPart rightWingUpperPart;
    private EntityDragonPart rightWingLowerPart;
    private EntityDragonPart leftWingUpperPart;
    private EntityDragonPart leftWingLowerPart;
    private EntityDragonPart tail1Part;
    private EntityDragonPart tail2Part;
    private EntityDragonPart tail3Part;
    private EntityDragonPart tail4Part;
    private boolean isOverAir;

    public EntityDragonBase(EntityType t, Level world, DragonType type, double minimumDamage, double maximumDamage, double minimumHealth, double maximumHealth, double minimumSpeed, double maximumSpeed) {
        super(t, world);
        initInventory();
        this.dragonType = type;
        this.minimumDamage = minimumDamage;
        this.maximumDamage = maximumDamage;
        this.minimumHealth = minimumHealth;
        this.maximumHealth = maximumHealth;
        this.minimumSpeed = minimumSpeed;
        this.maximumSpeed = maximumSpeed;
        this.minimumArmor = 1D;
        this.maximumArmor = 20D;
        ANIMATION_EAT = Animation.create(20);
        updateAttributes();
        if (world.isClientSide) {
            roll_buffer = new IFChainBuffer();
            pitch_buffer = new IFChainBuffer();
            pitch_buffer_body = new IFChainBuffer();
            turn_buffer = new ReversedBuffer();
            tail_buffer = new ChainBuffer();
        }
        legSolver = new LegSolverQuadruped(0.3F, 0.35F, 0.2F, 1.45F, 1.0F);
        this.flightManager = new IafDragonFlightManager(this);
        this.logic = createDragonLogic();
        this.noCulling = true;
        switchNavigator(0);
        randomizeAttacks();
        resetParts(1);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 20.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 1)
                //FOLLOW RANGE
                .add(Attributes.FOLLOW_RANGE, Math.min(2048, IafConfig.dragonTargetSearchLength))
                //ARMOR
                .add(Attributes.ARMOR, 4);
    }

    public BlockPos getRestrictCenter() {
        return this.homePos == null ? super.getRestrictCenter() : homePos;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DragonAIRide<>(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new DragonAIMate(this, 1.0D));
        this.goalSelector.addGoal(3, new DragonAIReturnToRoost(this, 1.0D));
        this.goalSelector.addGoal(4, new DragonAIEscort(this, 1.0D));
        this.goalSelector.addGoal(5, new DragonAIAttackMelee(this, 1.5D, false));
        this.goalSelector.addGoal(6, new AquaticAITempt(this, 1.0D, IafItemRegistry.FIRE_STEW, false));
        this.goalSelector.addGoal(7, new DragonAIWander(this, 1.0D));
        this.goalSelector.addGoal(8, new DragonAIWatchClosest(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new DragonAILookIdle(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new DragonAITargetNonTamed(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity entity) {
                return (!(entity instanceof Player) || !((Player) entity).isCreative()) && DragonUtils.canHostilesTarget(entity) && entity.getType() != EntityDragonBase.this.getType() && EntityDragonBase.this.shouldTarget(entity) && DragonUtils.isAlive(entity);
            }
        }));
        this.targetSelector.addGoal(5, new DragonAITarget(this, LivingEntity.class, true, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity entity) {
                return entity instanceof LivingEntity && DragonUtils.canHostilesTarget(entity) && entity.getType() != EntityDragonBase.this.getType() && EntityDragonBase.this.shouldTarget(entity) && DragonUtils.isAlive(entity);
            }
        }));
        this.targetSelector.addGoal(6, new DragonAITargetItems(this, false));
    }

    protected abstract boolean shouldTarget(Entity entity);

    public void resetParts(float scale) {
        removeParts();
        headPart = new EntityDragonPart(this, 1.55F * scale, 0, 0.6F * scale, 0.5F * scale, 0.35F * scale, 1.5F);
        headPart.copyPosition(this);
        headPart.setParent(this);
        neckPart = new EntityDragonPart(this, 0.85F * scale, 0, 0.7F * scale, 0.5F * scale, 0.2F * scale, 1);
        neckPart.copyPosition(this);
        neckPart.setParent(this);
        rightWingUpperPart = new EntityDragonPart(this, 1F * scale, 90, 0.5F * scale, 0.85F * scale, 0.3F * scale, 0.5F);
        rightWingUpperPart.copyPosition(this);
        rightWingUpperPart.setParent(this);
        rightWingLowerPart = new EntityDragonPart(this, 1.4F * scale, 100, 0.3F * scale, 0.85F * scale, 0.2F * scale, 0.5F);
        rightWingLowerPart.copyPosition(this);
        rightWingLowerPart.setParent(this);
        leftWingUpperPart = new EntityDragonPart(this, 1F * scale, -90, 0.5F * scale, 0.85F * scale, 0.3F * scale, 0.5F);
        leftWingUpperPart.copyPosition(this);
        leftWingUpperPart.setParent(this);
        leftWingLowerPart = new EntityDragonPart(this, 1.4F * scale, -100, 0.3F * scale, 0.85F * scale, 0.2F * scale, 0.5F);
        leftWingLowerPart.copyPosition(this);
        leftWingLowerPart.setParent(this);
        tail1Part = new EntityDragonPart(this, -0.75F * scale, 0, 0.6F * scale, 0.35F * scale, 0.35F * scale, 1);
        tail1Part.copyPosition(this);
        tail1Part.setParent(this);
        tail2Part = new EntityDragonPart(this, -1.15F * scale, 0, 0.45F * scale, 0.35F * scale, 0.35F * scale, 1);
        tail2Part.copyPosition(this);
        tail2Part.setParent(this);
        tail3Part = new EntityDragonPart(this, -1.5F * scale, 0, 0.35F * scale, 0.35F * scale, 0.35F * scale, 1);
        tail3Part.copyPosition(this);
        tail3Part.setParent(this);
        tail4Part = new EntityDragonPart(this, -1.95F * scale, 0, 0.25F * scale, 0.45F * scale, 0.3F * scale, 1.5F);
        tail4Part.copyPosition(this);
        tail4Part.setParent(this);
    }

    public void removeParts() {
        if (headPart != null) {
            headPart.remove();
            headPart = null;
        }
        if (neckPart != null) {
            neckPart.remove();
            neckPart = null;
        }
        if (rightWingUpperPart != null) {
            rightWingUpperPart.remove();
            rightWingUpperPart = null;
        }
        if (rightWingLowerPart != null) {
            rightWingLowerPart.remove();
            rightWingLowerPart = null;
        }
        if (leftWingUpperPart != null) {
            leftWingUpperPart.remove();
            leftWingUpperPart = null;
        }
        if (leftWingLowerPart != null) {
            leftWingLowerPart.remove();
            leftWingLowerPart = null;
        }
        if (tail1Part != null) {
            tail1Part.remove();
            tail1Part = null;
        }
        if (tail2Part != null) {
            tail2Part.remove();
            tail2Part = null;
        }
        if (tail3Part != null) {
            tail3Part.remove();
            tail3Part = null;
        }
        if (tail4Part != null) {
            tail4Part.remove();
            tail4Part = null;
        }
    }

    public void updateParts() {
        if (headPart != null) {
            if (!headPart.shouldContinuePersisting()) {
                level.addFreshEntity(headPart);
            }
            headPart.setParent(this);
        }
        if (neckPart != null) {
            if (!neckPart.shouldContinuePersisting()) {
                level.addFreshEntity(neckPart);
            }
            neckPart.setParent(this);
        }
        if (rightWingUpperPart != null) {
            if (!rightWingUpperPart.shouldContinuePersisting()) {
                level.addFreshEntity(rightWingUpperPart);
            }
            rightWingUpperPart.setParent(this);
        }
        if (rightWingLowerPart != null) {
            if (!rightWingLowerPart.shouldContinuePersisting()) {
                level.addFreshEntity(rightWingLowerPart);
            }
            rightWingLowerPart.setParent(this);
        }
        if (leftWingUpperPart != null) {
            if (!leftWingUpperPart.shouldContinuePersisting()) {
                level.addFreshEntity(leftWingUpperPart);
            }
            leftWingUpperPart.setParent(this);
        }
        if (leftWingLowerPart != null) {
            if (!leftWingLowerPart.shouldContinuePersisting()) {
                level.addFreshEntity(leftWingLowerPart);
            }
            leftWingLowerPart.setParent(this);
        }
        if (tail1Part != null) {
            if (!tail1Part.shouldContinuePersisting()) {
                level.addFreshEntity(tail1Part);
            }
            tail1Part.setParent(this);
        }
        if (tail2Part != null) {
            if (!tail2Part.shouldContinuePersisting()) {
                level.addFreshEntity(tail2Part);
            }
            tail2Part.setParent(this);
        }
        if (tail3Part != null) {
            if (!tail3Part.shouldContinuePersisting()) {
                level.addFreshEntity(tail3Part);
            }
            tail3Part.setParent(this);
        }
        if (tail4Part != null) {
            if (!tail4Part.shouldContinuePersisting()) {
                level.addFreshEntity(tail4Part);
            }
            tail4Part.setParent(this);
        }
    }

    protected void updateBurnTarget() {
        if (burningTarget != null && !this.isSleeping() && !this.isModelDead() && !this.isBaby()) {
            float maxDist = 115 * this.getDragonStage();
            boolean flag = false;
            if (level.getBlockEntity(burningTarget) instanceof TileEntityDragonforgeInput && ((TileEntityDragonforgeInput) level.getBlockEntity(burningTarget)).isAssembled()
                && this.distanceToSqr(burningTarget.getX() + 0.5D, burningTarget.getY() + 0.5D, burningTarget.getZ() + 0.5D) < maxDist && canPositionBeSeen(burningTarget.getX() + 0.5D, burningTarget.getY() + 0.5D, burningTarget.getZ() + 0.5D)) {
                this.getLookControl().setLookAt(burningTarget.getX() + 0.5D, burningTarget.getY() + 0.5D, burningTarget.getZ() + 0.5D, 180F, 180F);
                this.breathFireAtPos(burningTarget);
                this.setBreathingFire(true);
            } else {
                if (!level.isClientSide) {
                    IceAndFire.sendMSGToAll(new MessageDragonSetBurnBlock(this.getId(), true, burningTarget));
                }
                burningTarget = null;
            }
        }
    }

    protected abstract void breathFireAtPos(BlockPos burningTarget);

    protected PathNavigation createNavigation(Level worldIn) {
        return createNavigator(worldIn, AdvancedPathNavigate.MovementType.WALKING);
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type) {
        return createNavigator(worldIn, type, 4f, 4f);
    }

    protected PathNavigation createNavigator(Level worldIn, AdvancedPathNavigate.MovementType type, float width, float height) {
        AdvancedPathNavigate newNavigator = new AdvancedPathNavigate(this, level, type, width, height);
        this.navigation = newNavigator;
        newNavigator.setCanFloat(true);
        newNavigator.getNodeEvaluator().setCanOpenDoors(true);
        return newNavigator;
    }

    protected void switchNavigator(int navigatorType) {
        if (navigatorType == 0) {
            this.moveControl = new IafDragonFlightManager.GroundMoveHelper(this);
            this.navigation = createNavigator(level, AdvancedPathNavigate.MovementType.WALKING);
            this.navigatorType = 0;
            this.setFlying(false);
            this.setHovering(false);
        } else if (navigatorType == 1) {
            this.moveControl = new IafDragonFlightManager.FlightMoveHelper(this);
            this.navigation = createNavigator(level, AdvancedPathNavigate.MovementType.FLYING);
            this.navigatorType = 1;
        } else {
            this.moveControl = new IafDragonFlightManager.PlayerFlightMoveHelper(this);
            this.navigation = createNavigator(level, AdvancedPathNavigate.MovementType.FLYING);
            this.navigatorType = 2;
        }
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        breakBlock();
    }

    public boolean canDestroyBlock(BlockPos pos, BlockState state) {
        return state.getBlock().canEntityDestroy(state, level, pos, this);
    }

    public boolean isMobDead() {
        return this.isModelDead();
    }

    public int getMaxHeadYRot() {
        return 10 * this.getDragonStage() / 5;
    }

    public void openGUI(Player playerEntity) {
        IceAndFire.PROXY.setReferencedMob(this);
        if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger(playerEntity))) {
            playerEntity.openMenu(new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                    return new ContainerDragon(p_createMenu_1_, dragonInventory, p_createMenu_2_, EntityDragonBase.this);
                }

                @Override
                public Component getDisplayName() {
                    return EntityDragonBase.this.getDisplayName();
                }
            });
        }
    }

    public int getAmbientSoundInterval() {
        return 90;
    }

    protected void tickDeath() {
        this.deathTime = 0;
        if (!this.isModelDead()) {
            if (!this.level.isClientSide && this.lastHurtByPlayerTime > 0) {
                int i = this.getExperienceReward(this.lastHurtByPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.lastHurtByPlayer, i);
                while (i > 0) {
                    int j = ExperienceOrb.getExperienceValue(i);
                    i -= j;
                    this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), j));
                }
            }
        }
        this.setModelDead(true);
        this.ejectPassengers();
        if (this.getDeathStage() >= this.getAgeInDays() / 5) {
            this.remove();
            for (int k = 0; k < 40; ++k) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                if (level.isClientSide) {
                    this.level.addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d2, d0, d1);
                }
            }
            spawnDeathParticles();
        }
    }

    protected void spawnDeathParticles() {
    }

    protected void spawnBabyParticles() {
    }

    public void remove() {
        removeParts();
        super.remove(RemovalReason.DISCARDED);
    }

    protected int getExperienceReward(Player player) {
        switch (this.getDragonStage()) {
            case 2:
                return 20;
            case 3:
                return 150;
            case 4:
                return 300;
            case 5:
                return 650;
            default:
                return 5;
        }
    }

    public int getArmorOrdinal(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemDragonArmor) {
            ItemDragonArmor armorItem = (ItemDragonArmor) stack.getItem();
            return armorItem.type + 1;
        }
        return 0;
    }

    @Override
    public boolean isNoAi() {
        return this.isModelDead() || super.isNoAi();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HUNGER, Integer.valueOf(0));
        this.entityData.define(AGE_TICKS, Integer.valueOf(0));
        this.entityData.define(GENDER, Boolean.valueOf(false));
        this.entityData.define(VARIANT, Integer.valueOf(0));
        this.entityData.define(SLEEPING, Boolean.valueOf(false));
        this.entityData.define(FIREBREATHING, Boolean.valueOf(false));
        this.entityData.define(HOVERING, Boolean.valueOf(false));
        this.entityData.define(FLYING, Boolean.valueOf(false));
        this.entityData.define(DEATH_STAGE, Integer.valueOf(0));
        this.entityData.define(MODEL_DEAD, Boolean.valueOf(false));
        this.entityData.define(CONTROL_STATE, Byte.valueOf((byte) 0));
        this.entityData.define(TACKLE, Boolean.valueOf(false));
        this.entityData.define(AGINGDISABLED, Boolean.valueOf(false));
        this.entityData.define(COMMAND, Integer.valueOf(0));
        this.entityData.define(DRAGON_PITCH, Float.valueOf(0));
        this.entityData.define(CRYSTAL_BOUND, Boolean.valueOf(false));
        this.entityData.define(CUSTOM_POSE, "");
    }

    public boolean isGoingUp() {
        return (entityData.get(CONTROL_STATE).byteValue() & 1) == 1;
    }

    public boolean isGoingDown() {
        return (entityData.get(CONTROL_STATE).byteValue() >> 1 & 1) == 1;
    }

    public boolean isAttacking() {
        return (entityData.get(CONTROL_STATE).byteValue() >> 2 & 1) == 1;
    }

    public boolean isStriking() {
        return (entityData.get(CONTROL_STATE).byteValue() >> 3 & 1) == 1;
    }

    public boolean isDismounting() {
        return (entityData.get(CONTROL_STATE).byteValue() >> 4 & 1) == 1;
    }

    public void goUp(boolean up) {
        setStateField(0, up);
    }

    public void goDown(boolean down) {
        setStateField(1, down);
    }

    public void goAttack(boolean attack) {
        setStateField(2, attack);
    }

    public void goStrike(boolean strike) {
        setStateField(3, strike);
    }

    public void goDismount(boolean dismount) {
        setStateField(4, dismount);
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = entityData.get(CONTROL_STATE).byteValue();
        if (newState) {
            entityData.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            entityData.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    public byte getControlState() {
        return entityData.get(CONTROL_STATE).byteValue();
    }

    public void setControlState(byte state) {
        entityData.set(CONTROL_STATE, state);
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

    public float getDragonPitch() {
        return entityData.get(DRAGON_PITCH).floatValue();
    }

    public void setDragonPitch(float pitch) {
        entityData.set(DRAGON_PITCH, pitch);
    }

    public void incrementDragonPitch(float pitch) {
        entityData.set(DRAGON_PITCH, getDragonPitch() + pitch);
    }

    public void decrementDragonPitch(float pitch) {
        entityData.set(DRAGON_PITCH, getDragonPitch() - pitch);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Hunger", this.getHunger());
        compound.putInt("AgeTicks", this.getAgeInTicks());
        compound.putBoolean("Gender", this.isMale());
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Sleeping", this.isSleeping());
        compound.putBoolean("TamedDragon", this.isTame());
        compound.putBoolean("FireBreathing", this.isBreathingFire());
        compound.putBoolean("AttackDecision", usingGroundAttack);
        compound.putBoolean("Hovering", this.isHovering());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("DeathStage", this.getDeathStage());
        compound.putBoolean("ModelDead", this.isModelDead());
        compound.putFloat("DeadProg", this.modelDeadProgress);
        compound.putBoolean("Tackle", this.isTackling());
        compound.putBoolean("HasHomePosition", this.hasHomePosition);
        compound.putString("CustomPose", this.getCustomPose());
        if (homePos != null && this.hasHomePosition) {
            compound.putInt("HomeAreaX", homePos.getX());
            compound.putInt("HomeAreaY", homePos.getY());
            compound.putInt("HomeAreaZ", homePos.getZ());
        }
        compound.putBoolean("AgingDisabled", this.isAgingDisabled());
        compound.putInt("Command", this.getCommand());
        if (dragonInventory != null) {
            ListTag nbttaglist = new ListTag();
            for (int i = 0; i < dragonInventory.getContainerSize(); ++i) {
                ItemStack itemstack = dragonInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag CompoundNBT = new CompoundTag();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.save(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }
        compound.putBoolean("CrystalBound", this.isBoundToCrystal());
        if (this.hasCustomName()) {
            compound.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHunger(compound.getInt("Hunger"));
        this.setAgeInTicks(compound.getInt("AgeTicks"));
        this.setGender(compound.getBoolean("Gender"));
        this.setVariant(compound.getInt("Variant"));
        this.setInSittingPose(compound.getBoolean("Sleeping"));
        this.setTame(compound.getBoolean("TamedDragon"));
        this.setBreathingFire(compound.getBoolean("FireBreathing"));
        this.usingGroundAttack = compound.getBoolean("AttackDecision");
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setDeathStage(compound.getInt("DeathStage"));
        this.setModelDead(compound.getBoolean("ModelDead"));
        this.modelDeadProgress = compound.getFloat("DeadProg");
        this.setCustomPose(compound.getString("CustomPose"));
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0) {
            homePos = new BlockPos(compound.getInt("HomeAreaX"), compound.getInt("HomeAreaY"), compound.getInt("HomeAreaZ"));
        }
        this.setTackling(compound.getBoolean("Tackle"));
        this.setAgingDisabled(compound.getBoolean("AgingDisabled"));
        this.setCommand(compound.getInt("Command"));
        if (dragonInventory != null) {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = (net.minecraft.nbt.CompoundTag) nbttaglist.get(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                if (j <= 4) {
                    dragonInventory.setItem(j, ItemStack.of(CompoundNBT));
                }
            }
        } else {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initInventory();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = (net.minecraft.nbt.CompoundTag) nbttaglist.get(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                dragonInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        }
        this.setCrystalBound(compound.getBoolean("CrystalBound"));
        if (compound.contains("CustomName", 8) && !compound.getString("CustomName").startsWith("TextComponent")) {
            this.setCustomName(Component.Serializer.fromJson(compound.getString("CustomName")));
        }
    }

    private void initInventory() {
        dragonInventory = new SimpleContainer(5);
        if (dragonInventory != null) {
            for (int j = 0; j < dragonInventory.getContainerSize(); ++j) {
                ItemStack itemstack = dragonInventory.getItem(j);
                if (!itemstack.isEmpty()) {
                    dragonInventory.setItem(j, itemstack.copy());
                }
            }
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player && this.getTarget() != passenger) {
                Player player = (Player) passenger;
                if (this.isTame() && this.getOwnerUUID() != null && this.getOwnerUUID().equals(player.getUUID())) {
                    return player;
                }
            }
        }
        return null;
    }

    public boolean isRidingPlayer(Player player) {
        return getRidingPlayer() != null && player != null && getRidingPlayer().getUUID().equals(player.getUUID());
    }

    @Nullable
    public Player getRidingPlayer() {
        if (this.getControllingPassenger() instanceof Player) {
            return (Player) this.getControllingPassenger();
        }
        return null;
    }

    protected void updateAttributes() {
        prevArmorResLoc = armorResLoc;
        int armorHead = this.getArmorOrdinal(this.getItemBySlot(EquipmentSlot.HEAD));
        int armorNeck = this.getArmorOrdinal(this.getItemBySlot(EquipmentSlot.CHEST));
        int armorLegs = this.getArmorOrdinal(this.getItemBySlot(EquipmentSlot.LEGS));
        int armorFeet = this.getArmorOrdinal(this.getItemBySlot(EquipmentSlot.FEET));
        armorResLoc = dragonType.getName() + "|" + armorHead + "|" + armorNeck + "|" + armorLegs + "|" + armorFeet;
        IceAndFire.PROXY.updateDragonArmorRender(armorResLoc);
        double healthStep = (maximumHealth - minimumHealth) / 125F;
        double attackStep = (maximumDamage - minimumDamage) / 125F;
        double speedStep = (maximumSpeed - minimumSpeed) / 125F;
        double armorStep = (maximumArmor - minimumArmor) / 125F;
        if (this.getAgeInDays() <= 125) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.round(minimumHealth + (healthStep * this.getAgeInDays())));
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Math.round(minimumDamage + (attackStep * this.getAgeInDays())));
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(minimumSpeed + (speedStep * this.getAgeInDays()));
            double oldValue = minimumArmor + (armorStep * this.getAgeInDays());
            this.getAttribute(Attributes.ARMOR).setBaseValue(oldValue + calculateArmorModifier());
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(Math.min(2048, IafConfig.dragonTargetSearchLength));
        }
    }

    public int getHunger() {
        return this.entityData.get(HUNGER).intValue();
    }

    public void setHunger(int hunger) {
        this.entityData.set(HUNGER, Mth.clamp(hunger, 0, 100));
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getAgeInDays() {
        return this.entityData.get(AGE_TICKS).intValue() / 24000;
    }

    public void setAgeInDays(int age) {
        this.entityData.set(AGE_TICKS, age * 24000);
    }

    public int getAgeInTicks() {
        return this.entityData.get(AGE_TICKS).intValue();
    }

    public void setAgeInTicks(int age) {
        this.entityData.set(AGE_TICKS, age);
    }

    public int getDeathStage() {
        return this.entityData.get(DEATH_STAGE).intValue();
    }

    public void setDeathStage(int stage) {
        this.entityData.set(DEATH_STAGE, stage);
    }

    public boolean isMale() {
        return this.entityData.get(GENDER).booleanValue();
    }

    public boolean isModelDead() {
        if (level.isClientSide) {
            return this.isModelDead = Boolean.valueOf(this.entityData.get(MODEL_DEAD).booleanValue());
        }
        return isModelDead;
    }

    public void setModelDead(boolean modeldead) {
        this.entityData.set(MODEL_DEAD, modeldead);
        if (!level.isClientSide) {
            this.isModelDead = modeldead;
        }
    }

    public boolean isHovering() {
        return this.entityData.get(HOVERING).booleanValue();
    }

    public void setHovering(boolean hovering) {
        this.entityData.set(HOVERING, hovering);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING).booleanValue();
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean useFlyingPathFinder() {
        return isFlying();
    }

    public void setGender(boolean male) {
        this.entityData.set(GENDER, male);
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING).booleanValue();
    }

    public boolean isBlinking() {
        return this.tickCount % 50 > 43;
    }

    public boolean isBreathingFire() {
        return this.entityData.get(FIREBREATHING).booleanValue();
    }

    public void setBreathingFire(boolean breathing) {
        this.entityData.set(FIREBREATHING, breathing);
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    public boolean isOrderedToSit() {
        return (this.entityData.get(DATA_FLAGS_ID).byteValue() & 1) != 0;
    }

    public void setInSittingPose(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
    }

    public void setOrderedToSit(boolean sitting) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID).byteValue();
        if (sitting) {
            this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte) (b0 & -2)));
        }
    }

    public String getCustomPose() {
        return this.entityData.get(CUSTOM_POSE);
    }
    public void setCustomPose(String customPose) {
        this.entityData.set(CUSTOM_POSE, customPose);
        modelDeadProgress = 20f;
    }

    public void riderShootFire(Entity controller) {
    }

    @Override
    public void killed(ServerLevel world, LivingEntity entity) {
        this.setHunger(this.getHunger() + FoodUtils.getFoodPoints(entity));
    }

    private double calculateArmorModifier() {
        double val = 1D;
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            switch (getArmorOrdinal(getItemBySlot(slot))) {
                case 1:
                    val += 2D;
                    break;
                case 2:
                    val += 3D;
                    break;
                case 3:
                    val += 5D;
                    break;
                case 4:
                    val += 3D;
                    break;
                case 5:
                    val += 10D;
                    break;
                case 6:
                    val += 10D;
                    break;
                case 7:
                    val += 1.5D;
                    break;
                case 8:
                    val += 10D;
                    break;
            }
        }
        return val;
    }

    public boolean canMove() {
        return !this.isOrderedToSit() && !this.isSleeping() && this.getControllingPassenger() == null && !this.isModelDead() && sleepProgress == 0 && this.getAnimation() != ANIMATION_SHAKEPREY;
    }

    public boolean isAlive() {
        return super.isAlive();
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int lastDeathStage = this.getAgeInDays() / 5;
        if (stack.getItem() == IafItemRegistry.DRAGON_DEBUG_STICK) {
            logic.debug();
            return InteractionResult.SUCCESS;
        }
        if (this.isModelDead() && this.getDeathStage() < lastDeathStage && player.mayBuild()) {
            if (!level.isClientSide && !stack.isEmpty() && stack.getItem() != null && stack.getItem() == Items.GLASS_BOTTLE && this.getDeathStage() < lastDeathStage / 2 && IafConfig.dragonDropBlood) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                this.setDeathStage(this.getDeathStage() + 1);
                player.getInventory().add(new ItemStack(this.getBloodItem(), 1));
                return InteractionResult.SUCCESS;
            } else if (!level.isClientSide && stack.isEmpty() && IafConfig.dragonDropSkull) {
                if (this.getDeathStage() == lastDeathStage - 1) {
                    ItemStack skull = getSkull().copy();
                    skull.setTag(new CompoundTag());
                    skull.getTag().putInt("Stage", this.getDragonStage());
                    skull.getTag().putInt("DragonType", 0);
                    skull.getTag().putInt("DragonAge", this.getAgeInDays());
                    this.setDeathStage(this.getDeathStage() + 1);
                    if (!level.isClientSide) {
                        this.spawnAtLocation(skull, 1);
                    }
                    this.remove();
                } else if (this.getDeathStage() == (lastDeathStage / 2) - 1 && IafConfig.dragonDropHeart) {
                    ItemStack heart = new ItemStack(this.getHeartItem(), 1);
                    ItemStack egg = new ItemStack(this.getVariantEgg(this.random.nextInt(4)), 1);
                    if (!level.isClientSide) {
                        this.spawnAtLocation(heart, 1);
                        if (!this.isMale() && this.getDragonStage() > 3) {
                            this.spawnAtLocation(egg, 1);
                        }
                    }
                    this.setDeathStage(this.getDeathStage() + 1);
                } else {
                    this.setDeathStage(this.getDeathStage() + 1);
                    ItemStack drop = getRandomDrop();
                    if (!drop.isEmpty() && !level.isClientSide) {
                        this.spawnAtLocation(drop, 1);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int lastDeathStage = this.getAgeInDays() / 5;
        if (stack.getItem() == IafItemRegistry.DRAGON_DEBUG_STICK) {
            logic.debug();
            return InteractionResult.SUCCESS;
        }
        if (!this.isModelDead()) {
            if (stack.getItem() == IafItemRegistry.CREATIVE_DRAGON_MEAL) {
                this.setTame(true);
                this.tame(player);
                this.setHunger(this.getHunger() + 20);
                this.heal(Math.min(this.getHealth(), (int) (this.getMaxHealth() / 2)));
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.spawnItemCrackParticles(stack.getItem());
                this.spawnItemCrackParticles(Items.BONE);
                this.spawnItemCrackParticles(Items.BONE_MEAL);
                this.eatFoodBonus(stack);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            if (this.isFood(stack) && this.shouldDropLoot()) {
                this.setAge(0);
                // NOTE: Hand seems to be new requirement. Unsure of ideal behaviour
                this.usePlayerItem(player, InteractionHand.MAIN_HAND, stack);
                this.setInLove(player);
                return InteractionResult.SUCCESS;
            }
            if (this.isOwnedBy(player)) {
                if (stack.getItem() == getSummoningCrystal() && !ItemSummoningCrystal.hasDragon(stack)) {
                    this.setCrystalBound(true);
                    CompoundTag compound = stack.getTag();
                    if (compound == null) {
                        compound = new CompoundTag();
                        stack.setTag(compound);
                    }
                    CompoundTag dragonTag = new CompoundTag();
                    dragonTag.putUUID("DragonUUID", this.getUUID());
                    if (this.getCustomName() != null) {
                        dragonTag.putString("CustomName", this.getCustomName().getString());
                    }
                    compound.put("Dragon", dragonTag);
                    this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1, 1);
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
                this.tame(player);
                if (stack.getItem() == IafItemRegistry.DRAGON_HORN) {
                    return super.mobInteract(player, hand);
                }
                if (stack.isEmpty() && !player.isShiftKeyDown()) {
                    if (!level.isClientSide) {
                        if (this.getDragonStage() < 2) {
                            this.startRiding(player, true);
                            IceAndFire.sendMSGToAll(new MessageStartRidingMob(this.getId(), true, true));
                            return InteractionResult.SUCCESS;
                        }
                        if (this.getDragonStage() > 2 && !player.isPassenger()) {
                            player.setShiftKeyDown(false);
                            player.startRiding(this, true);
                            IceAndFire.sendMSGToAll(new MessageStartRidingMob(this.getId(), true, false));
                            this.setInSittingPose(false);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (stack.isEmpty() && player.isShiftKeyDown()) {
                    this.openGUI(player);
                    return InteractionResult.SUCCESS;
                } else {
                    int itemFoodAmount = FoodUtils.getFoodPoints(stack, true, dragonType.isPiscivore());
                    if (itemFoodAmount > 0 && (this.getHunger() < 100 || this.getHealth() < this.getMaxHealth())) {
                        //this.growDragon(1);
                        this.setHunger(this.getHunger() + itemFoodAmount);
                        this.setHealth(Math.min(this.getMaxHealth(), (int) (this.getHealth() + (itemFoodAmount / 10))));
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        this.eatFoodBonus(stack);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    if (stack.getItem() == IafItemRegistry.DRAGON_MEAL) {
                        this.growDragon(1);
                        this.setHunger(this.getHunger() + 20);
                        this.heal(Math.min(this.getHealth(), (int) (this.getMaxHealth() / 2)));
                        this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.BONE_MEAL);
                        this.eatFoodBonus(stack);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    if (stack.getItem() == IafItemRegistry.SICKLY_DRAGON_MEAL && !this.isAgingDisabled()) {
                        this.setHunger(this.getHunger() + 20);
                        this.heal(this.getMaxHealth());
                        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundVolume(), this.getVoicePitch());
                        this.spawnItemCrackParticles(stack.getItem());
                        this.spawnItemCrackParticles(Items.BONE);
                        this.spawnItemCrackParticles(Items.BONE_MEAL);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.spawnItemCrackParticles(Items.POISONOUS_POTATO);
                        this.setAgingDisabled(true);
                        this.eatFoodBonus(stack);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    if (stack.getItem() == IafItemRegistry.DRAGON_STAFF) {
                        if (player.isShiftKeyDown()) {
                            if (this.hasHomePosition) {
                                this.hasHomePosition = false;
                                player.displayClientMessage(new TranslatableComponent("dragon.command.remove_home"), true);
                                return InteractionResult.SUCCESS;
                            } else {
                                BlockPos pos = this.blockPosition();
                                this.homePos = pos;
                                this.hasHomePosition = true;
                                player.displayClientMessage(new TranslatableComponent("dragon.command.new_home", homePos.getX(), homePos.getY(), homePos.getZ()), true);
                                return InteractionResult.SUCCESS;
                            }
                        } else {
                            this.playSound(SoundEvents.ZOMBIE_INFECT, this.getSoundVolume(), this.getVoicePitch());
                            if (!level.isClientSide) {
                                this.setCommand(this.getCommand() + 1);
                                if (this.getCommand() > 2) {
                                    this.setCommand(0);
                                }
                            }
                            String commandText = "stand";
                            if (this.getCommand() == 1) {
                                commandText = "sit";
                            }
                            if (this.getCommand() == 2) {
                                commandText = "escort";
                            }
                            player.displayClientMessage(new TranslatableComponent("dragon.command." + commandText), true);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.mobInteract(player, hand);

    }

    protected abstract ItemLike getHeartItem();

    protected abstract Item getBloodItem();

    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    private ItemStack getRandomDrop() {
        ItemStack stack = getItemFromLootTable();
        if (stack.getItem() == IafItemRegistry.DRAGON_BONE) {
            this.playSound(SoundEvents.SKELETON_AMBIENT, 1, 1);
        } else {
            this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1, 1);
        }
        return stack;
    }

    public boolean canPositionBeSeen(double x, double y, double z) {
        HitResult result = this.level.clip(new ClipContext(new Vec3(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    public abstract ResourceLocation getDeadLootTable();

    public ItemStack getItemFromLootTable() {
        LootTable loottable = this.level.getServer().getLootTables().get(getDeadLootTable());
        LootContext.Builder lootcontext$builder = this.createLootContext(false, DamageSource.GENERIC);
        for (ItemStack itemstack : loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.ENTITY))) {
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    public void eatFoodBonus(ItemStack stack) {

    }


    public boolean requiresCustomPersistence() {
        return true;
    }

    public boolean isPersistenceRequired() {
        return true;
    }

    public void growDragon(int ageInDays) {
        if (this.isAgingDisabled()) {
            return;
        }
        this.setAgeInDays(this.getAgeInDays() + ageInDays);
        // NOTE: This is supposedly unnecessary with 1.18, but I left it in just in case
        this.setPos(this.getBoundingBox().getCenter());
        if (this.getAgeInDays() % 25 == 0) {
            for (int i = 0; i < this.getRenderSize() * 4; i++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float f = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX) + this.getBoundingBox().minX);
                float f1 = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY) + this.getBoundingBox().minY);
                float f2 = (float) (getRandom().nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ) + this.getBoundingBox().minZ);
                if (level.isClientSide) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, f, f1, f2, motionX, motionY, motionZ);
                }
            }

        }
        if (this.getDragonStage() >= 2)
            this.removeVehicle();
        this.updateAttributes();
    }

    public void spawnItemCrackParticles(Item item) {
        for (int i = 0; i < 15; i++) {
            double motionX = getRandom().nextGaussian() * 0.07D;
            double motionY = getRandom().nextGaussian() * 0.07D;
            double motionZ = getRandom().nextGaussian() * 0.07D;
            Vec3 headVec = this.getHeadPosition();
            if (level.isClientSide) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)), headVec.x, headVec.y, headVec.z, motionX, motionY, motionZ);
            }
        }
    }

    public boolean isTimeToWake() {
        return this.level.isDay() || this.getCommand() == 2;
    }

    private boolean isStuck() {
        return !this.isChained() && !this.isTame() && (!this.getNavigation().isDone() && (this.getNavigation().getPath() == null || this.getNavigation().getPath().getEndNode() != null && this.distanceToSqr(this.getNavigation().getPath().getEndNode().x, this.getNavigation().getPath().getEndNode().y, this.getNavigation().getPath().getEndNode().z) > 15)) && ticksStill > 80 && !this.isHovering() && canMove();
    }

    protected boolean isOverAir() {
        return isOverAir;
    }

    private boolean isOverAirLogic() {
        return level.isEmptyBlock(new BlockPos(this.getX(), this.getBoundingBox().minY - 1, this.getZ()));
    }

    public boolean isDiving() {
        return false;//isFlying() && motionY < -0.2;
    }

    public boolean isBeyondHeight() {
        if (this.getY() > this.level.getMaxBuildHeight()) {
            return true;
        }
        return this.getY() > IafConfig.maxDragonFlight;
    }

    private int calculateDownY() {
        if (this.getNavigation().getPath() != null) {
            Path path = this.getNavigation().getPath();
            Vec3 p = path.getEntityPosAtNode(this, Math.min(path.getNodeCount() - 1, path.getNextNodeIndex() + 1));
            if (p.y < this.getY() - 1) {
                return -1;
            }
        }
        return 1;
    }

    public void breakBlock() {
        if (this.blockBreakCounter > 0 || IafConfig.dragonBreakBlockCooldown == 0) {
            --this.blockBreakCounter;
            int bounds = 1;//(int)Math.ceil(this.getRenderSize() * 0.1);
            int flightModifier = isFlying() && this.getTarget() != null ? -1 : 1;
            int yMinus = calculateDownY();
            if (!this.isIceInWater() && (this.blockBreakCounter == 0 || IafConfig.dragonBreakBlockCooldown == 0) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
                if (IafConfig.dragonGriefing != 2 && (!this.isTame() || IafConfig.tamedDragonGriefing)) {
                    float hardness = IafConfig.dragonGriefing == 1 || this.getDragonStage() <= 3 ? 2.0F : 5.0F;
                    if (!isModelDead() && this.getDragonStage() >= 3 && (this.canMove() || this.getControllingPassenger() != null)) {
                        BlockPos.betweenClosedStream(
                                (int) Math.floor(this.getBoundingBox().minX) - bounds,
                                (int) Math.floor(this.getBoundingBox().minY) + yMinus,
                                (int) Math.floor(this.getBoundingBox().minZ) - bounds,
                                (int) Math.floor(this.getBoundingBox().maxX) + bounds,
                                (int) Math.floor(this.getBoundingBox().maxY) + bounds + flightModifier,
                                (int) Math.floor(this.getBoundingBox().maxZ) + bounds
                        ).forEach(pos -> {
                            if (MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, pos.getX(), pos.getY(), pos.getZ())))
                                return;
                            BlockState state = level.getBlockState(pos);
                            if (isBreakable(pos, state, hardness)) {
                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
                                if (!level.isClientSide) {
                                    if (random.nextFloat() <= IafConfig.dragonBlockBreakingDropChance && DragonUtils.canDropFromDragonBlockBreak(state)) {
                                        level.destroyBlock(pos, true);
                                    } else {
                                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    protected boolean isBreakable(BlockPos pos, BlockState state, float hardness) {
        return state.getMaterial().blocksMotion() && !state.isAir() && state.getFluidState().isEmpty() && !state.getShape(level, pos).isEmpty() && state.getDestroySpeed(level, pos) >= 0F && state.getDestroySpeed(level, pos) <= hardness && DragonUtils.canDragonBreak(state.getBlock()) && this.canDestroyBlock(pos, state);
    }

    public boolean isBlockPassable(BlockState state, BlockPos pos, BlockPos entityPos) {
        if (!isModelDead() && this.getDragonStage() >= 3) {
            if (IafConfig.dragonGriefing != 2 && (!this.isTame() || IafConfig.tamedDragonGriefing) && pos.getY() >= this.getY()) {
                return isBreakable(pos, state, IafConfig.dragonGriefing == 1 || this.getDragonStage() <= 3 ? 2.0F : 5.0F);
            }
        }
        return false;
    }

    protected boolean isIceInWater() {
        return false;
    }

    public void spawnGroundEffects() {
        for (int i = 0; i < this.getRenderSize(); i++) {
            for (int i1 = 0; i1 < 20; i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float radius = 0.75F * (0.7F * getRenderSize() / 3) * -3;
                float angle = (0.01745329251F * this.yBodyRot) + i1 * 1F;
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = getGround(new BlockPos(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY) - 1, Mth.floor(this.getZ() + extraZ)));
                BlockState BlockState = this.level.getBlockState(ground);
                if (BlockState.getMaterial() != Material.AIR) {
                    if (level.isClientSide) {
                        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    private BlockPos getGround(BlockPos blockPos) {
        while (level.isEmptyBlock(blockPos) && blockPos.getY() > 1) {
            blockPos = blockPos.below();
        }
        return blockPos;
    }

    public void fall(float distance, float damageMultiplier) {

    }

    public boolean isActuallyBreathingFire() {
        return this.fireTicks > 20 && this.isBreathingFire();
    }

    public boolean doesWantToLand() {
        return this.flyTicks > 6000 || isGoingDown() || flyTicks > 40 && this.flyProgress == 0 || this.isChained() && flyTicks > 100;
    }

    public abstract String getVariantName(int variant);

    public boolean shouldRiderSit() {
        return this.getControllingPassenger() != null;
    }

    public void positionRider(Entity passenger) {
        super.positionRider(passenger);
        if (this.hasPassenger(passenger)) {
            if (this.getControllingPassenger() == null || !this.getControllingPassenger().getUUID().equals(passenger.getUUID())) {
                updatePreyInMouth(passenger);
            } else {
                if (this.isModelDead()) {
                    passenger.stopRiding();
                }
                yBodyRot = getYRot();
                this.setYRot(passenger.getYRot());
                Vec3 riderPos = this.getRiderPosition();
                passenger.setPos(riderPos.x, riderPos.y + passenger.getBbHeight(), riderPos.z);
            }
        }
    }

    private float bob(float speed, float degree, boolean bounce, float f, float f1) {
        float bob = (float) (Math.sin(f * speed) * f1 * degree - f1 * degree);
        if (bounce) {
            bob = (float) -Math.abs((Math.sin(f * speed) * f1 * degree));
        }
        return bob * this.getRenderSize() / 3;
    }

    protected void updatePreyInMouth(Entity prey) {
        if (this.getAnimation() != ANIMATION_SHAKEPREY){
        this.setAnimation(ANIMATION_SHAKEPREY);
        }
        if (this.getAnimation() == ANIMATION_SHAKEPREY && this.getAnimationTick() > 55 && prey != null) {
            prey.hurt(DamageSource.mobAttack(this), prey instanceof Player ? 17F : (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 4);
            prey.stopRiding();
        }
        yBodyRot = getYRot();
        float modTick_0 = this.getAnimationTick() - 25;
        float modTick_1 = this.getAnimationTick() > 25 && this.getAnimationTick() < 55 ? 8 * Mth.clamp(Mth.sin((float) (Math.PI + modTick_0 * 0.25)), -0.8F, 0.8F) : 0;
        float modTick_2 = this.getAnimationTick() > 30 ? 10 : Math.max(0, this.getAnimationTick() - 20);
        float radius = 0.75F * (0.6F * getRenderSize() / 3) * -3;
        float angle = (0.01745329251F * this.yBodyRot) + 3.15F + (modTick_1 * 2F) * 0.015F;
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        double extraY = modTick_2 == 0 ? 0 : 0.035F * ((getRenderSize() / 3) + (modTick_2 * 0.5 * (getRenderSize() / 3)));
        prey.setPos(this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ);
    }

    public int getDragonStage() {
        int age = this.getAgeInDays();
        if (age >= 100) {
            return 5;
        } else if (age >= 75) {
            return 4;
        } else if (age >= 50) {
            return 3;
        } else if (age >= 25) {
            return 2;
        } else {
            return 1;
        }
    }

    public boolean isTeen() {
        return getDragonStage() < 4 && getDragonStage() > 2;
    }

    public boolean shouldDropLoot() {
        return getDragonStage() >= 4;
    }

    public boolean isBaby() {
        return getDragonStage() < 2;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setGender(this.getRandom().nextBoolean());
        int age = this.getRandom().nextInt(80) + 1;
        this.growDragon(age);
        this.setVariant(new Random().nextInt(4));
        this.setInSittingPose(false);
        this.updateAttributes();
        double healthStep = (maximumHealth - minimumHealth) / (125);
        this.heal((Math.round(minimumHealth + (healthStep * age))));
        this.usingGroundAttack = true;
        this.setHunger(50);
        return spawnDataIn;
    }

    @Override
    public boolean hurt(DamageSource dmg, float i) {
        if (this.isModelDead()) {
            return false;
        }
        if (this.isVehicle() && dmg.getEntity() != null && this.getControllingPassenger() != null && dmg.getEntity() == this.getControllingPassenger()) {
            return false;
        }

        if ((dmg.msgId.contains("arrow") || getVehicle() != null && dmg.getEntity() != null && dmg.getEntity().is(this.getVehicle())) && this.isPassenger()) {
            return false;
        }

        if (dmg == DamageSource.IN_WALL || dmg == DamageSource.FALLING_BLOCK || dmg == DamageSource.CRAMMING) {
            return false;
        }
        if (!level.isClientSide && dmg.getEntity() != null && this.getRandom().nextInt(4) == 0) {
            this.roar();
        }
        if (i > 0) {
            if (this.isSleeping()) {
                this.setInSittingPose(false);
                if (!this.isTame()) {
                    if (dmg.getEntity() instanceof Player) {
                        this.setTarget((Player) dmg.getEntity());
                    }
                }
            }
        }
        return super.hurt(dmg, i);

    }

    public void refreshDimensions() {
        super.refreshDimensions();
        float scale = Math.min(this.getRenderSize() * 0.35F, 7F);
        double prevX = getX();
        double prevY = getY();
        double prevZ = getZ();
        float localWidth = this.getBbWidth();
        if (this.getBbWidth() > localWidth && !this.firstTick && !this.level.isClientSide) {
            //this.setPosition(prevX, prevY, prevZ);
        }
        if (scale != lastScale) {
            resetParts(this.getRenderSize() / 3);
        }
        lastScale = scale;
    }

    @Override
    public void tick() {
        super.tick();
        refreshDimensions();
        updateParts();
        this.prevDragonPitch = getDragonPitch();
        if (level.isClientSide) {
            this.updateClientControls();
        }
        level.getProfiler().push("dragonLogic");
        this.maxUpStep = 1.2F;
        isOverAir = isOverAirLogic();
        logic.updateDragonCommon();
        if (this.isModelDead()) {
            if (!level.isClientSide && level.isEmptyBlock(new BlockPos(this.getX(), this.getBoundingBox().minY, this.getZ())) && this.getY() > -1) {
                this.move(MoverType.SELF, new Vec3(0, -0.2F, 0));
            }
            this.setBreathingFire(false);
            if(this.getDragonPitch() > 0){
                this.setDragonPitch(Math.min(0, this.getDragonPitch() - 5));
            }
            if(this.getDragonPitch() < 0){
                this.setDragonPitch(Math.max(0, this.getDragonPitch() + 5));
            }
        } else {
            if (level.isClientSide) {
                logic.updateDragonClient();
            } else {
                logic.updateDragonServer();
                logic.updateDragonAttack();
            }
        }
        level.getProfiler().pop();
        level.getProfiler().push("dragonFlight");
        if (isFlying() && !level.isClientSide) {
            this.flightManager.update();
        }
        level.getProfiler().pop();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.prevModelDeadProgress = this.modelDeadProgress;
        this.prevDiveProgress = this.diveProgress;
        prevAnimationProgresses[0] = this.sitProgress;
        prevAnimationProgresses[1] = this.sleepProgress;
        prevAnimationProgresses[2] = this.hoverProgress;
        prevAnimationProgresses[3] = this.flyProgress;
        prevAnimationProgresses[4] = this.fireBreathProgress;
        prevAnimationProgresses[5] = this.ridingProgress;
        prevAnimationProgresses[6] = this.tackleProgress;
        if (level.getDifficulty() == Difficulty.PEACEFUL && this.getTarget() instanceof Player) {
            this.setTarget(null);
        }
        if (this.isVehicle() && this.isModelDead()) {
            this.ejectPassengers();
        }
        if (this.isModelDead()) {
            this.setHovering(false);
            this.setFlying(false);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (animationTick > this.getAnimation().getDuration() && !level.isClientSide) {
            animationTick = 0;
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return this.getType().getDimensions().scale(this.getScale());
    }

    @Override
    public float getScale() {
        float scale = Math.min(this.getRenderSize() * 0.35F, 7F);
        return scale;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getRenderSize() {
        int stage = this.getDragonStage() - 1;
        float step = (growth_stages[stage][1] - growth_stages[stage][0]) / 25;
        if (this.getAgeInDays() > 125) {
            return growth_stages[stage][0] + (step * 25);
        }
        return growth_stages[stage][0] + (step * this.getAgeFactor());
    }

    private int getAgeFactor() {
        return (this.getDragonStage() > 1 ? this.getAgeInDays() - (25 * (this.getDragonStage() - 1)) : this.getAgeInDays());
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        this.getLookControl().setLookAt(entityIn, 30.0F, 30.0F);
        if (this.isTackling()) {
            return false;
        }
        if (this.isModelDead()) {
            return false;
        }
        boolean flag = entityIn.hurt(DamageSource.mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.doEnchantDamageEffects(this, entityIn);
        }

        return flag;
    }

    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                this.updateRiding(entity);
            }
        }
    }

    public void updateRiding(Entity riding) {
        if (riding != null && riding.hasPassenger(this) && riding instanceof Player) {
            int i = riding.getPassengers().indexOf(this);
            float radius = (i == 2 ? -0.2F : 0.5F) + (((Player) riding).isFallFlying() ? 2 : 0);
            float angle = (0.01745329251F * ((Player) riding).yBodyRot) + (i == 1 ? 90 : i == 0 ? -90 : 0);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            double extraY = (riding.isShiftKeyDown() ? 1.2D : 1.4D) + (i == 2 ? 0.4D : 0D);
            this.yHeadRot = ((Player) riding).yHeadRot;
            this.yRotO = ((Player) riding).yHeadRot;
            this.setPos(riding.getX() + extraX, riding.getY() + extraY, riding.getZ() + extraZ);
            if ((this.getControlState() == 1 << 4 || ((Player) riding).isFallFlying()) && !riding.isPassenger()) {
                this.stopRiding();
                if (level.isClientSide) {
                    IceAndFire.sendMSGToServer(new MessageStartRidingMob(this.getId(), false, true));
                }

            }

        }
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
        if (this.isModelDead()) {
            return NO_ANIMATION;
        }
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.isModelDead()) {
            return;
        }
        currentAnimation = animation;
    }

    public void playAmbientSound() {
        if (!this.isSleeping() && !this.isModelDead() && !this.level.isClientSide) {
            if (this.getAnimation() == this.NO_ANIMATION) {
                this.setAnimation(ANIMATION_SPEAK);
            }
            super.playAmbientSound();
        }
    }

    protected void playHurtSound(DamageSource source) {
        if (!this.isModelDead()) {
            if (this.getAnimation() == this.NO_ANIMATION && !this.level.isClientSide) {
                this.setAnimation(ANIMATION_SPEAK);
            }
            super.playHurtSound(source);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT};
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
        return null;
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal instanceof EntityDragonBase && otherAnimal != this && otherAnimal.getClass() == this.getClass()) {
            EntityDragonBase dragon = (EntityDragonBase) otherAnimal;
            return this.isMale() && !dragon.isMale() || !this.isMale() && dragon.isMale();
        }
        return false;
    }

    public EntityDragonEgg createEgg(EntityDragonBase ageable) {
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY());
        int k = Mth.floor(this.getZ());
        BlockPos pos = new BlockPos(i, j, k);
        EntityDragonEgg dragon = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG, this.level);
        dragon.setEggType(EnumDragonEgg.byMetadata(new Random().nextInt(4) + getStartMetaForType()));
        dragon.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        return dragon;
    }

    public int getStartMetaForType() {
        return 0;
    }

    public boolean isTargetBlocked(Vec3 target) {
        if (target != null) {
            BlockHitResult rayTrace = this.level.clip(new ClipContext(this.position().add(0, this.getEyeHeight(), 0), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (rayTrace != null && rayTrace.getLocation() != null) {
                BlockPos sidePos = rayTrace.getBlockPos();
                BlockPos pos = new BlockPos(rayTrace.getLocation());
                if (!level.isEmptyBlock(sidePos)) {
                    return true;
                } else if (!level.isEmptyBlock(pos)) {
                    return true;
                }
                return rayTrace != null && rayTrace.getType() == HitResult.Type.BLOCK;
            }
        }
        return false;
    }

    private double getFlySpeed() {
        return (2 + (this.getAgeInDays() / 125) * 2) * (this.isTackling() ? 2 : 1);
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLE).booleanValue();
    }

    public void setTackling(boolean tackling) {
        this.entityData.set(TACKLE, tackling);
    }

    public boolean isAgingDisabled() {
        return this.entityData.get(AGINGDISABLED).booleanValue();
    }

    public void setAgingDisabled(boolean isAgingDisabled) {
        this.entityData.set(AGINGDISABLED, isAgingDisabled);
    }


    public boolean isBoundToCrystal() {
        return this.entityData.get(CRYSTAL_BOUND).booleanValue();
    }

    public void setCrystalBound(boolean crystalBound) {
        this.entityData.set(CRYSTAL_BOUND, crystalBound);
    }


    public float getDistanceSquared(Vec3 Vector3d) {
        float f = (float) (this.getX() - Vector3d.x);
        float f1 = (float) (this.getY() - Vector3d.y);
        float f2 = (float) (this.getZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    public abstract Item getVariantScale(int variant);

    public abstract Item getVariantEgg(int variant);

    public abstract Item getSummoningCrystal();

    @OnlyIn(Dist.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getInstance();
        if (this.isRidingPlayer(mc.player)) {
            byte previousState = getControlState();
            goUp(mc.options.keyJump.isDown());
            goDown(IafKeybindRegistry.dragon_down.isDown());
            goAttack(IafKeybindRegistry.dragon_fireAttack.isDown());
            goStrike(IafKeybindRegistry.dragon_strike.isDown());
            goDismount(mc.options.keyShift.isDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getId(), controlState, getX(), getY(), getZ()));
            }
        }
        if (this.getVehicle() != null && this.getVehicle() == mc.player) {
            byte previousState = getControlState();
            goDismount(mc.options.keyShift.isDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getId(), controlState, getX(), getY(), getZ()));
            }
        }
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return false;
    }

    @Override
    public boolean canBeControlledByRider() {
        return true;
    }

    @Override
    public boolean isImmobile() {
        return this.getHealth() <= 0.0F || isOrderedToSit() && !this.isVehicle() || this.isModelDead();
    }

    @Override
    public void travel(Vec3 Vector3d) {
        if (this.getAnimation() == ANIMATION_SHAKEPREY || !this.canMove() && !this.isVehicle() || this.isOrderedToSit()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            Vector3d = new Vec3(0, 0, 0);
        }
        super.travel(Vector3d);
    }

    @Override
    public void move(MoverType typeIn, Vec3 pos) {
        if (this.isOrderedToSit() && !this.isVehicle()) {
            pos = new Vec3(0, pos.y(), 0);
        }
        super.move(typeIn, pos);

    }

    public void updateCheckPlayer() {
        double checklength = this.getBoundingBox().getSize() * 3;
        Player player = level.getNearestPlayer(this, checklength);
        if (this.isSleeping()) {
            if (player != null && !this.isOwnedBy(player) && !player.isCreative()) {
                this.setInSittingPose(false);
                this.setOrderedToSit(false);
                this.setTarget(player);
            }
        }
    }

    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    public boolean isDirectPathBetweenPoints(Vec3 vec1, Vec3 vec2) {
        BlockHitResult rayTrace = this.level.clip(new ClipContext(vec1, new Vec3(vec2.x, vec2.y + (double) this.getBbHeight() * 0.5D, vec2.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return rayTrace == null || rayTrace.getType() != HitResult.Type.BLOCK;
    }

    public void die(DamageSource cause) {
        if (cause.getEntity() != null) {
            //if (cause.getTrueSource() instanceof PlayerEntity) {
            //	((PlayerEntity) cause.getTrueSource()).addStat(ModAchievements.dragonSlayer, 1);
            //}
        }
        super.die(cause);
    }

    @Override
    public void onHearFlute(Player player) {
        if (this.isTame() && this.isOwnedBy(player)) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        }
    }

    public abstract SoundEvent getRoarSound();

    public void roar() {
        if (EntityGorgon.isStoneMob(this) || this.isModelDead()) {
            return;
        }
        if (random.nextBoolean()) {
            if (this.getAnimation() != ANIMATION_EPIC_ROAR) {
                this.setAnimation(ANIMATION_EPIC_ROAR);
                this.playSound(this.getRoarSound(), this.getSoundVolume() + 3 + Math.max(0, this.getDragonStage() - 2), this.getVoicePitch() * 0.7F);
            }
            if (this.getDragonStage() > 3) {
                int size = (this.getDragonStage() - 3) * 30;
                List<Entity> entities = level.getEntities(this, this.getBoundingBox().expandTowards(size, size, size));
                for (Entity entity : entities) {
                    boolean isStrongerDragon = entity instanceof EntityDragonBase && ((EntityDragonBase) entity).getDragonStage() >= this.getDragonStage();
                    if (entity instanceof LivingEntity && !isStrongerDragon) {
                        LivingEntity living = (LivingEntity) entity;
                        if (this.isOwnedBy(living) || this.isOwnersPet(living)) {
                            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 50 * size));
                        } else {
                            if (living.getItemBySlot(EquipmentSlot.HEAD).getItem() != IafItemRegistry.EARPLUGS) {
                                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 50 * size));
                            }
                        }
                    }
                }
            }
        } else {
            if (this.getAnimation() != ANIMATION_ROAR) {
                this.setAnimation(ANIMATION_ROAR);
                this.playSound(this.getRoarSound(), this.getSoundVolume() + 2 + Math.max(0, this.getDragonStage() - 3), this.getVoicePitch());
            }
            if (this.getDragonStage() > 3) {
                int size = (this.getDragonStage() - 3) * 30;
                List<Entity> entities = level.getEntities(this, this.getBoundingBox().expandTowards(size, size, size));
                for (Entity entity : entities) {
                    boolean isStrongerDragon = entity instanceof EntityDragonBase && ((EntityDragonBase) entity).getDragonStage() >= this.getDragonStage();
                    if (entity instanceof LivingEntity && !isStrongerDragon) {
                        LivingEntity living = (LivingEntity) entity;
                        if (this.isOwnedBy(living) || this.isOwnersPet(living)) {
                            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * size));
                        } else {
                            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * size));
                        }
                    }
                }
            }
        }
    }

    private boolean isOwnersPet(LivingEntity living) {
        return this.isTame() && this.getOwner() != null && living instanceof TamableAnimal && ((TamableAnimal) living).getOwner() != null && this.getOwner().is(((TamableAnimal) living).getOwner());
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vec3 vec1, Vec3 vec2) {

        HitResult movingobjectposition = this.level.clip(new ClipContext(vec1, vec2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return movingobjectposition == null || movingobjectposition.getType() != HitResult.Type.BLOCK;
    }

    public void processArrows() {
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, this.getBoundingBox());
        for (Entity entity : entities) {
            if (entity instanceof AbstractArrow) {

            }
        }
    }

    public boolean shouldRenderEyes() {
        return !this.isSleeping() && !this.isModelDead() && !this.isBlinking() && !EntityGorgon.isStoneMob(this);
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return DragonUtils.canTameDragonAttack(this, entity);
    }

    public void dropArmor() {

    }

    public boolean isChained() {
        return ChainProperties.hasChainData(this);
    }

    /*
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(ICamera camera) {
        boolean render = false;
        return inFrustrum(camera, headPart) || inFrustrum(camera, neckPart) ||
                inFrustrum(camera, leftWingLowerPart) || inFrustrum(camera, rightWingLowerPart) ||
                inFrustrum(camera, leftWingUpperPart) || inFrustrum(camera, rightWingUpperPart) ||
                inFrustrum(camera, tail1Part) || inFrustrum(camera, tail2Part) ||
                inFrustrum(camera, tail3Part) || inFrustrum(camera, tail4Part);
    }

    private boolean inFrustrum(ICamera camera, Entity entity) {
        return camera != null && entity != null && camera.isBoundingBoxInFrustum(entity.getBoundingBox());
    }

    */

    public HitResult rayTraceRider(Entity rider, double blockReachDistance, float partialTicks) {
        Vec3 Vector3d = rider.getEyePosition(partialTicks);
        Vec3 Vector3d1 = rider.getViewVector(partialTicks);
        Vec3 Vector3d2 = Vector3d.add(Vector3d1.x * blockReachDistance, Vector3d1.y * blockReachDistance, Vector3d1.z * blockReachDistance);
        return this.level.clip(new ClipContext(Vector3d, Vector3d2, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
    }

    public Vec3 getRiderPosition() {
        float sitProg = this.sitProgress * 0.015F;
        float deadProg = this.modelDeadProgress * -0.02F;
        float hoverProg = this.hoverProgress * 0.03F;
        float flyProg = this.flyProgress * 0.01F;
        float sleepProg = this.sleepProgress * -0.025F;
        float extraAgeScale = this.getScale()*0.2F;
        float pitchX = 0;
        float pitchY = 0;
        float dragonPitch = getDragonPitch();
        if (dragonPitch > 0) {
            pitchX = Math.min(dragonPitch / 90, 0.3F);
            pitchY = -(dragonPitch / 90) * 2F;
        }
        if (dragonPitch < 0) {//going up
            pitchY = (dragonPitch / 90) * 0.1F;
            pitchX = Math.max(dragonPitch / 90, -0.7F);
        }
        float xzMod = (0.15F + pitchX) * getRenderSize() + extraAgeScale;
        float headPosX = (float) (getX() + (xzMod) * Math.cos((getYRot() + 90) * Math.PI / 180));
        float headPosY = (float) (getY() + (0.7F + sitProg + hoverProg + deadProg + sleepProg + flyProg + pitchY) * getRenderSize() * 0.3F + extraAgeScale);
        float headPosZ = (float) (getZ() + (xzMod) * Math.sin((getYRot() + 90) * Math.PI / 180));
        return new Vec3(headPosX, headPosY, headPosZ);
    }

    public void kill() {
        this.remove();
        this.setDeathStage(this.getAgeInDays() / 5);
        this.setModelDead(false);
    }

    public Vec3 getHeadPosition() {
        float sitProg = this.sitProgress * 0.015F;
        float deadProg = this.modelDeadProgress * -0.02F;
        float hoverProg = this.hoverProgress * 0.03F;
        float flyProg = this.flyProgress * 0.01F;
        int tick = 0;
        if (this.getAnimationTick() < 10) {
            tick = this.getAnimationTick();
        } else if (this.getAnimationTick() > 50) {
            tick = 60 - this.getAnimationTick();
        } else {
            tick = 10;
        }
        float epicRoarProg = this.getAnimation() == ANIMATION_EPIC_ROAR ? tick * 0.1F : 0;
        float sleepProg = this.sleepProgress * -0.025F;
        float pitchMulti = 0;
        float pitchAdjustment = 0;
        float pitchMinus = 0;
        float dragonPitch = -getDragonPitch();
        if (this.isFlying() || this.isHovering()) {
            pitchMulti = (float) Math.sin(Math.toRadians(dragonPitch));
            pitchAdjustment = 1.2F;
            pitchMulti *= 2.1F * Math.abs(dragonPitch) / 90;
            if (pitchMulti > 0) {
                pitchMulti *= 1.5F - pitchMulti * 0.5F;
            }
            if (pitchMulti < 0) {
                pitchMulti *= 1.3F - pitchMulti * 0.1F;
            }
            pitchMinus = 0.3F * Math.abs(dragonPitch / 90);
            if (dragonPitch >= 0) {
                pitchAdjustment = 0.6F * Math.abs(dragonPitch / 90);
                pitchMinus = 0.95F * Math.abs(dragonPitch / 90);
            }
        }
        float flightXz = 1.0F + flyProg + hoverProg;
        float xzMod = (1.7F * getRenderSize() * 0.3F * flightXz) + getRenderSize() * (0.3F * (float) Math.sin((dragonPitch + 90) * Math.PI / 180) * pitchAdjustment - pitchMinus - hoverProg * 0.45F);
        float headPosX = (float) (getX() + (xzMod) * Math.cos((getYRot() + 90) * Math.PI / 180));
        float headPosY = (float) (getY() + (0.7F + sitProg + hoverProg + deadProg + epicRoarProg + sleepProg + flyProg + pitchMulti) * getRenderSize() * 0.3F);
        float headPosZ = (float) (getZ() + (xzMod) * Math.sin((getYRot() + 90) * Math.PI / 180));
        return new Vec3(headPosX, headPosY, headPosZ);
    }

    public abstract void stimulateFire(double burnX, double burnY, double burnZ, int syncType);

    public void randomizeAttacks() {
        this.airAttack = IafDragonAttacks.Air.values()[getRandom().nextInt(IafDragonAttacks.Air.values().length)];
        this.groundAttack = IafDragonAttacks.Ground.values()[getRandom().nextInt(IafDragonAttacks.Ground.values().length)];

    }

    public void tryScorchTarget() {
        LivingEntity entity = this.getTarget();
        if (entity != null) {
            float distX = (float) (entity.getX() - this.getX());
            float distZ = (float) (entity.getZ() - this.getZ());
            if (this.isBreathingFire()) {
                if (this.isActuallyBreathingFire()) {
                    setYRot(yBodyRot);
                    if (this.tickCount % 5 == 0) {
                        this.playSound(IafSoundRegistry.FIREDRAGON_BREATH, 4, 1);
                    }
                    stimulateFire(this.getX() + distX * this.fireTicks / 40, entity.getY(), this.getZ() + distZ * this.fireTicks / 40, 1);
                }
            } else {
                this.setBreathingFire(true);
            }
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity LivingEntityIn) {
        super.setTarget(LivingEntityIn);
        this.flightManager.onSetAttackTarget(LivingEntityIn);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (this.isTame() && target instanceof TamableAnimal) {
            TamableAnimal tamableTarget = (TamableAnimal) target;
            UUID targetOwner = tamableTarget.getOwnerUUID();
            if (targetOwner != null && targetOwner.equals(this.getOwnerUUID())) {
                return false;
            }
        }
        return super.wantsToAttack(target, owner);
    }

    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && DragonUtils.isAlive(target);
    }

    public boolean isPart(Entity entityHit) {
        return headPart != null && headPart.is(entityHit) || neckPart != null && neckPart.is(entityHit) ||
                leftWingLowerPart != null && leftWingLowerPart.is(entityHit) || rightWingLowerPart != null && rightWingLowerPart.is(entityHit) ||
                leftWingUpperPart != null && leftWingUpperPart.is(entityHit) || rightWingUpperPart != null && rightWingUpperPart.is(entityHit) ||
                tail1Part != null && tail1Part.is(entityHit) || tail2Part != null && tail2Part.is(entityHit) ||
                tail3Part != null && tail3Part.is(entityHit) || tail4Part != null && tail4Part.is(entityHit);
    }

    public double getFlightSpeedModifier() {
        return IafConfig.dragonFlightSpeedMod;
    }

    public boolean isAllowedToTriggerFlight() {
        return (this.hasFlightClearance() && this.onGround || this.isInWater()) && !this.isOrderedToSit() && this.getPassengers().isEmpty() && !this.isBaby() && !this.isSleeping() && this.canMove();
    }

    public BlockPos getEscortPosition() {
        return this.getOwner() != null ? new BlockPos(this.getOwner().position()) : this.blockPosition();
    }

    public boolean shouldTPtoOwner() {

        return this.getOwner() != null && this.distanceTo(this.getOwner()) > 10;
    }

    public boolean isSkeletal() {
        return this.getDeathStage() >= (this.getAgeInDays() / 5) / 2;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean save(CompoundTag compound) {
        return this.saveAsPassenger(compound);
    }

    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        if (soundIn == SoundEvents.GENERIC_EAT || soundIn == this.getAmbientSound() || soundIn == this.getHurtSound(null) || soundIn == this.getDeathSound() || soundIn == this.getRoarSound()) {
            if (!this.isSilent() && this.headPart != null) {
                this.level.playSound(null, this.headPart.getX(), this.headPart.getY(), this.headPart.getZ(), soundIn, this.getSoundSource(), volume, pitch);
            }
        } else {
            super.playSound(soundIn, volume, pitch);
        }
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    public boolean hasFlightClearance() {
        BlockPos topOfBB = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        for (int i = 1; i < 4; i++) {
            if (!level.isEmptyBlock(topOfBB.above(i))) {
                return false;
            }
        }
        return true;
    }

    public ItemStack getItemBySlot(EquipmentSlot slotIn) {
        if (slotIn == EquipmentSlot.OFFHAND) {
            return dragonInventory.getItem(0);
        } else if (slotIn == EquipmentSlot.HEAD) {
            return dragonInventory.getItem(1);
        } else if (slotIn == EquipmentSlot.CHEST) {
            return dragonInventory.getItem(2);
        } else if (slotIn == EquipmentSlot.LEGS) {
            return dragonInventory.getItem(3);
        } else if (slotIn == EquipmentSlot.FEET) {
            return dragonInventory.getItem(4);
        }
        return super.getItemBySlot(slotIn);
    }

    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlot.OFFHAND) {
            dragonInventory.setItem(0, stack);
        } else if (slotIn == EquipmentSlot.HEAD) {
            dragonInventory.setItem(1, stack);
        } else if (slotIn == EquipmentSlot.CHEST) {
            dragonInventory.setItem(2, stack);
        } else if (slotIn == EquipmentSlot.LEGS) {
            dragonInventory.setItem(3, stack);
        } else if (slotIn == EquipmentSlot.FEET) {
            dragonInventory.setItem(4, stack);
        } else {
            super.getItemBySlot(slotIn);
        }
        updateAttributes();
    }

    public float getVoicePitch() {
        return super.getVoicePitch();
    }

    public SoundEvent getBabyFireSound() {
        return SoundEvents.FIRE_EXTINGUISH;
    }

    protected boolean isPlayingAttackAnimation() {
        return this.getAnimation() == ANIMATION_BITE || this.getAnimation() == ANIMATION_SHAKEPREY || this.getAnimation() == ANIMATION_WINGBLAST ||
                this.getAnimation() == ANIMATION_TAILWHACK;
    }

    protected IafDragonLogic createDragonLogic() {
        return new IafDragonLogic(this);
    }

    protected int getFlightChancePerTick() {
        return FLIGHT_CHANCE_PER_TICK;
    }

    public void onRemovedFromWorld() {
        if (IafConfig.chunkLoadSummonCrystal) {
            if (this.isBoundToCrystal()) {
                DragonPosWorldData data = DragonPosWorldData.get(level);
                if (data != null) {
                    data.addDragon(this.getUUID(), this.blockPosition());
                }
            }
        }
        super.onRemovedFromWorld();
    }

    public int maxSearchNodes() {
        return 50;
    }
}