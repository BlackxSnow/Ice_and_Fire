package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityCyclops;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.InteractionHand;

public class CyclopsAIAttackMelee extends MeleeAttackGoal {

    public CyclopsAIAttackMelee(EntityCyclops creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    protected void checkAndPerformAttack(LivingEntity entity, double distance) {
        double d0 = this.getAttackReachSqr(entity);
        if (isCyclopsBlinded() && distance >= 36D) {
            this.stop();
            return;
        }
        if (distance <= d0 ) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(entity);
        }
    }

    private boolean isCyclopsBlinded() {
        return this.mob instanceof EntityCyclops && ((EntityCyclops) this.mob).isBlinded();
    }
}
