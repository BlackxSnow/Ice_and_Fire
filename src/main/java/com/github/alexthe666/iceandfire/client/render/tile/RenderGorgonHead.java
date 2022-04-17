package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelGorgonHead;
import com.github.alexthe666.iceandfire.client.model.ModelGorgonHeadActive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.ListModel;
import net.minecraft.resources.ResourceLocation;

public class RenderGorgonHead {

    private static final RenderType ACTIVE_TEXTURE = RenderType.entityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/gorgon/head_active.png"), false);
    private static final RenderType INACTIVE_TEXTURE = RenderType.entityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/gorgon/head_inactive.png"), false);
    private static final ListModel ACTIVE_MODEL = new ModelGorgonHeadActive();
    private static final ListModel INACTIVE_MODEL = new ModelGorgonHead();
    private final boolean active;

    public RenderGorgonHead(boolean alive) {
        this.active = alive;
    }

    public void render(PoseStack stackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ListModel model = active ? ACTIVE_MODEL : INACTIVE_MODEL;
        stackIn.pushPose();
        stackIn.translate(0.5F, active ?  1.5F : 1.25F, 0.5F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(active ? ACTIVE_TEXTURE : INACTIVE_TEXTURE);
        model.renderToBuffer(stackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        stackIn.popPose();
    }

}
