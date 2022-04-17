package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSwarmer;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAISummonerHurtByTarget extends TargetGoal {
    EntityMyrmexSwarmer tameable;
    LivingEntity attacker;
    private int timestamp;

    public MyrmexAISummonerHurtByTarget(EntityMyrmexSwarmer theDefendingTameableIn) {
        super(theDefendingTameableIn, false);
        this.tameable = theDefendingTameableIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        LivingEntity LivingEntity = this.tameable.getSummoner();

        if (LivingEntity == null) {
            return false;
        } else {
            this.attacker = LivingEntity.getLastHurtByMob();
            int i = LivingEntity.getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.tameable.shouldAttackEntity(this.attacker, LivingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity LivingEntity = this.tameable.getSummoner();

        if (LivingEntity != null) {
            this.timestamp = LivingEntity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}