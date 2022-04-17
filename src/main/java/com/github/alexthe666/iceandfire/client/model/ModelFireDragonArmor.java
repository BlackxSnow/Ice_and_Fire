package com.github.alexthe666.iceandfire.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ModelFireDragonArmor extends HumanoidModel {
    public ModelPart HornL;
    public ModelPart HornR;
    public ModelPart HornL3;
    public ModelPart HornR3;
    public ModelPart HeadFront;
    public ModelPart Jaw;
    public ModelPart HornL2;
    public ModelPart HornR2;
    public ModelPart Teeth1;
    public ModelPart Teeth2;
    public ModelPart RightShoulderSpike1;
    public ModelPart RightShoulderSpike2;
    public ModelPart LeftLegSpike;
    public ModelPart LeftLegSpike2;
    public ModelPart LeftLegSpike3;
    public ModelPart BackSpike1;
    public ModelPart BackSpike2;
    public ModelPart BackSpike3;
    public ModelPart LeftShoulderSpike1;
    public ModelPart LeftShoulderSpike2;
    public ModelPart RightLegSpike;
    public ModelPart RightLegSpike2;
    public ModelPart RightLegSpike3;

    public ModelFireDragonArmor(float modelSize, boolean legs) {
        super(modelSize, 0, 64, 64);
        this.texWidth = 64;
        this.texHeight = 64;
        this.RightLegSpike3 = new ModelPart(this, 0, 34);
        this.RightLegSpike3.setPos(-0.8F, 0.0F, -0.8F);
        this.RightLegSpike3.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(RightLegSpike3, -1.2217304763960306F, 1.2217304763960306F, -0.17453292519943295F);
        this.LeftShoulderSpike2 = new ModelPart(this, 0, 34);
        this.LeftShoulderSpike2.setPos(1.8F, -0.1F, 0.0F);
        this.LeftShoulderSpike2.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(LeftShoulderSpike2, -3.141592653589793F, 0.0F, 0.2617993877991494F);
        this.HornL2 = new ModelPart(this, 46, 36);
        this.HornL2.mirror = true;
        this.HornL2.setPos(0.0F, 0.3F, 4.5F);
        this.HornL2.addBox(-0.5F, -0.8F, -0.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(HornL2, -0.07504915783575616F, 0.0F, 0.0F);
        this.RightLegSpike = new ModelPart(this, 0, 34);
        this.RightLegSpike.setPos(0.0F, 5.0F, 0.4F);
        this.RightLegSpike.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(RightLegSpike, -1.4114477660878142F, 0.0F, 0.0F);
        this.HeadFront = new ModelPart(this, 6, 44);
        this.HeadFront.setPos(0.0F, -5.6F, 0.0F);
        this.HeadFront.addBox(-3.5F, -2.8F, -8.8F, 7, 2, 5, 0.0F);
        this.setRotateAngle(HeadFront, 0.045553093477052F, -0.0F, 0.0F);
        this.HornL3 = new ModelPart(this, 46, 36);
        this.HornL3.mirror = true;
        this.HornL3.setPos(4.0F, -4.0F, 0.7F);
        this.HornL3.addBox(-0.5F, -0.8F, -0.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(HornL3, -0.06981317007977318F, 0.4886921905584123F, 0.08726646259971647F);
        this.LeftLegSpike = new ModelPart(this, 0, 34);
        this.LeftLegSpike.setPos(0.0F, 5.0F, 0.4F);
        this.LeftLegSpike.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(LeftLegSpike, -1.4114477660878142F, 0.0F, 0.0F);
        this.RightShoulderSpike1 = new ModelPart(this, 0, 34);
        this.RightShoulderSpike1.setPos(-0.5F, -1.2F, 0.0F);
        this.RightShoulderSpike1.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(RightShoulderSpike1, -3.141592653589793F, 0.0F, -0.17453292519943295F);
        this.HornL = new ModelPart(this, 48, 44);
        this.HornL.mirror = true;
        this.HornL.setPos(3.6F, -8.0F, 1.0F);
        this.HornL.addBox(-1.0F, -0.5F, 0.0F, 2, 3, 5, 0.0F);
        this.setRotateAngle(HornL, 0.3141592653589793F, 0.33161255787892263F, 0.19198621771937624F);
        this.HornR = new ModelPart(this, 48, 44);
        this.HornR.setPos(-3.6F, -8.0F, 1.0F);
        this.HornR.addBox(-1.0F, -0.5F, 0.0F, 2, 3, 5, 0.0F);
        this.setRotateAngle(HornR, 0.3141592653589793F, -0.33161255787892263F, -0.19198621771937624F);
        this.RightShoulderSpike2 = new ModelPart(this, 0, 34);
        this.RightShoulderSpike2.setPos(-1.8F, -0.1F, 0.0F);
        this.RightShoulderSpike2.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(RightShoulderSpike2, -3.141592653589793F, 0.0F, -0.2617993877991494F);
        this.Teeth2 = new ModelPart(this, 6, 34);
        this.Teeth2.mirror = true;
        this.Teeth2.setPos(0.0F, -1.0F, 0.0F);
        this.Teeth2.addBox(-0.4F, 0.1F, -8.9F, 4, 1, 5, 0.0F);
        this.HornR3 = new ModelPart(this, 46, 36);
        this.HornR3.mirror = true;
        this.HornR3.setPos(-4.0F, -4.0F, 0.7F);
        this.HornR3.addBox(-0.5F, -0.8F, -0.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(HornR3, -0.06981317007977318F, -0.4886921905584123F, -0.08726646259971647F);
        this.BackSpike2 = new ModelPart(this, 0, 34);
        this.BackSpike2.setPos(0.0F, 3.5F, 0.6F);
        this.BackSpike2.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(BackSpike2, 1.1838568316277536F, 0.0F, 0.0F);
        this.LeftLegSpike2 = new ModelPart(this, 0, 34);
        this.LeftLegSpike2.setPos(0.7F, 3.6F, -0.4F);
        this.LeftLegSpike2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(LeftLegSpike2, -1.4114477660878142F, 0.0F, 0.0F);
        this.BackSpike1 = new ModelPart(this, 0, 34);
        this.BackSpike1.setPos(0.0F, 0.9F, 0.2F);
        this.BackSpike1.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(BackSpike1, 1.1838568316277536F, 0.0F, 0.0F);
        this.RightLegSpike2 = new ModelPart(this, 0, 34);
        this.RightLegSpike2.setPos(-0.7F, 3.6F, -0.4F);
        this.RightLegSpike2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(RightLegSpike2, -1.4114477660878142F, 0.0F, 0.0F);
        this.BackSpike3 = new ModelPart(this, 0, 34);
        this.BackSpike3.setPos(0.0F, 6.4F, 0.0F);
        this.BackSpike3.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(BackSpike3, 1.1838568316277536F, 0.0F, 0.0F);
        this.LeftShoulderSpike1 = new ModelPart(this, 0, 34);
        this.LeftShoulderSpike1.setPos(0.5F, -1.2F, 0.0F);
        this.LeftShoulderSpike1.addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotateAngle(LeftShoulderSpike1, -3.141592653589793F, 0.0F, 0.17453292519943295F);
        this.Jaw = new ModelPart(this, 6, 51);
        this.Jaw.setPos(0.0F, -5.4F, 0.0F);
        this.Jaw.addBox(-3.5F, 4.0F, -7.4F, 7, 2, 5, 0.0F);
        this.setRotateAngle(Jaw, -0.091106186954104F, -0.0F, 0.0F);
        this.LeftLegSpike3 = new ModelPart(this, 0, 34);
        this.LeftLegSpike3.setPos(0.8F, -0.0F, -0.8F);
        this.LeftLegSpike3.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(LeftLegSpike3, -1.2217304763960306F, -1.2217304763960306F, 0.17453292519943295F);
        this.HornR2 = new ModelPart(this, 46, 36);
        this.HornR2.mirror = true;
        this.HornR2.setPos(0.0F, 0.3F, 4.5F);
        this.HornR2.addBox(-0.5F, -0.8F, -0.0F, 1, 2, 5, 0.0F);
        this.setRotateAngle(HornR2, -0.07504915783575616F, 0.0F, 0.0F);
        this.Teeth1 = new ModelPart(this, 6, 34);
        this.Teeth1.setPos(0.0F, -1.0F, 0.0F);
        this.Teeth1.addBox(-3.6F, 0.1F, -8.9F, 4, 1, 5, 0.0F);
        if (legs) {
            this.leftLeg.addChild(this.LeftLegSpike3);
            this.leftLeg.addChild(this.LeftLegSpike2);
            this.leftLeg.addChild(this.LeftLegSpike);
            this.rightLeg.addChild(this.RightLegSpike3);
            this.rightLeg.addChild(this.RightLegSpike2);
            this.rightLeg.addChild(this.RightLegSpike);
        } else {
            this.leftArm.addChild(this.LeftShoulderSpike2);
            this.HornL.addChild(this.HornL2);
            this.head.addChild(this.HeadFront);
            this.head.addChild(this.HornL3);
            this.rightArm.addChild(this.RightShoulderSpike1);
            this.head.addChild(this.HornL);
            this.head.addChild(this.HornR);
            this.rightArm.addChild(this.RightShoulderSpike2);
            this.HeadFront.addChild(this.Teeth2);
            this.head.addChild(this.HornR3);
            this.body.addChild(this.BackSpike2);
            this.body.addChild(this.BackSpike1);
            this.body.addChild(this.BackSpike3);
            this.leftArm.addChild(this.LeftShoulderSpike1);
            this.head.addChild(this.Jaw);
            this.HornR.addChild(this.HornR2);
            this.HeadFront.addChild(this.Teeth1);
        }
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
