package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.model.ListModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMyrmexEgg extends LivingEntityRenderer<EntityMyrmexEgg, ListModel<EntityMyrmexEgg>> {

    public static final ResourceLocation EGG_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_egg.png");
    public static final ResourceLocation EGG_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_egg.png");

    public RenderMyrmexEgg(EntityRenderDispatcher renderManager) {
        super(renderManager, new ModelDragonEgg(), 0.3F);
    }

    protected boolean shouldShowName(EntityMyrmexEgg entity) {
        return entity.shouldShowName() && entity.hasCustomName();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMyrmexEgg entity) {
        return entity.isJungle() ? EGG_JUNGLE : EGG_DESERT;
    }

}
