package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.model.ModelMyrmexBase;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public class LayerMyrmexItem extends RenderLayer<EntityMyrmexBase, ListModel<EntityMyrmexBase>> {

    protected final RenderMyrmexBase livingEntityRenderer;

    public LayerMyrmexItem(RenderMyrmexBase livingEntityRendererIn) {
        super(livingEntityRendererIn);
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    private void renderHeldItem(EntityMyrmexBase myrmex, ItemStack stack, ItemTransforms.TransformType transform, HumanoidArm handSide) {

    }

    protected void translateToHand(HumanoidArm side, PoseStack stack) {
        ((ModelMyrmexBase) this.livingEntityRenderer.getModel()).postRenderArm(0, stack);
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityMyrmexBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn instanceof EntityMyrmexWorker) {
            ItemStack itemstack = entitylivingbaseIn.getItemInHand(InteractionHand.MAIN_HAND);
            if (!itemstack.isEmpty()) {
                matrixStackIn.pushPose();
                if (!itemstack.isEmpty()) {
                    matrixStackIn.pushPose();

                    if (entitylivingbaseIn.isShiftKeyDown()) {
                        matrixStackIn.translate(0.0F, 0.2F, 0.0F);
                    }
                    this.translateToHand(HumanoidArm.RIGHT, matrixStackIn);
                    matrixStackIn.translate(0F, 0.3F, -1.6F);
                    if(itemstack.getItem() instanceof BlockItem){
                        matrixStackIn.translate(0F, 0, 0.2F);
                    }else{
                        matrixStackIn.translate(0F, 0.2F, 0.3F);
                    }
                    matrixStackIn.mulPose(new Quaternion(Vector3f.XP, 160, true));
                    matrixStackIn.mulPose(new Quaternion(Vector3f.YP, 180, true));
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
                    matrixStackIn.popPose();
                }
                matrixStackIn.popPose();
            }
        }
    }
}