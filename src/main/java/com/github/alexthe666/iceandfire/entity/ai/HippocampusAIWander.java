package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.TamableAnimal;

public class HippocampusAIWander extends RandomStrollGoal {

    public HippocampusAIWander(PathfinderMob creatureIn, double speedIn) {
        super(creatureIn, speedIn);
    }

    public boolean canUse() {
        return !(mob instanceof TamableAnimal && ((TamableAnimal) mob).isOrderedToSit()) && !this.mob.isInWater() && super.canUse();
    }

    public boolean canContinueToUse() {
        return !(mob instanceof TamableAnimal && ((TamableAnimal) mob).isOrderedToSit()) && !this.mob.isInWater() && super.canContinueToUse();
    }
}