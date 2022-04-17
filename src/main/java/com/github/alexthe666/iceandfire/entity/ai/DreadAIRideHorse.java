package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntityDreadKnight;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class DreadAIRideHorse extends Goal {
    private final EntityDreadKnight knight;
    private AbstractHorse horse;

    public DreadAIRideHorse(EntityDreadKnight knight) {
        this.knight = knight;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (this.knight.isPassenger()) {
            return false;
        } else {
            List<AbstractHorse> list = this.knight.level.getEntitiesOfClass(AbstractHorse.class, this.knight.getBoundingBox().inflate(16.0D, 7.0D, 16.0D));

            if (list.isEmpty()) {
                return false;
            } else {
                for (AbstractHorse entityirongolem : list) {
                    if (!entityirongolem.isVehicle()) {
                        this.horse = entityirongolem;
                        break;
                    }
                }

                return this.horse != null;
            }
        }
    }

    public boolean canContinueToUse() {
        return !this.knight.isPassenger() && this.horse != null && !this.horse.isVehicle();
    }

    public void start() {
        this.horse.getNavigation().stop();
    }

    public void stop() {
        this.horse = null;
        this.knight.getNavigation().stop();
    }

    public void tick() {
        this.knight.getLookControl().setLookAt(this.horse, 30.0F, 30.0F);

        this.knight.getNavigation().moveTo(this.horse, 1.2D);

        if (this.knight.distanceToSqr(this.horse) < 4.0D) {
            this.horse.setTamed(true);
            this.knight.getNavigation().stop();
            this.knight.startRiding(horse);
        }
    }
}