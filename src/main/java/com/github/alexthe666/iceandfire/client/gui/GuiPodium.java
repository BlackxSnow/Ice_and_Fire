package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.inventory.ContainerPodium;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPodium extends AbstractContainerScreen<ContainerPodium> {

    private Inventory playerInventory;
    private Container podiumInventory;
    public static final ResourceLocation PODUIM_TEXTURE = new ResourceLocation("iceandfire:textures/gui/podium.png");

    public GuiPodium(ContainerPodium container, Inventory inv, Component name) {
        super(container, inv, name);
        this.playerInventory = inv;
        this.podiumInventory = container.podium;
        this.imageHeight = 133;
    }

    @Override
    protected void renderLabels(PoseStack ms, int x, int y) {
        if (podiumInventory != null) {
            String s = I18n.get("block.iceandfire.podium");
            this.getMinecraft().font.draw(ms, s, this.imageWidth / 2 - this.getMinecraft().font.width(s) / 2, 6, 4210752);
        }
        this.getMinecraft().font.draw(ms, this.playerInventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752);
    }


    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bind(PODUIM_TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

}