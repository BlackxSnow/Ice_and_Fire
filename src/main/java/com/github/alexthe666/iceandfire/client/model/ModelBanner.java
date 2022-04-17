package com.github.alexthe666.iceandfire.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ModelBanner extends ListModel {
    public final ModelPart flag = getModelRender();
    public final ModelPart pole = new ModelPart(64, 64, 44, 0);
    public final ModelPart bar;

    public ModelBanner() {
        this.texHeight = 64;
        this.texWidth = 64;
        this.pole.addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
        this.bar = new ModelPart(64, 64, 0, 42);
        this.bar.addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
    }

    public static ModelPart getModelRender() {
        ModelPart modelrenderer = new ModelPart(64, 64, 0, 0);
        modelrenderer.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
        return modelrenderer;
    }

    @Override
    public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(flag, pole, bar);
    }

}
