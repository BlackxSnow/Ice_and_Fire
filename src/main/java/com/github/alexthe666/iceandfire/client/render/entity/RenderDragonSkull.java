package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDragonSkull extends EntityRenderer<EntityDragonSkull> {

    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};
    public float[][] growth_stages;
    private TabulaModel fireDragonModel;
    private TabulaModel lightningDragonModel;
    private TabulaModel iceDragonModel;

    public RenderDragonSkull(EntityRenderDispatcher renderManager, ListModel fireDragonModel, ListModel iceDragonModel, ListModel lightningDragonModel) {
        super(renderManager);
        growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
        this.fireDragonModel = (TabulaModel) fireDragonModel;
        this.iceDragonModel = (TabulaModel) iceDragonModel;
        this.lightningDragonModel = (TabulaModel) lightningDragonModel;
    }

    private static void setRotationAngles(ModelPart cube, float rotX, float rotY, float rotZ) {
        cube.xRot = rotX;
        cube.yRot = rotY;
        cube.zRot = rotZ;
    }

    public void render(EntityDragonSkull entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        TabulaModel model;
        if (entity.getDragonType() == 2) {
            model = lightningDragonModel;
        } else if (entity.getDragonType() == 1) {
            model = iceDragonModel;
        } else {
            model = fireDragonModel;
        }
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(new Quaternion(Vector3f.XP, -180, true));
        matrixStackIn.mulPose(new Quaternion(Vector3f.YN, 180 - entity.getYaw(), true));
        float f = 0.0625F;
        matrixStackIn.scale(1.0F,  1.0F, 1.0F);
        float size = getRenderSize(entity) / 3;
        matrixStackIn.scale(size, size, size);
        matrixStackIn.translate(0, entity.isOnWall() ? -0.24F : -0.12F, entity.isOnWall() ? 0.4F : 0.5F);
        model.resetToDefaultPose();
        setRotationAngles(model.getCube("Head"), entity.isOnWall() ? (float) Math.toRadians(50F) : 0F, 0, 0);
        model.getCube("Head").render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }
    public ResourceLocation getTextureLocation(EntityDragonSkull entity) {
        if (entity.getDragonType() == 2) {
            return EnumDragonTextures.getLightningDragonSkullTextures(entity);
        }
        if (entity.getDragonType() == 1) {
            return EnumDragonTextures.getIceDragonSkullTextures(entity);
        }
        return EnumDragonTextures.getFireDragonSkullTextures(entity);
    }


    public float getRenderSize(EntityDragonSkull skull) {
        float step = (growth_stages[skull.getDragonStage() - 1][1] - growth_stages[skull.getDragonStage() - 1][0]) / 25;
        if (skull.getDragonAge() > 125) {
            return growth_stages[skull.getDragonStage() - 1][0] + ((step * 25));
        }
        return growth_stages[skull.getDragonStage() - 1][0] + ((step * this.getAgeFactor(skull)));
    }

    private int getAgeFactor(EntityDragonSkull skull) {
        return (skull.getDragonStage() > 1 ? skull.getDragonAge() - (25 * (skull.getDragonStage() - 1)) : skull.getDragonAge());
    }

}
