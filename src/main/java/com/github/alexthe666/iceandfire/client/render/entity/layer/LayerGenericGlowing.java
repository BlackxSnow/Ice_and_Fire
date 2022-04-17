package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerGenericGlowing<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final LivingEntityRenderer render;
    private ResourceLocation texture;

    public LayerGenericGlowing(LivingEntityRenderer renderIn, ResourceLocation texture) {
        super(renderIn);
        this.render = renderIn;
        this.texture = texture;
    }

    public boolean shouldCombineTextures() {
        return true;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType eyes = RenderType.eyes(texture);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(eyes);
        this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}