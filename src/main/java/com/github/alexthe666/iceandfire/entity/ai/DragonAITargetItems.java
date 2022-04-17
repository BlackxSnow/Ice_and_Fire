package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DragonAITargetItems<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    private final int targetChance;
    protected ItemEntity targetEntity;
    private boolean isIce = false;

    public DragonAITargetItems(Mob creature, boolean checkSight) {
        this(creature, checkSight, false);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public DragonAITargetItems(Mob creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 20, checkSight, onlyNearby, null);
        isIce = creature instanceof EntityIceDragon;
    }

    public DragonAITargetItems(Mob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        isIce = creature instanceof EntityIceDragon;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.targetEntitySelector = new Predicate<ItemEntity>() {
            @Override
            public boolean apply(@Nullable ItemEntity item) {
                return item instanceof ItemEntity && !item.getItem().isEmpty() && item.getItem().getItem() != null && FoodUtils.getFoodPoints(item.getItem(), true, isIce) > 0;
            }
        };
    }

    @Override
    public boolean canUse() {
        if (((EntityDragonBase) this.mob).getHunger() >= 100) {
            return false;
        }
        if (!((EntityDragonBase) this.mob).canMove()) {
            return false;
        }

        if (this.targetChance > 0 && this.mob.getRandom().nextInt(10) != 0) {
            return false;
        } else {

            List<ItemEntity> list = this.mob.level.getLoadedEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);

            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                return true;
            }
        }
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || this.targetEntity != null && this.targetEntity.isAlive()) {
            this.stop();
        }
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < 1) {
            this.targetEntity.getItem().shrink(1);
            this.mob.playSound(SoundEvents.GENERIC_EAT, 1, 1);
            int hunger = FoodUtils.getFoodPoints(this.targetEntity.getItem(), true, isIce);
            ((EntityDragonBase) this.mob).setHunger(Math.min(100, ((EntityDragonBase) this.mob).getHunger() + hunger));
            ((EntityDragonBase) this.mob).eatFoodBonus(this.targetEntity.getItem());
            this.mob.setHealth(Math.min(this.mob.getMaxHealth(), (int) (this.mob.getHealth() + FoodUtils.getFoodPoints(this.targetEntity.getItem(), true, isIce))));
            if (EntityDragonBase.ANIMATION_EAT != null) {
                ((EntityDragonBase) this.mob).setAnimation(EntityDragonBase.ANIMATION_EAT);
            }
            for (int i = 0; i < 4; i++) {
                ((EntityDragonBase) this.mob).spawnItemCrackParticles(this.targetEntity.getItem().getItem());
            }
            stop();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}