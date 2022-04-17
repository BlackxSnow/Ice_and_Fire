package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.client.model.util.HideableModelRenderer;
import com.github.alexthe666.iceandfire.entity.EntityDreadThrall;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;

public class ModelDreadThrall extends ModelDragonBase<EntityDreadThrall> implements ArmedModel {

    public HideableModelRenderer bipedHead;
    public HideableModelRenderer bipedHeadwear;
    public HideableModelRenderer bipedBody;
    public HideableModelRenderer bipedRightArm;
    public HideableModelRenderer bipedLeftArm;
    public HideableModelRenderer bipedRightLeg;
    public HideableModelRenderer bipedLeftLeg;
    public HumanoidModel.ArmPose leftArmPose;
    public HumanoidModel.ArmPose rightArmPose;
    public boolean isSneak;
    private ModelAnimator animator;
    private boolean armor = false;

    public ModelDreadThrall(float modelSize, boolean armorArms) {
        this.armor = armorArms;
        this.texHeight = 32;
        this.texWidth = 64;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.bipedBody = new HideableModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
        this.bipedBody.setPos(0.0F, 0.0F, 0.0F);

            /*
                    this.bipedBody.addChild(bipedHead);
        this.bipedHead.addChild(bipedHeadwear);
        this.bipedBody.addChild(bipedRightArm);
        this.bipedBody.addChild(bipedLeftArm);
        this.bipedBody.addChild(bipedRightLeg);
        this.bipedBody.addChild(bipedLeftLeg);
             */
        this.bipedRightArm = new HideableModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
        this.bipedRightArm.setPos(-5.0F, 2.0F, 0.0F);
        this.bipedLeftArm = new HideableModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
        this.bipedLeftArm.setPos(5.0F, 2.0F, 0.0F);
        this.bipedRightLeg = new HideableModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
        this.bipedRightLeg.setPos(-2.0F, 12.0F, 0.0F);
        this.bipedLeftLeg = new HideableModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
        this.bipedLeftLeg.setPos(2.0F, 12.0F, 0.0F);
        this.bipedHead = new HideableModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize - 0.5F);
        this.bipedHead.setPos(0.0F, 0.0F, 0.0F);
        this.bipedHeadwear = new HideableModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
        this.bipedHeadwear.setPos(0.0F, 0.0F, 0.0F);
        if (armorArms) {
            this.bipedHead = new HideableModelRenderer(this, 0, 0);
            this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
            this.bipedHead.setPos(0.0F, 0.0F + 0.0F, 0.0F);
            this.bipedHeadwear = new HideableModelRenderer(this, 32, 0);
            this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
            this.bipedHeadwear.setPos(0.0F, 0.0F + 0.0F, 0.0F);
            this.bipedBody = new HideableModelRenderer(this, 16, 16);
            this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
            this.bipedBody.setPos(0.0F, 0.0F + 0.0F, 0.0F);
            this.bipedRightArm = new HideableModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedRightArm.setPos(-5.0F, 2.0F + 0.0F, 0.0F);
            this.bipedLeftArm = new HideableModelRenderer(this, 40, 16);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedLeftArm.setPos(5.0F, 2.0F + 0.0F, 0.0F);
            this.bipedRightLeg = new HideableModelRenderer(this, 0, 16);
            this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedRightLeg.setPos(-1.9F, 12.0F + 0.0F, 0.0F);
            this.bipedLeftLeg = new HideableModelRenderer(this, 0, 16);
            this.bipedLeftLeg.mirror = true;
            this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
            this.bipedLeftLeg.setPos(1.9F, 12.0F + 0.0F, 0.0F);
        }
        this.bipedBody.addChild(bipedHead);
        this.bipedHead.addChild(bipedHeadwear);
        this.bipedBody.addChild(bipedRightArm);
        this.bipedBody.addChild(bipedLeftArm);
        this.bipedBody.addChild(bipedRightLeg);
        this.bipedBody.addChild(bipedLeftLeg);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    public void prepareMobModel(EntityDreadThrall LivingEntityIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemstack = LivingEntityIn.getItemInHand(InteractionHand.MAIN_HAND);

        super.prepareMobModel(LivingEntityIn, limbSwing, limbSwingAmount, partialTickTime);
    }

    public void setupAnim(EntityDreadThrall entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1);
        ItemStack itemstack = entityIn.getMainHandItem();
        EntityDreadThrall thrall = entityIn;
        if (false) {
            float f = Mth.sin(this.attackTime * (float) Math.PI);
            float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
            this.bipedRightArm.zRot = 0.0F;
            this.bipedLeftArm.zRot = 0.0F;
            this.bipedRightArm.yRot = -(0.1F - f * 0.6F);
            this.bipedLeftArm.yRot = 0.1F - f * 0.6F;
            this.bipedRightArm.xRot = -((float) Math.PI / 2F);
            this.bipedLeftArm.xRot = -((float) Math.PI / 2F);
            this.bipedRightArm.xRot -= f * 1.2F - f1 * 0.4F;
            this.bipedLeftArm.xRot -= f * 1.2F - f1 * 0.4F;
            this.bipedRightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedLeftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.bipedRightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
            this.bipedLeftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
        }
        float f = 1.0F;
        this.bipedRightArm.xRot += Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedLeftArm.xRot += Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.bipedLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
        this.bipedRightLeg.yRot = 0.0F;
        this.bipedLeftLeg.yRot = 0.0F;
        this.bipedRightLeg.zRot = 0.0F;
        this.bipedLeftLeg.zRot = 0.0F;

        if (entityIn.isPassenger()) {
            this.bipedRightArm.xRot += -((float) Math.PI / 5F);
            this.bipedLeftArm.xRot += -((float) Math.PI / 5F);
            this.bipedRightLeg.xRot = -1.4137167F;
            this.bipedRightLeg.yRot = ((float) Math.PI / 10F);
            this.bipedRightLeg.zRot = 0.07853982F;
            this.bipedLeftLeg.xRot = -1.4137167F;
            this.bipedLeftLeg.yRot = -((float) Math.PI / 10F);
            this.bipedLeftLeg.zRot = -0.07853982F;
        }
        if (this.attackTime > 0.0F) {
            HumanoidArm Handside = this.getMainHand(entityIn);
            ModelPart modelrenderer = this.getArmForSide(Handside);
            float f1 = this.attackTime;
            this.bipedBody.yRot = Mth.sin(Mth.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (Handside == HumanoidArm.LEFT) {
                this.bipedBody.yRot *= -1.0F;
            }

            this.bipedRightArm.z = Mth.sin(this.bipedBody.yRot) * 5.0F;
            this.bipedRightArm.x = -Mth.cos(this.bipedBody.yRot) * 5.0F;
            this.bipedLeftArm.z = -Mth.sin(this.bipedBody.yRot) * 5.0F;
            this.bipedLeftArm.x = Mth.cos(this.bipedBody.yRot) * 5.0F;
            this.bipedRightArm.yRot += this.bipedBody.yRot;
            this.bipedLeftArm.yRot += this.bipedBody.yRot;
            this.bipedLeftArm.xRot += this.bipedBody.yRot;
            f1 = 1.0F - this.attackTime;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = Mth.sin(f1 * (float) Math.PI);
            float f3 = Mth.sin(this.attackTime * (float) Math.PI) * -(this.bipedHead.xRot - 0.7F) * 0.75F;
            modelrenderer.xRot = (float) ((double) modelrenderer.xRot - ((double) f2 * 1.2D + (double) f3));
            modelrenderer.yRot += this.bipedBody.yRot * 2.0F;
            modelrenderer.zRot += Mth.sin(this.attackTime * (float) Math.PI) * -0.4F;
        }
        if (this.isSneak) {
            this.bipedBody.xRot = 0.5F;
            this.bipedRightArm.xRot += 0.4F;
            this.bipedLeftArm.xRot += 0.4F;
            this.bipedRightLeg.z = 4.0F;
            this.bipedLeftLeg.z = 4.0F;
            this.bipedRightLeg.y = 9.0F;
            this.bipedLeftLeg.y = 9.0F;
            this.bipedHead.y = 1.0F;
        } else {
            this.bipedBody.xRot = 0.0F;
            this.bipedRightLeg.z = 0.1F;
            this.bipedLeftLeg.z = 0.1F;
            this.bipedRightLeg.y = 12.0F;
            this.bipedLeftLeg.y = 12.0F;
            this.bipedHead.y = 0.0F;
        }

        this.bipedRightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        if (thrall.getAnimation() == EntityDreadThrall.ANIMATION_SPAWN) {
            //this.walk(bipedRightArm, 1.5F, 0.4F, false, 2, -0.3F, thrall.ticksExisted, 1);
            //this.walk(bipedLeftArm, 1.5F,  0.4F, true, 2, 0.3F, thrall.ticksExisted, 1);
            if (thrall.getAnimationTick() < 30) {
                this.flap(bipedRightArm, 0.5F, 0.5F, false, 2, -0.7F, thrall.tickCount, 1);
                this.flap(bipedLeftArm, 0.5F, 0.5F, true, 2, -0.7F, thrall.tickCount, 1);
                this.walk(bipedRightArm, 0.5F, 0.5F, true, 1, 0, thrall.tickCount, 1);
                this.walk(bipedLeftArm, 0.5F, 0.5F, true, 1, 0, thrall.tickCount, 1);
            }
        }
        this.flap(bipedBody, 0.5F, 0.15F, false, 1, 0F, limbSwing, limbSwingAmount);

    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(bipedBody);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        animator.update(entity);
        if (animator.setAnimation(EntityDreadThrall.ANIMATION_SPAWN)) {
            animator.startKeyframe(0);
            animator.move(this.bipedBody, 0, 35, 0);
            rotate(animator, this.bipedLeftArm, -180, 0, 0);
            rotate(animator, this.bipedRightArm, -180, 0, 0);
            animator.endKeyframe();
            animator.startKeyframe(30);
            animator.move(this.bipedBody, 0, 0, 0);
            rotate(animator, this.bipedLeftArm, -180, 0, 0);
            rotate(animator, this.bipedRightArm, -180, 0, 0);
            animator.endKeyframe();
            animator.resetKeyframe(5);
        }
    }

    public void rotate(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
        animator.rotate(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    public void rotateMinus(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
        animator.rotate(model, (float) Math.toRadians(x) - model.defaultRotationX, (float) Math.toRadians(y) - model.defaultRotationY, (float) Math.toRadians(z) - model.defaultRotationZ);
    }

    protected ModelPart getArmForSide(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.bipedLeftArm : this.bipedRightArm;
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
        this.bipedHead.invisible = !visible;
        this.bipedHeadwear.invisible = !visible;
        this.bipedBody.invisible = !visible;
        this.bipedRightArm.invisible = !visible;
        this.bipedLeftArm.invisible = !visible;
        this.bipedRightLeg.invisible = !visible;
        this.bipedLeftLeg.invisible = !visible;
    }

    @Override
    public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
        bipedBody.translateAndRotate(matrixStackIn);
        getArmForSide(sideIn).translateAndRotate(matrixStackIn);
    }

    @Override
    public void renderStatue(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
}