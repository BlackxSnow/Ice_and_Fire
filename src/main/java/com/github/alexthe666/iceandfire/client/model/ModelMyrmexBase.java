package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;

public abstract class ModelMyrmexBase<T extends EntityMyrmexBase> extends ModelDragonBase<T> {
    private static final ModelMyrmexLarva LARVA_MODEL = new ModelMyrmexLarva();
    private static final ModelMyrmexPupa PUPA_MODEL = new ModelMyrmexPupa();

    public void postRenderArm(float scale, PoseStack stackIn) {
        for (ModelPart renderer : this.getHeadParts()) {
            renderer.translateAndRotate(stackIn);
        }
    }

    public abstract ModelPart[] getHeadParts();
}
