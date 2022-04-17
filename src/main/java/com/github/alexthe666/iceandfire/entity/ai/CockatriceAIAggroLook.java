package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class CockatriceAIAggroLook extends NearestAttackableTargetGoal<Player> {
    private final EntityCockatrice cockatrice;
    private Player player;
    private int aggroTime;
    private int teleportTime;

    public CockatriceAIAggroLook(EntityCockatrice endermanIn) {
        super(endermanIn, Player.class, false);
        this.cockatrice = endermanIn;
    }

    /**
     * Returns whether the Goal should begin execution.
     */
    public boolean canUse() {
        if (cockatrice.isTame()) {
            return false;
        }
        double d0 = this.getFollowDistance();
        this.player = this.cockatrice.level.getNearestPlayer(new TargetingConditions() {
            public boolean test(@Nullable LivingEntity attacker, LivingEntity target) {
                return target != null && EntityGorgon.isEntityLookingAt(target, CockatriceAIAggroLook.this.cockatrice, EntityCockatrice.VIEW_RADIUS) && CockatriceAIAggroLook.this.cockatrice.distanceTo(target) < d0;
            }
        }, this.cockatrice.getX(), this.cockatrice.getY(), this.cockatrice.getZ());
        return this.player != null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.aggroTime = 5;
        this.teleportTime = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.player = null;
        super.stop();
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    public boolean canContinueToUse() {
        if (this.player != null && !this.player.isCreative() && !this.player.isSpectator()) {
            if (!EntityGorgon.isEntityLookingAt(this.player, this.cockatrice, 0.4F)) {
                return false;
            } else {
                this.cockatrice.lookAt(this.player, 10.0F, 10.0F);
                if (!this.cockatrice.isTame()) {
                    this.cockatrice.setTargetedEntity(this.player.getId());
                    this.cockatrice.setTarget(this.player);
                }
                return true;
            }
        } else {
            return this.targetMob != null && this.targetMob.isAlive() || super.canContinueToUse();
        }
    }
}
