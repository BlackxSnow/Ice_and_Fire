package com.github.alexthe666.iceandfire.client.model;


import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ModelCyclops extends ModelDragonBase<EntityCyclops> {
    public AdvancedModelBox body;
    public AdvancedModelBox UpperBody;
    public AdvancedModelBox Loin;
    public AdvancedModelBox rightleg;
    public AdvancedModelBox leftleg;
    public AdvancedModelBox Head;
    public AdvancedModelBox rightarm;
    public AdvancedModelBox leftarm;
    public AdvancedModelBox Belly;
    public AdvancedModelBox Chest;
    public AdvancedModelBox Eye;
    public AdvancedModelBox Horn;
    public AdvancedModelBox rightear;
    public AdvancedModelBox Leftear;
    public AdvancedModelBox Jaw;
    public AdvancedModelBox topTeethL;
    public AdvancedModelBox topTeethR;
    public AdvancedModelBox Eye_1;
    public AdvancedModelBox Horn2;
    public AdvancedModelBox bottomTeethR;
    public AdvancedModelBox bottomTeethL;
    public AdvancedModelBox rightarm2;
    public AdvancedModelBox leftarm2;
    public AdvancedModelBox LoinBack;
    public AdvancedModelBox rightleg2;
    public AdvancedModelBox leftleg2;
    private ModelAnimator animator;

    public ModelCyclops() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.rightear = new AdvancedModelBox(this, 0, 7);
        this.rightear.setPos(-4.5F, -2.7F, -1.1F);
        this.rightear.addBox(-1.0F, -2.7F, -1.3F, 1, 4, 2, 0.0F);
        this.setRotateAngle(rightear, -0.06981317007977318F, -0.5235987755982988F, 0.0F);
        this.Eye_1 = new AdvancedModelBox(this, 8, 6);
        this.Eye_1.setPos(0.0F, 0.0F, 0.0F);
        this.Eye_1.addBox(-1.5F, -1.5F, -4.6F, 3, 3, 1, 0.0F);
        this.Horn2 = new AdvancedModelBox(this, 17, 0);
        this.Horn2.setPos(0.0F, -5.0F, 0.5F);
        this.Horn2.addBox(-1.0F, -2.9F, -3.3F, 2, 3, 2, 0.0F);
        this.UpperBody = new AdvancedModelBox(this, 85, 21);
        this.UpperBody.setPos(0.0F, -6.0F, 0.5F);
        this.UpperBody.addBox(-6.0F, -13.9F, -4.1F, 12, 14, 9, 0.0F);
        this.topTeethL = new AdvancedModelBox(this, 90, 70);
        this.topTeethL.mirror = true;
        this.topTeethL.setPos(0.0F, 2.3F, 0.3F);
        this.topTeethL.addBox(-0.6F, -0.5F, -6.2F, 5, 1, 6, 0.0F);
        this.body = new AdvancedModelBox(this, 88, 46);
        this.body.setPos(0.0F, -3.5F, 0.0F);
        this.body.addBox(-5.0F, -7.0F, -3.0F, 10, 9, 8, 0.0F);
        this.Belly = new AdvancedModelBox(this, 35, 25);
        this.Belly.mirror = true;
        this.Belly.setPos(0.0F, -2.3F, -0.2F);
        this.Belly.addBox(-4.5F, -2.0F, -4.8F, 9, 13, 3, 0.0F);
        this.setRotateAngle(Belly, 0.045553093477052F, 0.0F, 0.0F);
        this.Loin = new AdvancedModelBox(this, 52, 49);
        this.Loin.setPos(0.0F, 0.0F, 0.0F);
        this.Loin.addBox(-5.5F, 0.0F, -4.1F, 11, 16, 5, 0.0F);
        this.leftleg2 = new AdvancedModelBox(this, 0, 15);
        this.leftleg2.mirror = true;
        this.leftleg2.setPos(0.0F, 10.0F, 0.2F);
        this.leftleg2.addBox(-3.0F, 1.0F, -3.0F, 6, 15, 6, 0.0F);
        this.setRotateAngle(leftleg2, 0.0F, 0.0F, 0.017453292519943295F);
        this.bottomTeethL = new AdvancedModelBox(this, 90, 70);
        this.bottomTeethL.mirror = true;
        this.bottomTeethL.setPos(0.0F, 2.3F, 0.3F);
        this.bottomTeethL.addBox(-0.2F, -3.7F, -6.6F, 5, 1, 6, 0.0F);
        this.LoinBack = new AdvancedModelBox(this, 49, 45);
        this.LoinBack.setPos(0.0F, 0.0F, 0.0F);
        this.LoinBack.addBox(-5.5F, 0.0F, -5.6F, 11, 16, 8, 0.0F);
        this.setRotateAngle(LoinBack, 0.0F, -3.141592653589793F, 0.0F);
        this.Leftear = new AdvancedModelBox(this, 0, 7);
        this.Leftear.mirror = true;
        this.Leftear.setPos(4.5F, -2.7F, -1.1F);
        this.Leftear.addBox(0.0F, -2.7F, -1.3F, 1, 4, 2, 0.0F);
        this.setRotateAngle(Leftear, -0.06981317007977318F, 0.5235987755982988F, 0.0F);
        this.Chest = new AdvancedModelBox(this, 93, 30);
        this.Chest.mirror = true;
        this.Chest.setPos(0.0F, -9.7F, -0.1F);
        this.Chest.addBox(-5.0F, -2.0F, -4.8F, 10, 5, 2, 0.0F);
        this.leftarm = new AdvancedModelBox(this, 64, 0);
        this.leftarm.mirror = true;
        this.leftarm.setPos(5.0F, -11.2F, -0.4F);
        this.leftarm.addBox(0.0F, -2.0F, -2.0F, 6, 13, 6, 0.0F);
        this.setRotateAngle(leftarm, 0.045553093477052F, 0.0F, -0.17453292519943295F);
        this.Horn = new AdvancedModelBox(this, 29, 0);
        this.Horn.setPos(0.0F, -5.7F, -1.2F);
        this.Horn.addBox(-1.5F, -5.6F, -2.8F, 3, 3, 3, 0.0F);
        this.setRotateAngle(Horn, 0.4553564018453205F, 0.0F, 0.0F);
        this.Jaw = new AdvancedModelBox(this, 90, 80);
        this.Jaw.setPos(0.0F, 2.3F, 0.3F);
        this.Jaw.addBox(-5.0F, -0.5F, -6.6F, 10, 2, 9, 0.0F);
        this.setRotateAngle(Jaw, 0.091106186954104F, 0.0F, 0.0F);
        this.Eye = new AdvancedModelBox(this, 0, 0);
        this.Eye.setPos(0.0F, -5.1F, -2.3F);
        this.Eye.addBox(-2.5F, -2.0F, -4.4F, 5, 4, 1, 0.0F);
        this.setRotateAngle(Eye, 0.091106186954104F, 0.0F, 0.0F);
        this.rightarm2 = new AdvancedModelBox(this, 60, 22);
        this.rightarm2.setPos(-3.1F, 10.0F, 0.1F);
        this.rightarm2.addBox(-3.0F, -2.0F, -1.7F, 5, 15, 5, 0.0F);
        this.setRotateAngle(rightarm2, -0.08726646259971647F, 0.0F, 0.0F);
        this.leftleg = new AdvancedModelBox(this, 0, 45);
        this.leftleg.mirror = true;
        this.leftleg.setPos(4.0F, 1.2F, 1.0F);
        this.leftleg.addBox(-3.0F, 1.0F, -3.0F, 6, 13, 6, 0.0F);
        this.setRotateAngle(leftleg, 0.0F, 0.0F, -0.017453292519943295F);
        this.Head = new AdvancedModelBox(this, 90, 0);
        this.Head.setPos(0.0F, -16.1F, 0.6F);
        this.Head.addBox(-4.5F, -8.0F, -6.0F, 9, 10, 9, 0.0F);
        this.bottomTeethR = new AdvancedModelBox(this, 90, 70);
        this.bottomTeethR.setPos(0.0F, 2.3F, 0.3F);
        this.bottomTeethR.addBox(-4.6F, -3.7F, -6.6F, 5, 1, 6, 0.0F);
        this.topTeethR = new AdvancedModelBox(this, 90, 70);
        this.topTeethR.setPos(0.0F, 2.3F, 0.3F);
        this.topTeethR.addBox(-4.3F, -0.5F, -6.2F, 5, 1, 6, 0.0F);
        this.rightleg2 = new AdvancedModelBox(this, 0, 15);
        this.rightleg2.setPos(0.0F, 10.0F, 0.2F);
        this.rightleg2.addBox(-3.0F, 1.0F, -3.0F, 6, 15, 6, 0.0F);
        this.setRotateAngle(rightleg2, 0.0F, 0.0F, -0.017453292519943295F);
        this.leftarm2 = new AdvancedModelBox(this, 60, 22);
        this.leftarm2.mirror = true;
        this.leftarm2.setPos(3.1F, 10.0F, -0.1F);
        this.leftarm2.addBox(-2.0F, -2.0F, -1.7F, 5, 15, 5, 0.0F);
        this.setRotateAngle(leftarm2, -0.08726646259971647F, 0.0F, 0.0F);
        this.rightleg = new AdvancedModelBox(this, 0, 45);
        this.rightleg.setPos(-4.0F, 1.2F, 1.0F);
        this.rightleg.addBox(-3.0F, 1.0F, -3.0F, 6, 13, 6, 0.0F);
        this.setRotateAngle(rightleg, 0.0F, 0.0F, 0.017453292519943295F);
        this.rightarm = new AdvancedModelBox(this, 64, 0);
        this.rightarm.setPos(-5.0F, -11.2F, -0.4F);
        this.rightarm.addBox(-6.0F, -2.0F, -2.0F, 6, 13, 6, 0.0F);
        this.setRotateAngle(rightarm, 0.0F, 0.0F, 0.17453292519943295F);
        this.Head.addChild(this.rightear);
        this.Eye.addChild(this.Eye_1);
        this.Horn.addChild(this.Horn2);
        this.body.addChild(this.UpperBody);
        this.Head.addChild(this.topTeethL);
        this.UpperBody.addChild(this.Belly);
        this.body.addChild(this.Loin);
        this.leftleg.addChild(this.leftleg2);
        this.Jaw.addChild(this.bottomTeethL);
        this.Loin.addChild(this.LoinBack);
        this.Head.addChild(this.Leftear);
        this.UpperBody.addChild(this.Chest);
        this.UpperBody.addChild(this.leftarm);
        this.Head.addChild(this.Horn);
        this.Head.addChild(this.Jaw);
        this.Head.addChild(this.Eye);
        this.rightarm.addChild(this.rightarm2);
        this.body.addChild(this.leftleg);
        this.UpperBody.addChild(this.Head);
        this.Jaw.addChild(this.bottomTeethR);
        this.Head.addChild(this.topTeethR);
        this.rightleg.addChild(this.rightleg2);
        this.leftarm.addChild(this.leftarm2);
        this.body.addChild(this.rightleg);
        this.UpperBody.addChild(this.rightarm);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, UpperBody, Loin, rightleg, leftleg, Head, rightarm, leftarm, Belly, Chest, Eye, Horn, rightear,
                Leftear, Jaw, topTeethL, topTeethR, Eye_1, Horn2, bottomTeethR, bottomTeethL, rightarm2, leftarm2, LoinBack, rightleg2, leftleg2);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        if (animator.setAnimation(EntityCyclops.ANIMATION_STOMP)) {
            animator.startKeyframe(7);
            this.rotate(animator, rightleg, -62, 0, 0);
            this.rotate(animator, rightleg2, 46, 0, 0);
            animator.move(rightleg2, 0, 1.1F, -1.7F);
            this.rotate(animator, leftleg, 10, 0, 0);
            this.rotate(animator, leftleg2, 10, 0, 0);
            animator.move(leftleg2, 0, 0, -0.3F);
            this.rotate(animator, body, -15, 0, 0);
            this.rotate(animator, UpperBody, 7, 0, 0);
            animator.endKeyframe();
            animator.setStaticKeyframe(5);
            animator.startKeyframe(5);
            this.rotate(animator, body, 5, 0, 0);
            this.rotate(animator, UpperBody, 7.5F, 0, 0);
            this.rotate(animator, rightleg, -46, 0, 0);
            this.rotate(animator, rightleg2, 36, 0, 0);
            this.rotate(animator, leftleg, 7, 0, 0);
            this.rotate(animator, leftleg2, 2, 0, 0);
            animator.move(rightleg2, 0, 1.1F, -0.9F);
            animator.move(body, 0, 1, 0);
            animator.endKeyframe();
            animator.resetKeyframe(10);
        }
        if (animator.setAnimation(EntityCyclops.ANIMATION_KICK)) {
            animator.startKeyframe(10);
            this.rotate(animator, body, 3, 0, 0);
            this.rotate(animator, leftleg, 13, 0, 0);
            this.rotate(animator, leftleg2, 18, 0, 0);
            animator.move(leftleg2, 0, 1, -0.9F);
            this.rotate(animator, rightleg, -26, 0, 0);
            this.rotate(animator, rightleg2, 23, 0, 0);
            animator.move(leftleg2, 0, 1.2F, -1.1F);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, body, -2, 0, 0);
            this.rotate(animator, rightleg, -55, 0, 0);
            this.rotate(animator, rightleg2, 18, 0, 0);
            animator.move(leftleg2, 0, 2, -0.5F);
            this.rotate(animator, leftleg, 10, 0, 0);
            animator.endKeyframe();
            animator.resetKeyframe(5);
        }
        if (animator.setAnimation(EntityCyclops.ANIMATION_EATPLAYER)) {
            animator.startKeyframe(10);
            animator.move(body, 0, 7, 0);
            this.rotate(animator, body, 25, 0, 0);
            this.rotate(animator, leftleg, -7, 0, 0);
            this.rotate(animator, rightleg, -85, 0, 0);
            this.rotate(animator, rightleg2, 50, 0, 0);
            this.rotate(animator, leftleg2, 52, 0, 0);
            animator.move(leftleg2, 0, 1.1F, -2);
            animator.move(rightleg2, 0, 1, -2);
            this.rotate(animator, UpperBody, 40, 0, 0);
            animator.move(UpperBody, 0, 1.7F, 0);
            this.rotate(animator, rightarm, -80, 0, 0);
            this.rotate(animator, leftarm, -80, 0, 0);
            this.rotate(animator, rightarm2, 0, 0, -23);
            this.rotate(animator, leftarm2, 0, 0, 23);
            animator.endKeyframe();
            animator.startKeyframe(15);
            this.rotate(animator, rightarm, -40, -25, 40);
            this.rotate(animator, rightarm2, -120, 0, 0);
            this.rotate(animator, leftarm, -40, 25, -40);
            this.rotate(animator, leftarm2, -120, 0, 0);
            animator.move(rightarm2, 0, 1.2F, 1.4F);
            animator.move(leftarm2, 0, 1.2F, 1.4F);
            this.rotate(animator, Head, -25, 0, 0);
            animator.move(Head, 0, -0.5F, 0);
            this.rotate(animator, Jaw, 5, 0, 0);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, rightarm, -40, -25, 40);
            this.rotate(animator, rightarm2, -120, 0, 0);
            this.rotate(animator, leftarm, -40, 25, -40);
            this.rotate(animator, leftarm2, -120, 0, 0);
            animator.move(rightarm2, 0, 1.2F, 1.4F);
            animator.move(leftarm2, 0, 1.2F, 1.4F);
            this.rotate(animator, Head, -48, 0, 0);
            animator.move(Head, 0, -0.8F, 0);
            this.rotate(animator, Jaw, 57, 0, 0);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, rightarm, -40, -25, 40);
            this.rotate(animator, rightarm2, -120, 0, 0);
            this.rotate(animator, leftarm, -40, 25, -40);
            this.rotate(animator, leftarm2, -120, 0, 0);
            animator.move(rightarm2, 0, 1.2F, 1.4F);
            animator.move(leftarm2, 0, 1.2F, 1.4F);
            this.rotate(animator, Head, -25, 0, 0);
            animator.move(Head, 0, -0.5F, 0);
            this.rotate(animator, Jaw, 5, 0, 0);
            animator.endKeyframe();
            animator.resetKeyframe(5);
        }
        if (animator.setAnimation(EntityCyclops.ANIMATION_ROAR)) {
            animator.startKeyframe(5);
            this.rotate(animator, body, 15, 0, 0);
            this.rotate(animator, leftleg, -20, -15, -15);
            this.rotate(animator, leftleg2, 15, 0, 15);
            this.rotate(animator, rightleg, -20, 15, 15);
            this.rotate(animator, rightleg2, 15, 0, -15);
            animator.move(leftleg2, -0.4F, 1.5F, -0.5F);
            animator.move(rightleg2, 0.4F, 1.5F, -0.5F);
            this.rotate(animator, UpperBody, 10, 0, 0);
            this.rotate(animator, Head, -25, 0, 0);
            this.rotate(animator, rightarm, -25, 35, 25);
            this.rotate(animator, leftarm, -25, -35, -25);
            this.rotate(animator, rightarm2, -28, 0, 0);
            this.rotate(animator, leftarm2, -28, 0, 0);
            animator.move(Head, 0, 0, -0.5F);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, body, 15, 0, 0);
            this.rotate(animator, leftleg, -20, -15, -15);
            this.rotate(animator, leftleg2, 15, 0, 15);
            this.rotate(animator, rightleg, -20, 15, 15);
            this.rotate(animator, rightleg2, 15, 0, -15);
            animator.move(leftleg2, -0.4F, 1.5F, -0.5F);
            animator.move(rightleg2, 0.4F, 1.5F, -0.5F);
            this.rotate(animator, UpperBody, 10, 0, 0);
            this.rotate(animator, rightarm, -25, 35, 25);
            this.rotate(animator, leftarm, -25, -35, -25);
            this.rotate(animator, rightarm2, -28, 0, 0);
            this.rotate(animator, leftarm2, -28, 0, 0);
            this.rotate(animator, Head, -45, 20, 0);
            this.rotate(animator, Jaw, 45, 0, 0);
            animator.move(Head, 0, 0, -0.5F);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, body, 15, 0, 0);
            this.rotate(animator, leftleg, -20, -15, -15);
            this.rotate(animator, leftleg2, 15, 0, 15);
            this.rotate(animator, rightleg, -20, 15, 15);
            this.rotate(animator, rightleg2, 15, 0, -15);
            animator.move(leftleg2, -0.4F, 1.5F, -0.5F);
            animator.move(rightleg2, 0.4F, 1.5F, -0.5F);
            this.rotate(animator, UpperBody, 10, 0, 0);
            this.rotate(animator, rightarm, -25, 35, 25);
            this.rotate(animator, leftarm, -25, -35, -25);
            this.rotate(animator, rightarm2, -28, 0, 0);
            this.rotate(animator, leftarm2, -28, 0, 0);
            this.rotate(animator, Head, -45, -20, 0);
            this.rotate(animator, Jaw, 45, 0, 0);
            animator.move(Head, 0, 0, -0.5F);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, body, 15, 0, 0);
            this.rotate(animator, leftleg, -20, -15, -15);
            this.rotate(animator, leftleg2, 15, 0, 15);
            this.rotate(animator, rightleg, -20, 15, 15);
            this.rotate(animator, rightleg2, 15, 0, -15);
            animator.move(leftleg2, -0.4F, 1.5F, -0.5F);
            animator.move(rightleg2, 0.4F, 1.5F, -0.5F);
            this.rotate(animator, UpperBody, 10, 0, 0);
            this.rotate(animator, rightarm, -25, 35, 25);
            this.rotate(animator, leftarm, -25, -35, -25);
            this.rotate(animator, rightarm2, -28, 0, 0);
            this.rotate(animator, leftarm2, -28, 0, 0);
            this.rotate(animator, Head, -45, 20, 0);
            this.rotate(animator, Jaw, 45, 0, 0);
            animator.move(Head, 0, 0, -0.5F);
            animator.endKeyframe();
            animator.startKeyframe(5);
            this.rotate(animator, body, 15, 0, 0);
            this.rotate(animator, leftleg, -20, -15, -15);
            this.rotate(animator, leftleg2, 15, 0, 15);
            this.rotate(animator, rightleg, -20, 15, 15);
            this.rotate(animator, rightleg2, 15, 0, -15);
            animator.move(leftleg2, -0.4F, 1.5F, -0.5F);
            animator.move(rightleg2, 0.4F, 1.5F, -0.5F);
            this.rotate(animator, UpperBody, 10, 0, 0);
            this.rotate(animator, rightarm, -25, 35, 25);
            this.rotate(animator, leftarm, -25, -35, -25);
            this.rotate(animator, rightarm2, -28, 0, 0);
            this.rotate(animator, leftarm2, -28, 0, 0);
            this.rotate(animator, Head, -45, -20, 0);
            this.rotate(animator, Jaw, 45, 0, 0);
            animator.move(Head, 0, 0, -0.5F);
            animator.endKeyframe();
            animator.resetKeyframe(5);
            this.Loin.xRot = Math.min(0, Math.min(this.leftleg.xRot, this.rightleg.xRot));
            this.LoinBack.xRot = this.Loin.xRot - Math.max(this.leftleg.xRot, this.rightleg.xRot);
        }
    }

    public void setupAnim(EntityCyclops entity, float f, float f1, float f2, float f3, float f4) {
        animate(entity, f, f1, f2, f3, f4);
        float speed_walk = 0.2F;
        float speed_idle = 0.05F;
        float degree_walk = 0.75F;
        float degree_idle = 0.5F;
        this.walk(this.rightleg, speed_walk, degree_walk * -0.75F, true, 0, 0F, f, f1);
        this.walk(this.leftleg, speed_walk, degree_walk * -0.75F, false, 0, 0F, f, f1);
        this.walk(this.rightleg2, speed_walk, degree_walk * -0.5F, true, 1, -0.3F, f, f1);
        this.walk(this.leftleg2, speed_walk, degree_walk * -0.5F, false, 1, 0.3F, f, f1);
        this.walk(this.rightarm, speed_walk, degree_walk * -0.75F, false, 0, 0F, f, f1);
        this.walk(this.leftarm, speed_walk, degree_walk * -0.75F, true, 0, 0F, f, f1);
        this.walk(this.rightarm2, speed_walk, degree_walk * -0.5F, false, 1, -0.3F, f, f1);
        this.walk(this.leftarm2, speed_walk, degree_walk * -0.5F, true, 1, 0.3F, f, f1);
        this.swing(this.body, speed_walk, degree_walk * -0.5F, false, 0, 0F, f, f1);
        this.swing(this.UpperBody, speed_walk, degree_walk * -0.25F, true, 0, 0F, f, f1);
        this.swing(this.Belly, speed_walk, degree_walk * -0.25F, false, 0, 0F, f, f1);
        this.walk(this.UpperBody, speed_idle, degree_idle * -0.1F, true, 0F, -0.1F, f2, 1);
        this.flap(this.leftarm, speed_idle, degree_idle * -0.1F, true, 0, 0F, f2, 1);
        this.flap(this.rightarm, speed_idle, degree_idle * -0.1F, false, 0, 0F, f2, 1);
        this.flap(this.leftarm2, speed_idle, degree_idle * -0.1F, true, 0, -0.1F, f2, 1);
        this.flap(this.rightarm2, speed_idle, degree_idle * -0.1F, false, 0, -0.1F, f2, 1);
        if (entity.getAnimation() != EntityCyclops.ANIMATION_EATPLAYER) {
            this.faceTarget(f3, f4, 1, this.Head);
        }
        this.walk(this.Jaw, speed_idle, degree_idle * -0.15F, true, 0F, -0.1F, f2, 1);

        if (entity != null) {
            Vec3 Vector3d = entity.getEyePosition(0.0F);
            Vec3 Vector3d1 = entity.getEyePosition(0.0F);
            double d0 = Vector3d.y - Vector3d1.y;

            if (d0 > 0.0D) {
                this.Eye.y = -4.1F;
            } else {
                this.Eye.y = -5.1F;
            }

            Vec3 Vector3d2 = entity.getViewVector(0.0F);
            Vector3d2 = new Vec3(Vector3d2.x, 0.0D, Vector3d2.z);
            Vec3 Vector3d3 = (new Vec3(Vector3d1.x - Vector3d.x, 0.0D, Vector3d1.z - Vector3d.z)).normalize().yRot(((float) Math.PI / 2F));
            double d1 = Vector3d2.dot(Vector3d3);
            this.Eye.x = Mth.sqrt((float) Math.abs(d1)) * 2.0F * (float) Math.signum(d1);
        }
    }

    @Override
    public void renderStatue(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
