package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDragonFireCharge extends EntityRenderer<Fireball> {

    public boolean isFire;

    public RenderDragonFireCharge(EntityRenderDispatcher renderManager, boolean isFire) {
        super(renderManager);
        this.isFire = isFire;
    }

    @Override
    public ResourceLocation getTextureLocation(Fireball entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(Fireball entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(isFire ? Blocks.MAGMA_BLOCK.defaultBlockState() : IafBlockRegistry.DRAGON_ICE.defaultBlockState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
        matrixStackIn.popPose();
    }

}
