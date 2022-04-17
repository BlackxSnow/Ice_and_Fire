package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHydraArrow extends ArrowRenderer {
    private static final ResourceLocation TEXTURES = new ResourceLocation("iceandfire:textures/models/misc/hydra_arrow.png");

    public RenderHydraArrow(EntityRenderDispatcher render) {
        super(render);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TEXTURES;
    }

}