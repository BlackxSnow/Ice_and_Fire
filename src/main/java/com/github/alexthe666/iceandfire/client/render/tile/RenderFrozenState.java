package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import com.mojang.math.Matrix4f;

public class RenderFrozenState {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("textures/block/frosted_ice_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("textures/block/frosted_ice_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("textures/block/frosted_ice_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("textures/block/frosted_ice_3.png");

    public static void render(LivingEntity entity, PoseStack matrixStack) {
        float sideExpand = -0.125F;
        float sideExpandY = 0.325F;
        AABB axisalignedbb1 = new AABB(-entity.getBbWidth() / 2F - sideExpand, 0, -entity.getBbWidth() / 2F - sideExpand,
            entity.getBbWidth() / 2F + sideExpand, entity.getBbHeight() + sideExpandY, entity.getBbWidth() / 2F + sideExpand);
        matrixStack.pushPose();
        matrixStack.pushPose();
        RenderSystem.enableDepthTest();
        Minecraft.getInstance().getTextureManager().bind(getIceTexture(FrozenProperties.ticksUntilUnfrozen(entity)));
        renderMovingAABB(axisalignedbb1, matrixStack);
        RenderSystem.disableDepthTest();
        matrixStack.popPose();
        matrixStack.popPose();
    }

    private static ResourceLocation getIceTexture(int ticksFrozen) {
        if (ticksFrozen < 100) {
            if (ticksFrozen < 50) {
                if (ticksFrozen < 20) {
                    return TEXTURE_3;
                }
                return TEXTURE_2;
            }
            return TEXTURE_1;
        }
        return TEXTURE_0;
    }

    public static void renderMovingAABB(AABB boundingBox, PoseStack stack) {
        Tesselator tessellator = Tesselator.getInstance();
        VertexConsumer vertexbuffer = tessellator.getBuilder().getVertexBuilder();
        BufferBuilder buffer = tessellator.getBuilder();
        float f3 = 0;
        Matrix4f matrix4f = stack.last().pose();
        float maxX = (float) boundingBox.maxX * 0.425F;
        float minX = (float) boundingBox.minX * 0.425F;
        float maxY = (float) boundingBox.maxY * 0.425F;
        float minY = (float) boundingBox.minY * 0.425F;
        float maxZ = (float) boundingBox.maxZ * 0.425F;
        float minZ = (float) boundingBox.minZ * 0.425F;
        buffer.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).uv(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).uv(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).uv(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).uv(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, -1.0F).endVertex();

        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).uv(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).uv(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).uv(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).uv(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(0.0F, 0.0F, 1.0F).endVertex();

        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).uv(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).uv(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).uv(f3 + maxX - minX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).uv(f3 + minX - maxX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, -1.0F, 0.0F).endVertex();

        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).uv(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).uv(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).uv(f3 + maxX - minX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).uv(f3 + minX - maxX, f3 + maxZ - minZ).color(255, 255, 255, 255).normal(0.0F, 1.0F, 0.0F).endVertex();

        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).uv(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).uv(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).uv(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).uv(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(-1.0F, 0.0F, 0.0F).endVertex();

        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).uv(f3 + minX - maxX, f3 + minY - maxY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).uv(f3 + minX - maxX, f3 + maxY - minY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).uv(f3 + maxX - minX, f3 + maxY - minY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        vertexbuffer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).uv(f3 + maxX - minX, f3 + minY - maxY).color(255, 255, 255, 255).normal(1.0F, 0.0F, 0.0F).endVertex();
        tessellator.end();
    }
}
