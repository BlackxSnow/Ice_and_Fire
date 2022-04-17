package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.GorgonAIStareAttack;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IHumanoid;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import io.netty.buffer.Unpooled;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class EntityGorgon extends Monster implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid {

    public static Animation ANIMATION_SCARE;
    public static Animation ANIMATION_HIT;
    private int animationTick;
    private Animation currentAnimation;
    private GorgonAIStareAttack aiStare;
    private MeleeAttackGoal aiMelee;
    private int playerStatueCooldown;

    public EntityGorgon(EntityType type, Level worldIn) {
        super(type, worldIn);
        ANIMATION_SCARE = Animation.create(30);
        ANIMATION_HIT = Animation.create(10);
    }

    public static boolean isEntityLookingAt(LivingEntity looker, LivingEntity seen, double degree) {
        degree *= 1 + (looker.distanceTo(seen) * 0.1);
        Vec3 Vector3d = looker.getViewVector(1.0F).normalize();
        Vec3 Vector3d1 = new Vec3(seen.getX() - looker.getX(), seen.getBoundingBox().minY + (double) seen.getEyeHeight() - (looker.getY() + (double) looker.getEyeHeight()), seen.getZ() - looker.getZ());
        double d0 = Vector3d1.length();
        Vector3d1 = Vector3d1.normalize();
        double d1 = Vector3d.dot(Vector3d1);
        return d1 > 1.0D - degree / d0 && (looker.canSee(seen) && !isStoneMob(seen));
    }

    public static boolean isStoneMob(LivingEntity mob) {
        return mob instanceof EntityStoneStatue;
    }

    public static boolean isBlindfolded(LivingEntity attackTarget) {
        return attackTarget != null && (attackTarget.getItemBySlot(EquipmentSlot.HEAD).getItem() == IafItemRegistry.BLINDFOLD || attackTarget.hasEffect(MobEffects.BLINDNESS) || ServerEvents.isBlindMob(attackTarget));
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        HitResult result = this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getType() != HitResult.Type.MISS;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, aiStare = new GorgonAIStareAttack(this, 1.0D, 0, 15.0F));
        this.goalSelector.addGoal(3, aiMelee = new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            public boolean canUse() {
                interval = 20;
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F) {
            public boolean canContinueToUse() {
                if (this.lookAt != null && this.lookAt instanceof Player && ((Player) this.lookAt).isCreative()) {
                    return false;
                }
                return super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, 10, false, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity.isAlive();
            }
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, LivingEntity.class, 10, true, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity)  || (entity instanceof IBlacklistedFromStatues && ((IBlacklistedFromStatues) entity).canBeTurnedToStone());
            }
        }));
        this.goalSelector.removeGoal(aiMelee);
    }

    public void attackEntityWithRangedAttack(LivingEntity entity) {
        if (!(entity instanceof Mob) && entity instanceof LivingEntity) {
            forcePreyToLook((Mob) entity);
        }
    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget().hasEffect(MobEffects.BLINDNESS) || this.getTarget() != null && this.getTarget() instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) this.getTarget()).canBeTurnedToStone();
        if (blindness && this.deathTime == 0) {
            if (this.getAnimation() != ANIMATION_HIT) {
                this.setAnimation(ANIMATION_HIT);
            }
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false, true));
            }
        }
        return super.doHurtTarget(entityIn);
    }

    public void setTarget(@Nullable LivingEntity LivingEntityIn) {
        super.setTarget(LivingEntityIn);
        if (LivingEntityIn != null && !level.isClientSide) {


            boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || LivingEntityIn.hasEffect(MobEffects.BLINDNESS) || LivingEntityIn instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) LivingEntityIn).canBeTurnedToStone() || isBlindfolded(LivingEntityIn);
            if (blindness && this.deathTime == 0) {
                this.goalSelector.addGoal(3, aiMelee);
                this.goalSelector.removeGoal(aiStare);
            } else {
                this.goalSelector.addGoal(3, aiStare);
                this.goalSelector.removeGoal(aiMelee);
            }
        }
    }

    protected int getExperienceReward(Player player) {
        return 30;
    }

    protected void tickDeath() {
        ++this.deathTime;
        this.ambientSoundTime = 20;
        if (this.level.isClientSide) {
            for (int k = 0; k < 5; ++k) {
                double d2 = 0.4;
                double d0 = 0.1;
                double d1 = 0.1;
                IceAndFire.PROXY.spawnParticle("blood", this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY(), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d2, d0, d1);
            }
        }
        if (this.deathTime >= 200) {
            if (!this.level.isClientSide && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))) {
                int i = this.getExperienceReward(this.lastHurtByPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.lastHurtByPlayer, i);
                while (i > 0) {
                    int j = ExperienceOrb.getExperienceValue(i);
                    i -= j;
                    this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), j));
                }
            }
            this.remove();

            for (int k = 0; k < 20; ++k) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.CLOUD, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth(), d2, d0, d1);
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        if (playerStatueCooldown > 0) {
            playerStatueCooldown--;
        }
        if (this.getTarget() != null) {
            boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget().hasEffect(MobEffects.BLINDNESS);
            if (!blindness && this.deathTime == 0 && this.getTarget() instanceof Mob && !(this.getTarget() instanceof Player)) {
                forcePreyToLook((Mob) this.getTarget());
            }
            if(isEntityLookingAt(this.getTarget(), this, 0.4)){
                this.getLookControl().setLookAt(this.getTarget().getX(), this.getTarget().getY() + (double) this.getTarget().getEyeHeight(), this.getTarget().getZ(), (float) this.getMaxHeadYRot(), (float) this.getMaxHeadXRot());
            }
        }


        if (this.getTarget() != null && isEntityLookingAt(this, this.getTarget(), 0.4) && isEntityLookingAt(this.getTarget(), this, 0.4) && !isBlindfolded(this.getTarget())) {
            boolean blindness = this.hasEffect(MobEffects.BLINDNESS) || this.getTarget().hasEffect(MobEffects.BLINDNESS) || this.getTarget() instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) this.getTarget()).canBeTurnedToStone();
            if (!blindness && this.deathTime == 0) {
                if (this.getAnimation() != ANIMATION_SCARE) {
                    this.playSound(IafSoundRegistry.GORGON_ATTACK, 1, 1);
                    this.setAnimation(ANIMATION_SCARE);
                }
                if (this.getAnimation() == ANIMATION_SCARE) {
                    if (this.getAnimationTick() > 10) {
                        if (!level.isClientSide) {
                            if (playerStatueCooldown == 0) {
                                EntityStoneStatue statue = EntityStoneStatue.buildStatueEntity(this.getTarget());
                                statue.absMoveTo(this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ(), this.getTarget().yRot, this.getTarget().xRot);
                                if (!level.isClientSide) {
                                    level.addFreshEntity(statue);
                                }
                                statue.yRotO = this.getTarget().yRot;
                                statue.yRot = this.getTarget().yRot;
                                statue.yHeadRot = this.getTarget().yRot;
                                statue.yBodyRot = this.getTarget().yRot;
                                statue.yBodyRotO = this.getTarget().yRot;
                                playerStatueCooldown = 40;
                                if (this.getTarget() instanceof Player) {
                                    this.getTarget().hurt(IafDamageRegistry.GORGON_DMG, Integer.MAX_VALUE);
                                } else {
                                    this.getTarget().remove();
                                }
                                this.setTarget(null);

                            }
                        }
                    }
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public int getMaxHeadXRot() {
        return 10;
    }

    public int getMaxHeadYRot() {
        return 30;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public void forcePreyToLook(LivingEntity mob) {
        if(mob instanceof Mob){
            Mob mobEntity = (Mob)mob;
            mobEntity.getLookControl().setLookAt(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ(), (float) mobEntity.getMaxHeadYRot(), (float) mobEntity.getMaxHeadXRot());

        }
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, IafConfig.gorgonMaxHealth)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                //ATTACK
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                //ARMOR
                .add(Attributes.ARMOR, 1.0D);
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
        return new Animation[]{ANIMATION_SCARE, ANIMATION_HIT};
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.GORGON_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSoundRegistry.GORGON_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.GORGON_DIE;
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
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
