package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.props.MiscProperties;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.message.MessageSpawnParticleAt;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/*
    dragon logic separation for client, server and shared sides.
 */
public class IafDragonLogic {

    private EntityDragonBase dragon;

    public IafDragonLogic(EntityDragonBase dragon) {
        this.dragon = dragon;
    }

    /*
    logic done exclusively on server.
    */
    public void updateDragonServer() {

        Player ridingPlayer = dragon.getRidingPlayer();
        if(ridingPlayer != null){
            if (dragon.isGoingUp()) {
                if (!dragon.isFlying() && !dragon.isHovering()) {
                    dragon.spacebarTicks += 2;
                }
            } else if (dragon.isDismounting()) {
                if (dragon.isFlying() || dragon.isHovering()) {
                    dragon.setDeltaMovement(dragon.getDeltaMovement().add(0, -0.04, 0));
                    dragon.setFlying(false);
                    dragon.setHovering(false);
                }
            }
        }
        if (!dragon.isDismounting() && (dragon.isFlying() || dragon.isHovering())) {
            dragon.setDeltaMovement(dragon.getDeltaMovement().add(0, 0.01, 0));
        }
        if (dragon.isAttacking() && dragon.getControllingPassenger() != null && dragon.getDragonStage() > 1) {
            dragon.setBreathingFire(true);
            dragon.riderShootFire(dragon.getControllingPassenger());
            dragon.fireStopTicks = 10;
        }
        if (dragon.isStriking() && dragon.getControllingPassenger() != null && dragon.getControllingPassenger() instanceof Player) {
            LivingEntity target = DragonUtils.riderLookingAtEntity(dragon, (Player) dragon.getControllingPassenger(), dragon.getDragonStage() + (dragon.getBoundingBox().maxX - dragon.getBoundingBox().minX));
            if (dragon.getAnimation() != EntityDragonBase.ANIMATION_BITE) {
                dragon.setAnimation(EntityDragonBase.ANIMATION_BITE);
            }
            if (target != null && !DragonUtils.hasSameOwner(dragon, target)) {
                target.hurt(DamageSource.mobAttack(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (dragon.getControllingPassenger() != null && dragon.getControllingPassenger().isShiftKeyDown()) {
            if (dragon.getControllingPassenger() instanceof LivingEntity)
                MiscProperties.setDismountedDragon((LivingEntity) dragon.getControllingPassenger(), true);
            dragon.getControllingPassenger().stopRiding();
        }
        if (dragon.isFlying() && !dragon.isHovering() && dragon.getControllingPassenger() != null && !dragon.isOnGround() && Math.max(Math.abs(dragon.getDeltaMovement().x()), Math.abs(dragon.getDeltaMovement().z())) < 0.1F) {
            dragon.setHovering(true);
            dragon.setFlying(false);
        }
        if (dragon.isHovering() && !dragon.isFlying() && dragon.getControllingPassenger() != null && !dragon.isOnGround() && Math.max(Math.abs(dragon.getDeltaMovement().x()), Math.abs(dragon.getDeltaMovement().z())) > 0.1F) {
            dragon.setFlying(true);
            dragon.usingGroundAttack = false;
            dragon.setHovering(false);
        }
        if (dragon.spacebarTicks > 0) {
            dragon.spacebarTicks--;
        }
        if (dragon.spacebarTicks > 20 && dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner()) && !dragon.isFlying() && !dragon.isHovering()) {
            dragon.setHovering(true);
        }
        if (dragon.isOverAir() && !dragon.isPassenger()) {
            double ydist = dragon.yo - dragon.getY();//down 0.4 up -0.38
            float planeDist = (float) ((Math.abs(dragon.getDeltaMovement().x) + Math.abs(dragon.getDeltaMovement().z)) * 6F);
            if (!dragon.isHovering()) {
                dragon.incrementDragonPitch((float) (ydist) * 10);
            }
            dragon.setDragonPitch(Mth.clamp(dragon.getDragonPitch(), -60, 40));
            float plateau = 2;
            if (dragon.getDragonPitch() > plateau) {
                //down
                //this.motionY -= 0.2D;
                dragon.decrementDragonPitch(planeDist * Math.abs(dragon.getDragonPitch()) / 90);
            }
            if (dragon.getDragonPitch() < -plateau) {//-2
                //up
                dragon.incrementDragonPitch(planeDist * Math.abs(dragon.getDragonPitch()) / 90);
            }
            if (dragon.getDragonPitch() > 2F) {
                dragon.decrementDragonPitch(1);
            } else if (dragon.getDragonPitch() < -2F) {
                dragon.incrementDragonPitch(1);
            }
            if (dragon.getDragonPitch() < -45 && planeDist < 3) {
                if (dragon.isFlying() && !dragon.isHovering()) {
                    dragon.setHovering(true);
                }
            }
        } else {
            dragon.setDragonPitch(0);
        }
        if(dragon.lookingForRoostAIFlag && dragon.getLastHurtByMob() != null || dragon.isSleeping()){
            dragon.lookingForRoostAIFlag = false;
        }
        if (IafConfig.doDragonsSleep && !dragon.isSleeping() && !dragon.isTimeToWake() && dragon.getPassengers().isEmpty() && this.dragon.getCommand() != 2) {
            if(dragon.hasHomePosition && dragon.getRestrictCenter() != null && dragon.getDistanceSquared(Vec3.atCenterOf(dragon.getRestrictCenter())) > dragon.getBbWidth() * 10
                    && this.dragon.getCommand() != 2 && this.dragon.getCommand() != 1){
                dragon.lookingForRoostAIFlag = true;
            }else{
                dragon.lookingForRoostAIFlag = false;
                if(!dragon.isInWater() && dragon.isOnGround() && !dragon.isFlying() && !dragon.isHovering() && dragon.getTarget() == null){
                    dragon.setInSittingPose(true);
                }
            }
        } else{
            dragon.lookingForRoostAIFlag = false;
        }
        if (dragon.isSleeping() && (dragon.isFlying() || dragon.isHovering() || dragon.isInWater() || (dragon.level.canSeeSkyFromBelowWater(dragon.blockPosition()) && dragon.isTimeToWake() && !dragon.isTame() || dragon.isTimeToWake() && dragon.isTame()) || dragon.getTarget() != null || !dragon.getPassengers().isEmpty())) {
            dragon.setInSittingPose(false);
        }
        if (dragon.isOrderedToSit() && dragon.getControllingPassenger() != null) {
            dragon.setOrderedToSit(false);
        }
        if (dragon.isVehicle() && !dragon.isOverAir() && dragon.isFlying() && !dragon.isHovering() && dragon.flyTicks > 40) {
            dragon.setFlying(false);
        }
        if (dragon.blockBreakCounter <= 0) {
            dragon.blockBreakCounter = IafConfig.dragonBreakBlockCooldown;
        }
        dragon.updateBurnTarget();
        if (dragon.isOrderedToSit() && (dragon.getCommand() != 1 || dragon.getControllingPassenger() != null)) {
            dragon.setOrderedToSit(false);
        }
        if (!dragon.isOrderedToSit() && dragon.getCommand() == 1 && dragon.getControllingPassenger() == null) {
            dragon.setOrderedToSit(true);
        }
        if (dragon.isOrderedToSit()) {
            dragon.getNavigation().stop();
        }
        if (dragon.isInLove()) {
            dragon.level.broadcastEntityEvent(dragon, (byte) 18);
        }
        if ((int) dragon.xo == (int) dragon.getX() && (int) dragon.zo == (int) dragon.getZ()) {
            dragon.ticksStill++;
        } else {
            dragon.ticksStill = 0;
        }
        if (dragon.isTackling() && !dragon.isFlying() && dragon.isOnGround()) {
            dragon.tacklingTicks++;
            if (dragon.tacklingTicks == 40) {
                dragon.tacklingTicks = 0;
                dragon.setTackling(false);
                dragon.setFlying(false);
            }
        }
        if (dragon.getRandom().nextInt(500) == 0 && !dragon.isModelDead() && !dragon.isSleeping()) {
            dragon.roar();
        }
        if (dragon.isFlying() && dragon.getTarget() != null && dragon.airAttack == IafDragonAttacks.Air.TACKLE) {
            dragon.setTackling(true);
        }
        if (dragon.isFlying() && dragon.getTarget() != null && dragon.isTackling() && dragon.getBoundingBox().expandTowards(2.0D, 2.0D, 2.0D).intersects(dragon.getTarget().getBoundingBox())) {
            dragon.usingGroundAttack = true;
            dragon.randomizeAttacks();
            dragon.getTarget().hurt(DamageSource.mobAttack(dragon), dragon.getDragonStage() * 3);
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (dragon.isTackling() && (dragon.getTarget() == null || dragon.airAttack != IafDragonAttacks.Air.TACKLE)) {
            dragon.setTackling(false);
            dragon.randomizeAttacks();
        }
        if (dragon.isPassenger()) {
            dragon.setFlying(false);
            dragon.setHovering(false);
            dragon.setInSittingPose(false);
        }
        if (dragon.isFlying() && dragon.tickCount % 40 == 0 || dragon.isFlying() && dragon.isSleeping()) {
            dragon.setInSittingPose(false);
        }
        if (!dragon.canMove()) {
            if (dragon.getTarget() != null) {
                dragon.setTarget(null);
            }
            dragon.getNavigation().stop();
        }
        if (!dragon.isTame()) {
            dragon.updateCheckPlayer();
        }
        if (dragon.isModelDead() && (dragon.isFlying() || dragon.isHovering())) {
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (ridingPlayer == null) {
            if ((dragon.useFlyingPathFinder() || dragon.isHovering()) && dragon.navigatorType != 1) {
                dragon.switchNavigator(1);
            }
        } else {
            if ((dragon.useFlyingPathFinder() || dragon.isHovering()) && dragon.navigatorType != 2) {
                dragon.switchNavigator(2);
            }
        }
        if (!dragon.useFlyingPathFinder() && !dragon.isHovering() && dragon.navigatorType != 0) {
            dragon.switchNavigator(0);
        }
        if (!dragon.isOverAir() && dragon.doesWantToLand() && (dragon.isFlying() || dragon.isHovering()) && !dragon.isInWater()) {
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (dragon.isHovering() && dragon.isFlying() && dragon.flyTicks > 40) {
            dragon.setHovering(false);
            dragon.setFlying(true);
        }
        if(dragon.isHovering()){
            dragon.hoverTicks++;
        }else{
            dragon.hoverTicks = 0;
        }
        if (dragon.isHovering() && !dragon.isFlying()) {
            if (dragon.isSleeping()) {
                dragon.setHovering(false);
            }
            if (dragon.doesWantToLand() && !dragon.isOnGround() && !dragon.isInWater()) {
                dragon.setDeltaMovement(dragon.getDeltaMovement().add(0, -0.25, 0));
            } else {
                if ((dragon.getControllingPassenger() == null || dragon.getControllingPassenger() instanceof EntityDreadQueen) && !dragon.isBeyondHeight()) {
                    double up = dragon.isInWater() ? 0.12D : 0.08D;
                    dragon.setDeltaMovement(dragon.getDeltaMovement().add(0, up, 0));
                }
                if (dragon.hoverTicks > 40) {
                    dragon.setHovering(false);
                    dragon.setFlying(true);
                    dragon.flyHovering = 0;
                    dragon.hoverTicks = 0;
                    dragon.flyTicks = 0;
                }
            }
        }
        if (dragon.isSleeping()) {
            dragon.getNavigation().stop();
        }
        if ((dragon.isOnGround() || dragon.isInWater()) && dragon.flyTicks != 0) {
            dragon.flyTicks = 0;
        }
        if (dragon.isAllowedToTriggerFlight() && dragon.isFlying() && dragon.doesWantToLand()) {
            dragon.setFlying(false);
            dragon.setHovering(dragon.isOverAir());
            if (!dragon.isOverAir()) {
                dragon.flyTicks = 0;
                dragon.setFlying(false);
            }
        }
        if (dragon.isFlying()) {
            dragon.flyTicks++;
        }
        if ((dragon.isHovering() || dragon.isFlying()) && dragon.isSleeping()) {
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (!dragon.isFlying() && !dragon.isHovering()) {
            if (dragon.isAllowedToTriggerFlight() || dragon.getY() < -1) {
                if (dragon.getRandom().nextInt(dragon.getFlightChancePerTick()) == 0 || dragon.getY() < -1 || dragon.getTarget() != null && Math.abs(dragon.getTarget().getY()- dragon.getY()) > 5 || dragon.isInWater() && !dragon.isIceInWater()) {
                    dragon.setHovering(true);
                    dragon.setInSittingPose(false);
                    dragon.setOrderedToSit(false);
                    dragon.flyHovering = 0;
                    dragon.hoverTicks = 0;
                    dragon.flyTicks = 0;
                }
            }
        }
        if (dragon.getTarget() != null) {
            if (!dragon.getPassengers().isEmpty() && dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner())) {
                dragon.setTarget(null);
            }
            if (!DragonUtils.isAlive(dragon.getTarget())) {
                dragon.setTarget(null);
            }
        }
        if (!dragon.isAgingDisabled()) {
            dragon.setAgeInTicks(dragon.getAgeInTicks() + 1);
            if (dragon.getAgeInTicks() % 24000 == 0) {
                dragon.updateAttributes();
                dragon.growDragon(0);
            }
        }
        if (dragon.tickCount % IafConfig.dragonHungerTickRate == 0 && IafConfig.dragonHungerTickRate > 0) {
            if (dragon.getHunger() > 0) {
                dragon.setHunger(dragon.getHunger() - 1);
            }
        }
        if ((dragon.groundAttack == IafDragonAttacks.Ground.FIRE) && dragon.getDragonStage() < 2) {
            dragon.usingGroundAttack = true;
            dragon.randomizeAttacks();
            dragon.playSound(dragon.getBabyFireSound(), 1, 1);
        }
        if (dragon.isBreathingFire()) {
            if(dragon.isSleeping() || dragon.isModelDead()){
                dragon.setBreathingFire(false);
                dragon.randomizeAttacks();
                dragon.fireTicks = 0;
            }
            if (dragon.burningTarget == null) {
                if (dragon.fireTicks > dragon.getDragonStage() * 25 || dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner()) && dragon.fireStopTicks <= 0) {
                    dragon.setBreathingFire(false);
                    dragon.randomizeAttacks();
                    dragon.fireTicks = 0;
                }
            }

            if (dragon.fireStopTicks > 0 && dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner())) {
                dragon.fireStopTicks--;
            }
        }
        if (dragon.isFlying() && dragon.getTarget() != null && dragon.getBoundingBox().expandTowards(3.0F, 3.0F, 3.0F).intersects(dragon.getTarget().getBoundingBox())) {
            dragon.doHurtTarget(dragon.getTarget());
        }
        if (dragon.isFlying() && dragon.airAttack == IafDragonAttacks.Air.TACKLE && (dragon.horizontalCollision || dragon.isOnGround())) {
            dragon.usingGroundAttack = true;
            dragon.setFlying(false);
            dragon.setHovering(false);
        }
        if (dragon.isFlying() && dragon.usingGroundAttack) {
            dragon.airAttack = IafDragonAttacks.Air.TACKLE;
        }
        if (dragon.isFlying() && dragon.airAttack == IafDragonAttacks.Air.TACKLE && dragon.getTarget() != null && dragon.isTargetBlocked(dragon.getTarget().position())) {
            dragon.randomizeAttacks();
        }

    }

    /*
    logic done exclusively on client.
    */
    public void updateDragonClient() {
        if (!dragon.isModelDead()) {
            dragon.turn_buffer.calculateChainSwingBuffer(50, 0, 4, dragon);
            dragon.tail_buffer.calculateChainSwingBuffer(90, 20, 5F, dragon);
            if (!dragon.isOnGround()) {
                dragon.roll_buffer.calculateChainFlapBuffer(55, 1, 2F, 0.5F, dragon);
                dragon.pitch_buffer.calculateChainWaveBuffer(90, 10, 1F, 0.5F, dragon);
                dragon.pitch_buffer_body.calculateChainWaveBuffer(80, 10, 1, 0.5F, dragon);
            }
        }
        if (dragon.walkCycle < 39) {
            dragon.walkCycle++;
        } else {
            dragon.walkCycle = 0;
        }
        if (dragon.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST && (dragon.getAnimationTick() == 17 || dragon.getAnimationTick() == 22 || dragon.getAnimationTick() == 28)) {
            dragon.spawnGroundEffects();
        }
        dragon.legSolver.update(dragon, dragon.getRenderSize() / 3F);

        if (dragon.flightCycle == 11) {
            dragon.spawnGroundEffects();
        }
        if (dragon.isModelDead() && dragon.flightCycle != 0) {
            dragon.flightCycle = 0;
        }
    }

    /*
    logic done on server and client on parallel.
    */
    public void updateDragonCommon() {
        if (dragon.isBreathingFire()) {
            dragon.fireTicks++;
            if (dragon.burnProgress < 40) {
                dragon.burnProgress++;
            }
        } else {
            dragon.burnProgress = 0;
        }
        if (dragon.flightCycle == 2 && !dragon.isDiving() && (dragon.isFlying() || dragon.isHovering())) {
            float dragonSoundVolume = IafConfig.dragonFlapNoiseDistance;
            float dragonSoundPitch = dragon.getVoicePitch();
            dragon.playSound(IafSoundRegistry.DRAGON_FLIGHT, dragonSoundVolume, dragonSoundPitch);
        }
        if (dragon.flightCycle < 58) {
            dragon.flightCycle += 2;
        } else {
            dragon.flightCycle = 0;
        }
        boolean sitting = dragon.isOrderedToSit() && !dragon.isModelDead() && !dragon.isSleeping() && !dragon.isHovering() && !dragon.isFlying();
        if (sitting && dragon.sitProgress < 20.0F) {
            dragon.sitProgress += 0.5F;
        } else if (!sitting && dragon.sitProgress > 0.0F) {
            dragon.sitProgress -= 0.5F;
        }
        boolean sleeping = dragon.isSleeping() && !dragon.isHovering() && !dragon.isFlying();
        if (sleeping && dragon.sleepProgress < 20.0F) {
            dragon.sleepProgress += 0.5F;
        } else if (!sleeping && dragon.sleepProgress > 0.0F) {
            dragon.sleepProgress -= 0.5F;
        }
        boolean fireBreathing = dragon.isBreathingFire();
        dragon.prevFireBreathProgress = dragon.fireBreathProgress;
        if (fireBreathing && dragon.fireBreathProgress < 10.0F) {
            dragon.fireBreathProgress += 0.5F;
        } else if (!fireBreathing && dragon.fireBreathProgress > 0.0F) {
            dragon.fireBreathProgress -= 0.5F;
        }
        boolean hovering = dragon.isHovering() || dragon.isFlying() && dragon.airAttack == IafDragonAttacks.Air.HOVER_BLAST && dragon.getTarget() != null && dragon.distanceTo(dragon.getTarget()) < 17F;
        if (hovering && dragon.hoverProgress < 20.0F) {
            dragon.hoverProgress += 0.5F;
        } else if (!hovering && dragon.hoverProgress > 0.0F) {
            dragon.hoverProgress -= 2F;
        }
        boolean diving = dragon.isDiving();
        if (diving && dragon.diveProgress < 10.0F) {
            dragon.diveProgress += 1F;
        } else if (!diving && dragon.diveProgress > 0.0F) {
            dragon.diveProgress -= 2F;
        }
        boolean tackling = dragon.isTackling() && dragon.isOverAir();
        if (tackling && dragon.tackleProgress < 5F) {
            dragon.tackleProgress += 0.5F;
        } else if (!tackling && dragon.tackleProgress > 0.0F) {
            dragon.tackleProgress -= 1.5F;
        }
        boolean flying = dragon.isFlying();
        if (flying && dragon.flyProgress < 20.0F) {
            dragon.flyProgress += 0.5F;
        } else if (!flying && dragon.flyProgress > 0.0F) {
            dragon.flyProgress -= 2F;
        }
        boolean modeldead = dragon.isModelDead();
        if (modeldead && dragon.modelDeadProgress < 20.0F) {
            dragon.modelDeadProgress += 0.5F;
        } else if (!modeldead && dragon.modelDeadProgress > 0.0F) {
            dragon.modelDeadProgress -= 0.5F;
        }
        boolean riding = dragon.isPassenger() && dragon.getVehicle() != null && dragon.getVehicle() instanceof Player;
        if (riding && dragon.ridingProgress < 20.0F) {
            dragon.ridingProgress += 0.5F;
        } else if (!riding && dragon.ridingProgress > 0.0F) {
            dragon.ridingProgress -= 0.5F;
        }
        if (dragon.hasHadHornUse) {
            dragon.hasHadHornUse = false;
        }
        if ((dragon.groundAttack == IafDragonAttacks.Ground.FIRE) && dragon.getDragonStage() < 2) {
            if (dragon.level.isClientSide) {
                dragon.spawnBabyParticles();
            }
            dragon.randomizeAttacks();
        }
    }


    /*
    logic handler for the dragon's melee attacks.
    */
    public void updateDragonAttack() {
        if (dragon.isPlayingAttackAnimation() && dragon.getTarget() != null && dragon.canSee(dragon.getTarget())) {
            LivingEntity target = dragon.getTarget();
            double dist = dragon.distanceTo(target);
            if (dist < dragon.getRenderSize() * 0.2574 * 2 + 2) {
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_BITE) {
                    if (dragon.getAnimationTick() > 15 && dragon.getAnimationTick() < 25) {
                        target.hurt(DamageSource.mobAttack(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                        dragon.usingGroundAttack = dragon.getRandom().nextBoolean();
                        dragon.randomizeAttacks();
                    }
                }
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_TAILWHACK) {
                    if (dragon.getAnimationTick() > 20 && dragon.getAnimationTick() < 30) {
                        target.hurt(DamageSource.mobAttack(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                        target.knockback( dragon.getDragonStage() * 0.6F, Mth.sin(dragon.yRot * 0.017453292F), -Mth.cos(dragon.yRot * 0.017453292F));
                        dragon.usingGroundAttack = dragon.getRandom().nextBoolean();
                        dragon.randomizeAttacks();
                    }
                }
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST) {
                    if ((dragon.getAnimationTick() == 15 || dragon.getAnimationTick() == 25 || dragon.getAnimationTick() == 35)) {
                        target.hurt(DamageSource.mobAttack(dragon), ((int) dragon.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                        target.knockback( dragon.getDragonStage() * 0.6F, Mth.sin(dragon.yRot * 0.017453292F), -Mth.cos(dragon.yRot * 0.017453292F));
                        dragon.usingGroundAttack = dragon.getRandom().nextBoolean();
                        dragon.randomizeAttacks();
                    }
                }
            }
        }
    }

    public void debug() {
        String side = dragon.level.isClientSide ? "CLIENT" : "SERVER";
        String owner = dragon.getOwner() == null ? "null" : dragon.getOwner().getName().getString();
        String attackTarget = dragon.getTarget() == null ? "null" : dragon.getTarget().getName().getString();
        IceAndFire.LOGGER.warn("DRAGON DEBUG[" + side + "]:"
                + "\nStage: " + dragon.getDragonStage()
                + "\nAge: " + dragon.getAgeInDays()
                + "\nVariant: " + dragon.getVariantName(dragon.getVariant())
                + "\nOwner: " + owner
                + "\nAttack Target: " + attackTarget
                + "\nFlying: " + dragon.isFlying()
                + "\nHovering: " + dragon.isHovering()
                + "\nHovering Time: " + dragon.hoverTicks
                + "\nWidth: " + dragon.getBbWidth()
                + "\nMoveHelper: " + dragon.getMoveControl()
                + "\nGround Attack: " + dragon.groundAttack
                + "\nAir Attack: " + dragon.airAttack
                + "\nTackling: " + dragon.isTackling()

        );
    }

    public void debugPathfinder(Path currentPath) {
        if (IceAndFire.DEBUG) {
            try {
                for (int i = 0; i < currentPath.getNodeCount(); i++) {
                    Node point = currentPath.getNode(i);
                    int particle = 2;
                    IceAndFire.sendMSGToAll(new MessageSpawnParticleAt(point.x, point.y, point.z, particle));
                }
                if (currentPath.getNextNodePos() != null) {
                    Vec3 point = Vec3.atCenterOf(currentPath.getNextNodePos());
                    int particle = 1;
                    IceAndFire.sendMSGToAll(new MessageSpawnParticleAt(point.x, point.y, point.z, particle));

                }
            } catch (Exception e) {
                //Pathfinders are always unfriendly.
            }

        }
    }
}