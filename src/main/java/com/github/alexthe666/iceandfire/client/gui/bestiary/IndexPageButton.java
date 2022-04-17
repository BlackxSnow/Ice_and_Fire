package com.github.alexthe666.iceandfire.client.gui.bestiary;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndexPageButton extends Button {

    public IndexPageButton(int id, int x, int y, Component buttonText, net.minecraft.client.gui.components.Button.OnPress butn) {
        super(x, y, 160, 32, buttonText, butn);
        this.width = 160;
        this.height = 32;
    }

    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partial) {
        if (this.active) {
            Font fontrenderer = IafConfig.useVanillaFont ? Minecraft.getInstance().font : (Font) IceAndFire.PROXY.getFontRenderer();
            GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bind(new ResourceLocation("iceandfire:textures/gui/bestiary/widgets.png"));
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.blit(matrixStack, this.x, this.y, 0, flag ? 32 : 0, this.width, this.height  );
            int j =  flag ? 0XFAE67D : 0X303030;
            fontrenderer.draw(matrixStack, this.getMessage().getVisualOrderText(), (this.x + this.width / 2  - fontrenderer.width(this.getMessage().getString()) / 2), this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
