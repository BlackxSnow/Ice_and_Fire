package com.github.alexthe666.iceandfire.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ModelDragonsteelFireArmor extends HumanoidModel {
    public ModelPart visor1;
    public ModelPart HornR;
    public ModelPart HornR4;
    public ModelPart HornL;
    public ModelPart HornL4;
    public ModelPart visor2;
    public ModelPart HornR2;
    public ModelPart HornR3;
    public ModelPart HornR5;
    public ModelPart HornL2;
    public ModelPart HornL3;
    public ModelPart HornL5;
    public ModelPart sleeveRight;
    public ModelPart robeLowerLeft;
    public ModelPart sleeveLeft;
    public ModelPart robeLowerRight;

    public ModelDragonsteelFireArmor(float modelSize) {
        super(modelSize, 0, 64, 64);
        this.texWidth = 64;
        this.texHeight = 64;
        this.HornL4 = new ModelPart(this, 9, 38);
        this.HornL4.setPos(3.2F, -7.4F, -3.0F);
        this.HornL4.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5, modelSize);
        this.setRotateAngle(HornL4, -0.14713125594312196F, 0.296705972839036F, 0.0F);
        this.HornL5 = new ModelPart(this, 25, 45);
        this.HornL5.setPos(0.0F, -0.1F, 4.3F);
        this.HornL5.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 3, modelSize);
        this.setRotateAngle(HornL5, 0.3649483465920143F, 0.0F, 0.0F);
        this.HornL = new ModelPart(this, 9, 39);
        this.HornL.setPos(2.5F, -7.9F, -4.2F);
        this.HornL.addBox(-1.0F, -0.5F, 0.0F, 2, 2, 4, modelSize);
        this.setRotateAngle(HornL, 0.43022366061660217F, 0.15707963267948966F, 0.0F);
        this.HornL3 = new ModelPart(this, 24, 44);
        this.HornL3.setPos(0.0F, -0.1F, 4.3F);
        this.HornL3.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 4, modelSize);
        this.setRotateAngle(HornL3, 0.5918411493512771F, 0.0F, 0.0F);
        this.visor2 = new ModelPart(this, 27, 50);
        this.visor2.mirror = true;
        this.visor2.setPos(-0.1F, 9.0F, 0.2F);
        this.visor2.addBox(0.8F, -13.3F, -4.9F, 4, 5, 8, modelSize);
        this.HornR3 = new ModelPart(this, 24, 44);
        this.HornR3.mirror = true;
        this.HornR3.setPos(0.0F, -0.1F, 4.3F);
        this.HornR3.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 4, modelSize);
        this.setRotateAngle(HornR3, 0.5918411493512771F, 0.0F, 0.0F);
        this.HornL2 = new ModelPart(this, 9, 38);
        this.HornL2.setPos(0.0F, 0.3F, 3.6F);
        this.HornL2.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5, modelSize);
        this.setRotateAngle(HornL2, -0.3391174736624982F, 0.0F, 0.0F);
        this.sleeveRight = new ModelPart(this, 36, 33);
        this.sleeveRight.setPos(0.3F, -0.3F, 0.0F);
        this.sleeveRight.addBox(-4.5F, -2.1F, -2.4F, 5, 6, 5, modelSize);
        this.setRotateAngle(sleeveRight, 0.0F, 0.0F, -0.12217304763960307F);
        this.HornR5 = new ModelPart(this, 25, 45);
        this.HornR5.mirror = true;
        this.HornR5.setPos(0.0F, -0.1F, 4.3F);
        this.HornR5.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 3, modelSize);
        this.setRotateAngle(HornR5, 0.3649483465920143F, 0.0F, 0.0F);
        this.robeLowerRight = new ModelPart(this, 4, 51);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerRight.addBox(-2.1F, 0.0F, -2.5F, 4, 7, 5, modelSize);
        this.robeLowerLeft = new ModelPart(this, 4, 51);
        this.robeLowerLeft.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerLeft.addBox(-1.9F, 0.0F, -2.5F, 4, 7, 5, modelSize);
        this.visor1 = new ModelPart(this, 27, 50);
        this.visor1.setPos(0.0F, 9.0F, 0.2F);
        this.visor1.addBox(-4.7F, -13.3F, -4.9F, 4, 5, 8, modelSize);
        this.HornR = new ModelPart(this, 9, 39);
        this.HornR.setPos(-2.5F, -7.9F, -4.2F);
        this.HornR.addBox(-1.0F, -0.5F, 0.0F, 2, 2, 4, modelSize);
        this.setRotateAngle(HornR, 0.43022366061660217F, -0.15707963267948966F, 0.0F);
        this.HornR4 = new ModelPart(this, 9, 38);
        this.HornR4.setPos(-3.2F, -7.4F, -3.0F);
        this.HornR4.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5, modelSize);
        this.setRotateAngle(HornR4, -0.14713125594312196F, -0.296705972839036F, 0.0F);
        this.sleeveLeft = new ModelPart(this, 36, 33);
        this.sleeveLeft.mirror = true;
        this.sleeveLeft.setPos(-0.7F, -0.3F, 0.0F);
        this.sleeveLeft.addBox(-0.5F, -2.1F, -2.4F, 5, 6, 5, modelSize);
        this.setRotateAngle(sleeveLeft, 0.0F, 0.0F, 0.12217304763960307F);
        this.HornR2 = new ModelPart(this, 9, 38);
        this.HornR2.setPos(0.0F, 0.3F, 3.6F);
        this.HornR2.addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5, modelSize);
        this.setRotateAngle(HornR2, -0.3391174736624982F, 0.0F, 0.0F);
        this.head.addChild(this.HornL4);
        this.HornL4.addChild(this.HornL5);
        this.head.addChild(this.HornL);
        this.HornL2.addChild(this.HornL3);
        this.head.addChild(this.visor2);
        this.HornR2.addChild(this.HornR3);
        this.HornL.addChild(this.HornL2);
        this.rightArm.addChild(this.sleeveRight);
        this.HornR4.addChild(this.HornR5);
        this.rightLeg.addChild(this.robeLowerRight);
        this.leftLeg.addChild(this.robeLowerLeft);
        this.head.addChild(this.visor1);
        this.head.addChild(this.HornR);
        this.head.addChild(this.HornR4);
        this.leftArm.addChild(this.sleeveLeft);
        this.HornR.addChild(this.HornR2);
    }

    public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
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
    }
}
