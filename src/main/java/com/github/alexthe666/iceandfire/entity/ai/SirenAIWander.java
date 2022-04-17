package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class SirenAIWander extends RandomStrollGoal {

    private EntitySiren siren;

    public SirenAIWander(EntitySiren creatureIn, double speedIn) {
        super(creatureIn, speedIn);
        this.siren = creatureIn;
    }

    public boolean canUse() {
        return !this.siren.isInWater() && !siren.isSinging() && super.canUse();
    }

    public boolean canContinueToUse() {
        return !this.siren.isInWater() && !siren.isSinging() && super.canContinueToUse();
    }
}