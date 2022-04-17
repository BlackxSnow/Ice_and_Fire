package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.client.model.util.HideableModelRenderer;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;

public class ModelDreadQueen extends ModelDragonBase<EntityDreadQueen> {
    public HideableModelRenderer body;
    public HideableModelRenderer chestplate;
    public HideableModelRenderer head;
    public HideableModelRenderer armRight;
    public HideableModelRenderer legRight;
    public HideableModelRenderer armLeft;
    public HideableModelRenderer legLeft;
    public HideableModelRenderer cloak;
    public HideableModelRenderer necklace;
    public HideableModelRenderer crown;
    public HideableModelRenderer sleeveRight;
    public HideableModelRenderer robeLowerRight;
    public HideableModelRenderer sleeveLeft;
    public HideableModelRenderer robeLowerLeft;
    public HumanoidModel.ArmPose leftArmPose;
    public HumanoidModel.ArmPose rightArmPose;
    public boolean isSneak;
    private ModelAnimator animator;
    private boolean armor = false;

    public ModelDreadQueen(float modelSize, boolean armorArms) {
        this.texWidth = 128;
        this.texHeight = 64;
        this.armor = armorArms;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.body = new HideableModelRenderer(this, 16, 16);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.crown = new HideableModelRenderer(this, 58, 0);
        this.crown.setPos(0.0F, 1.0F, 0.0F);
        this.crown.addBox(-4.5F, -12.5F, -4.5F, 9, 13, 9, 0.0F);
        this.legRight = new HideableModelRenderer(this, 0, 16);
        this.legRight.setPos(-1.9F, 12.0F, 0.1F);
        this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.sleeveLeft = new HideableModelRenderer(this, 36, 33);
        this.sleeveLeft.mirror = true;
        this.sleeveLeft.setPos(-0.5F, -0.1F, 0.0F);
        this.sleeveLeft.addBox(-0.5F, -2.1F, -2.4F, 5, 6, 5, 0.0F);
        this.robeLowerLeft = new HideableModelRenderer(this, 58, 33);
        this.robeLowerLeft.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerLeft.addBox(-1.9F, 0.0F, -2.5F, 4, 7, 5, 0.0F);
        this.chestplate = new HideableModelRenderer(this, 1, 32);
        this.chestplate.setPos(0.0F, 0.1F, 0.0F);
        this.chestplate.addBox(-4.5F, -0.3F, -2.5F, 9, 12, 5, 0.0F);
        this.robeLowerRight = new HideableModelRenderer(this, 58, 33);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerRight.addBox(-2.1F, 0.0F, -2.5F, 4, 7, 5, 0.0F);
        this.sleeveRight = new HideableModelRenderer(this, 36, 33);
        this.sleeveRight.setPos(0.5F, -0.1F, 0.0F);
        this.sleeveRight.addBox(-4.5F, -2.1F, -2.4F, 5, 6, 5, 0.0F);
        this.cloak = new HideableModelRenderer(this, 81, 37);
        this.cloak.setPos(0.0F, 0.1F, 0.0F);
        this.cloak.addBox(-4.5F, 0.0F, 2.3F, 9, 21, 1, 0.0F);
        this.setRotateAngle(cloak, 0.045553093477052F, 0.0F, 0.0F);
        this.armRight = new HideableModelRenderer(this, 40, 16);
        this.armRight.setPos(-5.0F, 2.0F, 0.0F);
        this.armRight.addBox(-2.5F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(armRight, 0.0F, 0.0F, 0.10000736613927509F);
        this.necklace = new HideableModelRenderer(this, 1, 52);
        this.necklace.setPos(0.0F, 0.1F, 0.0F);
        this.necklace.addBox(-4.5F, -0.3F, -2.8F, 9, 7, 5, 0.0F);
        this.setRotateAngle(necklace, 0.032114058236695664F, 0.0F, 0.0F);
        this.armLeft = new HideableModelRenderer(this, 40, 16);
        this.armLeft.mirror = true;
        this.armLeft.setPos(5.0F, 2.0F, -0.0F);
        this.armLeft.addBox(-0.5F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(armLeft, -0.0F, 0.0F, -0.10000736613927509F);
        this.head = new HideableModelRenderer(this, 0, 0);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.legLeft = new HideableModelRenderer(this, 0, 16);
        this.legLeft.mirror = true;
        this.legLeft.setPos(1.9F, 12.0F, 0.1F);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.head.addChild(this.crown);
        this.body.addChild(this.legRight);
        this.armLeft.addChild(this.sleeveLeft);
        this.legLeft.addChild(this.robeLowerLeft);
        this.body.addChild(this.chestplate);
        this.legRight.addChild(this.robeLowerRight);
        this.armRight.addChild(this.sleeveRight);
        this.body.addChild(this.cloak);
        this.body.addChild(this.armRight);
        this.chestplate.addChild(this.necklace);
        this.body.addChild(this.armLeft);
        this.body.addChild(this.head);
        this.body.addChild(this.legLeft);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void prepareMobModel(EntityDreadQueen LivingEntityIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemstack = LivingEntityIn.getItemInHand(InteractionHand.MAIN_HAND);
        super.prepareMobModel(LivingEntityIn, limbSwing, limbSwingAmount, partialTickTime);
    }

    public void setupAnim(EntityDreadQueen entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1);
        ItemStack itemstack = entityIn.getMainHandItem();
        this.faceTarget(netHeadYaw, headPitch, 1.0F, head);
        float f = 1.0F;
        this.armRight.xRot += Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.armLeft.xRot += Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.legRight.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.legLeft.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
        this.legRight.yRot = 0.0F;
        this.legLeft.yRot = 0.0F;
        this.legRight.zRot = 0.0F;
        this.legLeft.zRot = 0.0F;

        if (entityIn.isPassenger()) {
            this.armRight.xRot += -((float) Math.PI / 5F);
            this.armLeft.xRot += -((float) Math.PI / 5F);
            this.legRight.xRot = -1.4137167F;
            this.legRight.yRot = ((float) Math.PI / 10F);
            this.legRight.zRot = 0.07853982F;
            this.legLeft.xRot = -1.4137167F;
            this.legLeft.yRot = -((float) Math.PI / 10F);
            this.legLeft.zRot = -0.07853982F;
        }
        if (this.attackTime > 0.0F) {
            HumanoidArm Handside = this.getMainHand(entityIn);
            ModelPart modelrenderer = this.getArmForSide(Handside);
            float f1 = this.attackTime;
            this.body.yRot = Mth.sin(Mth.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (Handside == HumanoidArm.LEFT) {
                this.body.yRot *= -1.0F;
            }

            this.armRight.z = Mth.sin(this.body.yRot) * 5.0F;
            this.armRight.x = -Mth.cos(this.body.yRot) * 5.0F;
            this.armLeft.z = -Mth.sin(this.body.yRot) * 5.0F;
            this.armLeft.x = Mth.cos(this.body.yRot) * 5.0F;
            this.armRight.yRot += this.body.yRot;
            this.armLeft.yRot += this.body.yRot;
            this.armLeft.xRot += this.body.yRot;
            f1 = 1.0F - this.attackTime;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = Mth.sin(f1 * (float) Math.PI);
            float f3 = Mth.sin(this.attackTime * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
            modelrenderer.xRot = (float) ((double) modelrenderer.xRot - ((double) f2 * 1.2D + (double) f3));
            modelrenderer.yRot += this.body.yRot * 2.0F;
            modelrenderer.zRot += Mth.sin(this.attackTime * (float) Math.PI) * -0.4F;
        }
        if (this.isSneak) {
            this.body.xRot = 0.5F;
            this.armRight.xRot += 0.4F;
            this.armLeft.xRot += 0.4F;
            this.legRight.z = 4.0F;
            this.legLeft.z = 4.0F;
            this.legRight.y = 9.0F;
            this.legLeft.y = 9.0F;
            this.head.y = 1.0F;
        } else {
            this.body.xRot = 0.0F;
            this.legRight.z = 0.1F;
            this.legLeft.z = 0.1F;
            this.legRight.y = 12.0F;
            this.legLeft.y = 12.0F;
            this.head.y = 0.0F;
        }

        this.armRight.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armLeft.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armRight.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.armLeft.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        if (entityIn.getAnimation() == EntityDreadQueen.ANIMATION_SPAWN) {
            //this.walk(armRight, 1.5F, 0.4F, false, 2, -0.3F, thrall.ticksExisted, 1);
            //this.walk(armLeft, 1.5F,  0.4F, true, 2, 0.3F, thrall.ticksExisted, 1);
            if (entityIn.getAnimationTick() < 30) {
                this.flap(armRight, 0.5F, 0.5F, false, 2, -0.7F, entityIn.tickCount, 1);
                this.flap(armLeft, 0.5F, 0.5F, true, 2, -0.7F, entityIn.tickCount, 1);
                this.walk(armRight, 0.5F, 0.5F, true, 1, 0, entityIn.tickCount, 1);
                this.walk(armLeft, 0.5F, 0.5F, true, 1, 0, entityIn.tickCount, 1);
            }
        }

    }


    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        animator.update(entity);
        if (animator.setAnimation(EntityDreadQueen.ANIMATION_SPAWN)) {
            animator.startKeyframe(0);
            animator.move(this.body, 0, 35, 0);
            rotate(animator, this.armLeft, -180, 0, 0);
            rotate(animator, this.armRight, -180, 0, 0);
            animator.endKeyframe();
            animator.startKeyframe(30);
            animator.move(this.body, 0, 0, 0);
            rotate(animator, this.armLeft, -180, 0, 0);
            rotate(animator, this.armRight, -180, 0, 0);
            animator.endKeyframe();
            animator.resetKeyframe(5);
        }
    }

    protected ModelPart getArmForSide(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.armLeft : this.armRight;
    }

    protected HumanoidArm getMainHand(Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            LivingEntity LivingEntity = (LivingEntity) entityIn;
            HumanoidArm Handside = LivingEntity.getMainArm();
            return LivingEntity.swingingArm == InteractionHand.MAIN_HAND ? Handside : Handside.getOpposite();
        } else {
            return HumanoidArm.RIGHT;
        }
    }

    public void setVisible(boolean visible) {
        this.head.invisible = !visible;
        this.body.invisible = !visible;
        this.armRight.invisible = !visible;
        this.armLeft.invisible = !visible;
        this.legRight.invisible = !visible;
        this.legLeft.invisible = !visible;
    }

    @Override
    public void renderStatue(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }
}
