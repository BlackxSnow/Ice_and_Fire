package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;


import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class CyclopsAITargetSheepPlayers<T extends LivingEntity> extends NearestAttackableTargetGoal {

    public CyclopsAITargetSheepPlayers(Mob goalOwnerIn, Class targetClassIn, boolean checkSight) {
        super(goalOwnerIn, targetClassIn, 0, checkSight, true, new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity livingEntity) {
                return false; //TODO Sheep hunt cyclops
            }
        });
        this.setFlags(EnumSet.of(Flag.TARGET));

    }


}