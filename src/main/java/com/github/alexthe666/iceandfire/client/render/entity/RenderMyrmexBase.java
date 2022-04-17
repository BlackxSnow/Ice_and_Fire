package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelMyrmexPupa;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerMyrmexItem;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.ListModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMyrmexBase extends MobRenderer<EntityMyrmexBase, ListModel<EntityMyrmexBase>> {

    private static final ListModel<EntityMyrmexBase> LARVA_MODEL = new ModelMyrmexPupa();
    private static final ListModel<EntityMyrmexBase> PUPA_MODEL = new ModelMyrmexPupa();
    private ListModel<EntityMyrmexBase> adultModel;

    public RenderMyrmexBase(EntityRenderDispatcher renderManager, ListModel model, float shadowSize) {
        super(renderManager, model, shadowSize);
        this.addLayer(new LayerMyrmexItem(this));
        this.adultModel = model;
    }

    public void render(EntityMyrmexBase entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.getGrowthStage() == 0) {
            model = LARVA_MODEL;
        }else if (entityIn.getGrowthStage() == 1) {
            model = PUPA_MODEL;
        }else{
            model = adultModel;
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

    }
        @Override
    protected void scale(EntityMyrmexBase myrmex, PoseStack matrixStackIn, float partialTickTime) {
        float scale = myrmex.getModelScale();
        if (myrmex.getGrowthStage() == 0) {
            scale /= 2;
        }
        if (myrmex.getGrowthStage() == 1) {
            scale /= 1.5F;
        }
        matrixStackIn.scale(scale, scale, scale);
        if (myrmex.isPassenger() && myrmex.getGrowthStage() < 2) {
            matrixStackIn.mulPose(new Quaternion(Vector3f.YP, 90, true));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMyrmexBase myrmex) {
        return myrmex.getTexture();
    }

}
