package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSwarmer;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MyrmexAISummonerHurtTarget extends TargetGoal {
    EntityMyrmexSwarmer tameable;
    LivingEntity attacker;
    private int timestamp;

    public MyrmexAISummonerHurtTarget(EntityMyrmexSwarmer theEntityMyrmexSwarmerIn) {
        super(theEntityMyrmexSwarmerIn, false);
        this.tameable = theEntityMyrmexSwarmerIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        LivingEntity LivingEntity = this.tameable.getSummoner();

        if (LivingEntity == null) {
            return false;
        } else {
            this.attacker = LivingEntity.getLastHurtMob();
            int i = LivingEntity.getLastHurtMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.tameable.shouldAttackEntity(this.attacker, LivingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity LivingEntity = this.tameable.getSummoner();

        if (LivingEntity != null) {
            this.timestamp = LivingEntity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}