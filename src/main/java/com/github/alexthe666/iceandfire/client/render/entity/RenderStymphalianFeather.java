package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderStymphalianFeather extends ArrowRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/stymphalianbird/feather.png");

    public RenderStymphalianFeather(EntityRenderDispatcher render) {
        super(render);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return TEXTURE;
    }
}