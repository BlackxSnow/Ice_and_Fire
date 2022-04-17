package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;

public class FlyingAITarget extends NearestAttackableTargetGoal {

    public FlyingAITarget(Mob creature, Class classTarget, boolean checkSight) {
        super(creature, classTarget, checkSight);
    }

    public FlyingAITarget(Mob creature, Class classTarget, boolean checkSight, boolean onlyNearby) {
        super(creature, classTarget, checkSight, onlyNearby);
    }

    public FlyingAITarget(Mob creature, Class classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate targetSelector) {
        super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
    }

    @Override
    protected AABB getTargetSearchArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
    }

    public boolean canUse() {
        if (mob instanceof EntitySeaSerpent && (((EntitySeaSerpent) mob).isJumpingOutOfWater() || !mob.isInWater())) {
            return false;
        }
        return super.canUse();
    }

}
