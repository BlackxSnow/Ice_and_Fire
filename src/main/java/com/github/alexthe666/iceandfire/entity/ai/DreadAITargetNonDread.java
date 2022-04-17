package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import com.google.common.base.Predicate;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class DreadAITargetNonDread extends NearestAttackableTargetGoal {

    public DreadAITargetNonDread(Mob entityIn, Class<LivingEntity> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 0, checkSight, false, targetSelector);
    }

    protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions targetPredicate) {
        if (super.canAttack(target, targetPredicate)) {
            return !(target instanceof IDreadMob) && DragonUtils.isAlive(target);
        }
        return false;
    }


}
