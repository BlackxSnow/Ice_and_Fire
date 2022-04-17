package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;

public class HippogryphAITargetItems<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    private final int targetChance;
    protected ItemEntity targetEntity;

    public HippogryphAITargetItems(Mob creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public HippogryphAITargetItems(Mob creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 20, checkSight, onlyNearby, null);
    }

    public HippogryphAITargetItems(Mob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
        this.targetEntitySelector = new Predicate<ItemEntity>() {
            @Override
            public boolean apply(@Nullable ItemEntity item) {
                return item instanceof ItemEntity && !item.getItem().isEmpty() && item.getItem().getItem() == Items.RABBIT_FOOT;
            }
        };
    }

    @Override
    public boolean canUse() {

        if (!((EntityHippogryph) this.mob).canMove()) {
            return false;
        }
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
            EntityHippogryph hippo = (EntityHippogryph) this.mob;
            this.targetEntity.getItem().shrink(1);
            this.mob.playSound(SoundEvents.GENERIC_EAT, 1, 1);
            hippo.setAnimation(EntityHippogryph.ANIMATION_EAT);
            hippo.feedings++;
            hippo.heal(4);
            if (hippo.feedings > 3 && (hippo.feedings > 7 || hippo.getRandom().nextInt(3) == 0) && !hippo.isTame() && this.targetEntity.getThrower() != null && this.mob.level.getPlayerByUUID(this.targetEntity.getThrower()) != null) {
                Player owner = this.mob.level.getPlayerByUUID(this.targetEntity.getThrower());
                if (owner != null) {
                    hippo.tame(owner);
                    hippo.setTarget(null);
                    hippo.setCommand(1);
                    hippo.setOrderedToSit(true);
                }
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