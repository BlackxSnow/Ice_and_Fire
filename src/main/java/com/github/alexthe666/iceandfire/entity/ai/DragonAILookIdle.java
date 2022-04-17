package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraft.world.entity.ai.goal.Goal;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DragonAILookIdle extends Goal {
    private EntityDragonBase dragon;
    private double lookX;
    private double lookZ;
    private int idleTime;

    public DragonAILookIdle(EntityDragonBase prehistoric) {
        this.dragon = prehistoric;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.dragon.canMove() || dragon.getAnimation() == EntityDragonBase.ANIMATION_SHAKEPREY) {
            return false;
        }
        return this.dragon.getRandom().nextFloat() < 0.02F;
    }

    @Override
    public boolean canContinueToUse() {
        return this.idleTime >= 0;
    }

    @Override
    public void start() {
        double d0 = (Math.PI * 2D) * this.dragon.getRandom().nextDouble();
        this.lookX = Math.cos(d0);
        this.lookZ = Math.sin(d0);
        this.idleTime = 20 + this.dragon.getRandom().nextInt(20);
    }

    @Override
    public void tick() {
    	if (this.idleTime > 0) {
    		--this.idleTime;
    	}
        this.dragon.getLookControl().setLookAt(this.dragon.getX() + this.lookX, this.dragon.getY() + this.dragon.getEyeHeight(), this.dragon.getZ() + this.lookZ, this.dragon.getMaxHeadYRot(), this.dragon.getMaxHeadXRot());
    }
}