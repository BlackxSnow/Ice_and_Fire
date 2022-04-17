package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.google.common.base.Predicate;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DeathwormAITargetItems<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    private final int targetChance;
    protected ItemEntity targetEntity;

    public DeathwormAITargetItems(EntityDeathWorm creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public DeathwormAITargetItems(EntityDeathWorm creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 0, checkSight, onlyNearby, null);
    }

    public DeathwormAITargetItems(EntityDeathWorm creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
        this.targetEntitySelector = new Predicate<ItemEntity>() {
            @Override
            public boolean apply(@Nullable ItemEntity item) {
                return item instanceof ItemEntity && !item.getItem().isEmpty() && item.getItem().getItem() == Item.byBlock(Blocks.TNT);
            }
        };
        this.setFlags(EnumSet.of(Flag.TARGET));

    }

    @Override
    public boolean canUse() {
        List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            this.targetEntity = list.get(0);
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
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < 1) {
            EntityDeathWorm deathWorm = (EntityDeathWorm) this.mob;
            this.targetEntity.getItem().shrink(1);
            this.mob.playSound(SoundEvents.GENERIC_EAT, 1, 1);
            deathWorm.setAnimation(EntityDeathWorm.ANIMATION_BITE);
            deathWorm.setExplosive(true);
            stop();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }


}