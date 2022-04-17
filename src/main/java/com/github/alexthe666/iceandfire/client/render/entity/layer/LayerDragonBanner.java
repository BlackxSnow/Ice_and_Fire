package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public class LayerDragonBanner extends RenderLayer<EntityDragonBase, ListModel<EntityDragonBase>> {


    private final RenderLayerParent<EntityDragonBase, ListModel<EntityDragonBase>> renderer;

    public LayerDragonBanner(MobRenderer renderIn) {
        super(renderIn);
        this.renderer = renderIn;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityDragonBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemInHand(InteractionHand.OFF_HAND);
        matrixStackIn.pushPose();
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof BannerItem) {
            float f = (entity.getRenderSize() / 3F);
            float f2 = 1F / f;
            matrixStackIn.pushPose();
            postRender(((TabulaModel) this.renderer.getModel()).getCube("BodyUpper"), matrixStackIn, 0.0625F);
            matrixStackIn.translate(0, -0.2F, 0.4F);
            matrixStackIn.mulPose(new Quaternion(Vector3f.XP, 180, true));
            matrixStackIn.pushPose();
            matrixStackIn.scale(f2, f2, f2);
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.NONE, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
    }

    protected void postRender(AdvancedModelBox renderer, PoseStack matrixStackIn, float scale) {
        if (renderer.xRot == 0.0F && renderer.yRot == 0.0F && renderer.zRot == 0.0F) {
            if (renderer.x != 0.0F || renderer.y != 0.0F || renderer.z != 0.0F) {
                matrixStackIn.translate(renderer.x * scale, renderer.y * scale, renderer.z * scale);
            }
        } else {
            matrixStackIn.translate(renderer.x * scale, renderer.y * scale, renderer.z * scale);
            if (renderer.zRot != 0.0F) {
                matrixStackIn.mulPose(Vector3f.ZP.rotation(renderer.zRot));
            }

            if (renderer.yRot != 0.0F) {
                matrixStackIn.mulPose(Vector3f.YP.rotation(renderer.yRot));
            }

            if (renderer.xRot != 0.0F) {
                matrixStackIn.mulPose(Vector3f.XP.rotation(renderer.xRot));
            }
        }
    }

}