package com.github.alexthe666.iceandfire.entity.ai;

import java.util.ArrayList;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class SirenAIFindWaterTarget extends Goal {
    private EntitySiren mob;

    public SirenAIFindWaterTarget(EntitySiren mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isInWater()) {
            return false;
        }
        if (this.mob.getRandom().nextFloat() < 0.5F) {
            Path path = this.mob.getNavigation().getPath();
            if (path != null && path.getEndNode() != null || !this.mob.getNavigation().isDone() && !this.mob.isDirectPathBetweenPoints(this.mob.position(), new Vec3(path.getEndNode().x, path.getEndNode().y, path.getEndNode().z))) {
                this.mob.getNavigation().stop();
            }
            if (this.mob.getNavigation().isDone()) {
                Vec3 vec3 = this.findWaterTarget();
                if (vec3 != null) {
                    this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    public Vec3 findWaterTarget() {
        if (this.mob.getTarget() == null || !this.mob.getTarget().isAlive()) {
            List<Vec3> water = new ArrayList<>();
            List<Vec3> singTargets = new ArrayList<>();
            for (int x = (int) this.mob.getX() - 5; x < (int) this.mob.getX() + 5; x++) {
                for (int y = (int) this.mob.getY() - 5; y < (int) this.mob.getY() + 5; y++) {
                    for (int z = (int) this.mob.getZ() - 5; z < (int) this.mob.getZ() + 5; z++) {
                        if (mob.wantsToSing()) {
                            if (this.mob.level.getBlockState(new BlockPos(x, y, z)).getMaterial().isSolid() && this.mob.level.isEmptyBlock(new BlockPos(x, y + 1, z)) && this.mob.isDirectPathBetweenPoints(this.mob.position(), new Vec3(x, y + 1, z))) {
                                singTargets.add(new Vec3(x, y + 1, z));
                            }
                        }
                        if (this.mob.level.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.WATER && this.mob.isDirectPathBetweenPoints(this.mob.position(), new Vec3(x, y, z))) {
                            water.add(new Vec3(x, y, z));
                        }

                    }
                }
            }
            if (!singTargets.isEmpty()) {
                return singTargets.get(this.mob.getRandom().nextInt(singTargets.size()));

            }
            if (!water.isEmpty()) {
                return water.get(this.mob.getRandom().nextInt(water.size()));
            }
        } else {
            BlockPos blockpos1 = this.mob.getTarget().blockPosition();
            return new Vec3(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
        }
        return null;
    }
}