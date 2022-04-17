package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.inventory.ContainerDragonForge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDragonForge extends AbstractContainerScreen<ContainerDragonForge> {
    private static final ResourceLocation TEXTURE_FIRE = new ResourceLocation("iceandfire:textures/gui/dragonforge_fire.png");
    private static final ResourceLocation TEXTURE_ICE = new ResourceLocation("iceandfire:textures/gui/dragonforge_ice.png");
    private static final ResourceLocation TEXTURE_LIGHTNING = new ResourceLocation("iceandfire:textures/gui/dragonforge_lightning.png");
    private final Inventory playerInventory;
    private ContainerDragonForge tileFurnace;
    private int dragonType;

    public GuiDragonForge(ContainerDragonForge container, Inventory inv, Component name) {
        super(container, inv, name);
        this.playerInventory = inv;
        this.tileFurnace = container;
        if (tileFurnace instanceof ContainerDragonForge) {
            this.dragonType = tileFurnace.isFire;
        }
    }

    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        Font font = this.getMinecraft().font;
        if (tileFurnace != null) {
            String s = I18n.get("block.iceandfire.dragonforge_" + DragonType.getNameFromInt(dragonType) + "_core");
            font.draw(stack, s, this.imageWidth / 2 - font.width(s) / 2, 6, 4210752);
        }
        font.draw(stack, this.playerInventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (dragonType == 0) {
            this.getMinecraft().getTextureManager().bind(TEXTURE_FIRE);
        } else if (dragonType == 1) {
            this.getMinecraft().getTextureManager().bind(TEXTURE_ICE);
        } else{
            this.getMinecraft().getTextureManager().bind(TEXTURE_LIGHTNING);
        }
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, k, l, 0, 0, this.imageWidth, this.imageHeight);
        int i1 = this.func_175381_h(126);
        this.blit(matrixStack, k + 12, l + 23, 0, 166, i1, 38);
    }

    private int func_175381_h(int p_175381_1_) {
        BlockEntity te = IceAndFire.PROXY.getRefrencedTE();
        int j = 0;
        int maxCookTime = 1000;
        if (te instanceof TileEntityDragonforge) {
            maxCookTime = ((TileEntityDragonforge) te).getMaxCookTime(tileFurnace.getSlot(0).getItem(), tileFurnace.getSlot(1).getItem());
            j = Math.min(((TileEntityDragonforge) te).cookTime, maxCookTime);
        }
        return j != 0 ? j * p_175381_1_ / maxCookTime : 0;
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

}