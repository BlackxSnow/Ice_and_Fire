package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelStymphalianBird;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderStymphalianBird extends MobRenderer<EntityStymphalianBird, ModelStymphalianBird> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/models/stymphalianbird/stymphalian_bird.png");

    public RenderStymphalianBird(EntityRenderDispatcher renderManager) {
        super(renderManager, new ModelStymphalianBird(), 0.6F);
    }

    @Override
    public void scale(EntityStymphalianBird LivingEntityIn, PoseStack stack, float partialTickTime) {
        stack.scale(0.75F, 0.75F, 0.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityStymphalianBird cyclops) {
        return TEXTURE;
    }

}
