package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAIPickupBabies<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super LivingEntity> targetEntitySelector;
    public EntityMyrmexWorker myrmex;
    protected LivingEntity targetEntity;

    public MyrmexAIPickupBabies(EntityMyrmexWorker myrmex) {
        super(myrmex, false, false);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
        this.targetEntitySelector = new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity myrmex) {
                return myrmex != null && (myrmex instanceof EntityMyrmexBase && ((EntityMyrmexBase) myrmex).getGrowthStage() < 2 && !((EntityMyrmexBase) myrmex).isInNursery() || myrmex instanceof EntityMyrmexEgg && !((EntityMyrmexEgg) myrmex).isInNursery());
            }
        };
        this.myrmex = myrmex;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.myrmex.canMove() || this.myrmex.holdingSomething() || !this.myrmex.getNavigation().isDone() || this.myrmex.shouldEnterHive() || !this.myrmex.keepSearching || this.myrmex.holdingBaby()) {
            return false;
        }
        List<LivingEntity> listBabies = this.mob.level.getLoadedEntitiesOfClass(LivingEntity.class, this.getTargetableArea(20), this.targetEntitySelector);
        if (listBabies.isEmpty()) {
            return false;
        } else {
            Collections.sort(listBabies, this.theNearestAttackableTargetSorter);
            this.targetEntity = listBabies.get(0);
            return true;
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
        if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
            this.stop();
        }
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < 2) {
            this.targetEntity.startRiding(this.myrmex);
            stop();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(EntityMyrmexBase theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}