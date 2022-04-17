package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.item.ItemHippogryphEgg;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class HippogryphAIMate extends Goal {
    private final EntityHippogryph hippo;
    private final Class<? extends Animal> mateClass;
    Level world;
    int spawnBabyDelay;
    double moveSpeed;
    private EntityHippogryph targetMate;

    public HippogryphAIMate(EntityHippogryph animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public HippogryphAIMate(EntityHippogryph hippogryph, double speed, Class<? extends Animal> mate) {
        this.hippo = hippogryph;
        this.world = hippogryph.level;
        this.mateClass = mate;
        this.moveSpeed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.hippo.isInLove() || this.hippo.isOrderedToSit()) {
            return false;
        } else {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    public boolean canContinueToUse() {
        return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    public void stop() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    public void tick() {
        this.hippo.getLookControl().setLookAt(this.targetMate, 10.0F, (float) this.hippo.getMaxHeadXRot());
        this.hippo.getNavigation().moveTo(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay >= 60 && this.hippo.distanceToSqr(this.targetMate) < 9.0D) {
            this.spawnBaby();
        }
    }

    private EntityHippogryph getNearbyMate() {
        List<EntityHippogryph> list = this.world.getEntitiesOfClass(EntityHippogryph.class, this.hippo.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityHippogryph entityanimal = null;

        for (EntityHippogryph entityanimal1 : list) {
            if (this.hippo.canMate(entityanimal1) && this.hippo.distanceToSqr(entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.hippo.distanceToSqr(entityanimal1);
            }
        }

        return entityanimal;
    }

    private void spawnBaby() {
        ItemEntity egg = new ItemEntity(world, this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), ItemHippogryphEgg.createEggStack(this.hippo.getEnumVariant(), this.targetMate.getEnumVariant()));

        if (egg != null) {
            Player PlayerEntity = this.hippo.getLoveCause();

            if (PlayerEntity == null && this.targetMate.getLoveCause() != null) {
                PlayerEntity = this.targetMate.getLoveCause();
            }
            this.hippo.setAge(6000);
            this.targetMate.setAge(6000);
            this.hippo.resetLove();
            this.targetMate.resetLove();
            egg.moveTo(this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), 0.0F, 0.0F);
            if (!world.isClientSide) {
                this.world.addFreshEntity(egg);
            }
            Random random = this.hippo.getRandom();

            for (int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextDouble() * (double) this.hippo.getBbWidth() * 2.0D - (double) this.hippo.getBbWidth();
                double d4 = 0.5D + random.nextDouble() * (double) this.hippo.getBbHeight();
                double d5 = random.nextDouble() * (double) this.hippo.getBbWidth() * 2.0D - (double) this.hippo.getBbWidth();
                this.world.addParticle(ParticleTypes.HEART, this.hippo.getX() + d3, this.hippo.getY() + d4, this.hippo.getZ() + d5, d0, d1, d2);
            }

            if (this.world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.world.addFreshEntity(new ExperienceOrb(this.world, this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), random.nextInt(7) + 1));
            }
        }
    }
}