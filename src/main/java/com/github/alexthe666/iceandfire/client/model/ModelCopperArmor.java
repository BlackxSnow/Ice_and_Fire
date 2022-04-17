package com.github.alexthe666.iceandfire.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ModelCopperArmor extends HumanoidModel {

    public ModelPart robeLowerLeft;
    public ModelPart facePlate;
    public ModelPart crest;
    public ModelPart robeLowerRight;

    public ModelCopperArmor(float modelSize) {
        super(modelSize, 0, 64, 64);
        this.texWidth = 64;
        this.texHeight = 64;
        this.robeLowerLeft = new ModelPart(this, 0, 51);
        this.robeLowerLeft.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerLeft.addBox(-1.9F, 0.0F, -2.5F, 4, 8, 5, modelSize);
        this.facePlate = new ModelPart(this, 34, 32);
        this.facePlate.setPos(0.0F, 0.0F, 0.0F);
        this.facePlate.addBox(-4.5F, -8.2F, -4.01F, 9, 10, 1, modelSize);
          this.crest = new ModelPart(this, 23, 31);
        this.crest.setPos(0.0F, -7.6F, 2.6F);
        this.crest.addBox(0.0F, -7.5F, -9.0F, 0, 16, 14, 0.0F);
        this.robeLowerRight = new ModelPart(this, 0, 51);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerRight.addBox(-2.1F, 0.0F, -2.5F, 4, 8, 5, modelSize);
        this.leftLeg.addChild(this.robeLowerLeft);
        this.head.addChild(this.facePlate);
        this.head.addChild(this.crest);
        this.rightLeg.addChild(this.robeLowerRight);
    }

    public void setupAnim(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn instanceof ArmorStand) {
            ArmorStand entityarmorstand = (ArmorStand) entityIn;
            this.head.xRot = 0.017453292F * entityarmorstand.getHeadPose().getX();
            this.head.yRot = 0.017453292F * entityarmorstand.getHeadPose().getY();
            this.head.zRot = 0.017453292F * entityarmorstand.getHeadPose().getZ();
            this.head.setPos(0.0F, 1.0F, 0.0F);
            this.body.xRot = 0.017453292F * entityarmorstand.getBodyPose().getX();
            this.body.yRot = 0.017453292F * entityarmorstand.getBodyPose().getY();
            this.body.zRot = 0.017453292F * entityarmorstand.getBodyPose().getZ();
            this.leftArm.xRot = 0.017453292F * entityarmorstand.getLeftArmPose().getX();
            this.leftArm.yRot = 0.017453292F * entityarmorstand.getLeftArmPose().getY();
            this.leftArm.zRot = 0.017453292F * entityarmorstand.getLeftArmPose().getZ();
            this.rightArm.xRot = 0.017453292F * entityarmorstand.getRightArmPose().getX();
            this.rightArm.yRot = 0.017453292F * entityarmorstand.getRightArmPose().getY();
            this.rightArm.zRot = 0.017453292F * entityarmorstand.getRightArmPose().getZ();
            this.leftLeg.xRot = 0.017453292F * entityarmorstand.getLeftLegPose().getX();
            this.leftLeg.yRot = 0.017453292F * entityarmorstand.getLeftLegPose().getY();
            this.leftLeg.zRot = 0.017453292F * entityarmorstand.getLeftLegPose().getZ();
            this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
            this.rightLeg.xRot = 0.017453292F * entityarmorstand.getRightLegPose().getX();
            this.rightLeg.yRot = 0.017453292F * entityarmorstand.getRightLegPose().getY();
            this.rightLeg.zRot = 0.017453292F * entityarmorstand.getRightLegPose().getZ();
            this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
            this.hat.copyFrom(this.head);
        } else {
            super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        float f = 0;
        float f1 = 12;
        if (crouching) {
            f = -1;
            f1 = 10;
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
