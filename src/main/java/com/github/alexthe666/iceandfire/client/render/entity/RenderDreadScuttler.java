package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDreadScuttler;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadScuttler;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderDreadScuttler extends MobRenderer<EntityDreadScuttler, ModelDreadScuttler> {

    public static final ResourceLocation TEXTURE_EYES = new ResourceLocation("iceandfire:textures/models/dread/dread_scuttler_eyes.png");
    public static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/dread/dread_scuttler.png");

    public RenderDreadScuttler(EntityRenderDispatcher renderManager) {
        super(renderManager, new ModelDreadScuttler(), 0.75F);
        this.addLayer(new LayerGenericGlowing(this, TEXTURE_EYES));
    }

    @Override
    public void scale(EntityDreadScuttler LivingEntityIn, PoseStack stack, float partialTickTime) {
        stack.scale(LivingEntityIn.getScale(), LivingEntityIn.getScale(), LivingEntityIn.getScale());
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDreadScuttler beast) {
        return TEXTURE;

    }

}
