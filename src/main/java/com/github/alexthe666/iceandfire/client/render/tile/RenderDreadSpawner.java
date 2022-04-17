package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawner;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.BaseSpawner;

public class RenderDreadSpawner<T extends TileEntityDreadSpawner> extends BlockEntityRenderer<T> {

    public RenderDreadSpawner(BlockEntityRenderDispatcher p_i226016_1_) {
        super(p_i226016_1_);
    }

    public void render(TileEntityDreadSpawner tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.0D, 0.5D);
        BaseSpawner abstractspawner = tileEntityIn.getSpawnerBaseLogic();
        Entity entity = abstractspawner.getOrCreateDisplayEntity();
        if (entity != null) {
            float f = 0.53125F;
            float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if ((double) f1 > 1.0D) {
                f /= f1;
            }

            matrixStackIn.translate(0.0D, 0.4F, 0.0D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float) Mth.lerp(partialTicks, abstractspawner.getoSpin(), abstractspawner.getSpin()) * 10.0F));
            matrixStackIn.translate(0.0D, -0.2F, 0.0D);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
            matrixStackIn.scale(f, f, f);
            Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        }

        matrixStackIn.popPose();
    }
}