package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;

import net.minecraft.client.model.HumanoidModel;

public class ModelStonePlayer extends HumanoidModel<EntityStoneStatue> {

    public ModelStonePlayer(float modelSize, boolean smallArmsIn) {
        super(modelSize);
    }

    public void setupAnim(EntityStoneStatue entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
