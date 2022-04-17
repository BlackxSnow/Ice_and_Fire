package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAIFindGaurdingEntity<T extends EntityMyrmexBase> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super EntityMyrmexBase> targetEntitySelector;
    public EntityMyrmexSoldier myrmex;
    protected EntityMyrmexBase targetEntity;

    public MyrmexAIFindGaurdingEntity(EntityMyrmexSoldier myrmex) {
        super(myrmex, false, false);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
        this.targetEntitySelector = new Predicate<EntityMyrmexBase>() {
            @Override
            public boolean apply(@Nullable EntityMyrmexBase myrmex) {
                return myrmex != null && !(myrmex instanceof EntityMyrmexSoldier) && myrmex.getGrowthStage() > 1 && EntityMyrmexBase.haveSameHive(MyrmexAIFindGaurdingEntity.this.myrmex, myrmex) && !myrmex.isBeingGuarded && myrmex.needsGaurding();
            }
        };
        this.myrmex = myrmex;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.myrmex.canMove() || this.myrmex.getTarget() != null || this.myrmex.guardingEntity != null) {
            return false;
        }
        List<EntityMyrmexBase> list = this.mob.level.getEntitiesOfClass(EntityMyrmexBase.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            this.myrmex.guardingEntity = list.get(0);
            return true;
        }
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
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