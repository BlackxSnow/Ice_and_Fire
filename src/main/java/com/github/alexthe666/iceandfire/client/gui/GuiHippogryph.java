package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.inventory.ContainerHippogryph;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHippogryph extends AbstractContainerScreen<ContainerHippogryph> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iceandfire:textures/gui/hippogryph.png");
    private final Inventory playerInventory;
    private final ContainerHippogryph hippogryphInv;
    private float mousePosx;
    private float mousePosY;

    public GuiHippogryph(ContainerHippogryph dragonInv, Inventory playerInv, Component name) {
        super(dragonInv, playerInv, name);
        this.playerInventory = playerInv;
        this.hippogryphInv = dragonInv;
    }

    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        Entity entity = IceAndFire.PROXY.getReferencedMob();
        Font font = this.getMinecraft().font;
        if (entity instanceof EntityHippogryph) {
            EntityHippogryph hippo = (EntityHippogryph) entity;
            font.draw(matrixStack, hippo.getDisplayName().getString(), 8,  6, 4210752);
        }
        font.draw(matrixStack, this.playerInventory.getDisplayName().getString(),  8,  this.imageHeight - 96 + 2, 4210752);
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bind(TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        Entity entity = IceAndFire.PROXY.getReferencedMob();
        if (entity instanceof EntityHippogryph) {
            EntityHippogryph hippo = (EntityHippogryph) entity;
            if (hippo.isChested()) {
                this.blit(matrixStack, i + 79, j + 17, 0, this.imageHeight, 5 * 18, 54);
            }
            GuiDragon.drawEntityOnScreen(i + 51, j + 60, 17, (float) (i + 51) - this.mousePosx, (float) (j + 75 - 50) - this.mousePosY, hippo);
        }
    }

}