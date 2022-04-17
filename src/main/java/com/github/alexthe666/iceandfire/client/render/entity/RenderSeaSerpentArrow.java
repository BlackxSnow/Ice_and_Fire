package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSeaSerpentArrow extends ArrowRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/misc/sea_serpent_arrow.png");

    public RenderSeaSerpentArrow(EntityRenderDispatcher render) {
        super(render);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TEXTURE;
    }

}