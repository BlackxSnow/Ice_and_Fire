package com.github.alexthe666.iceandfire.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

public class GuiMainMenuBlit {
    public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, int p_blit_3_, float p_blit_4_, float p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_, int p_blit_9_, float alpha) {
        innerBlit(p_blit_0_, p_blit_0_ + p_blit_2_, p_blit_1_, p_blit_1_ + p_blit_3_, 0, p_blit_6_, p_blit_7_, p_blit_4_, p_blit_5_, p_blit_8_, p_blit_9_, alpha);
    }

    public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, float alpha) {
        blit(p_blit_0_, p_blit_1_, p_blit_4_, p_blit_5_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_, alpha);
    }

    private static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, int p_innerBlit_5_, int p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_, int p_innerBlit_9_, int p_innerBlit_10_, float alpha) {
        innerBlit(p_innerBlit_0_, p_innerBlit_1_, p_innerBlit_2_, p_innerBlit_3_, p_innerBlit_4_, (p_innerBlit_7_ + 0.0F) / (float)p_innerBlit_9_, (p_innerBlit_7_ + (float)p_innerBlit_5_) / (float)p_innerBlit_9_, (p_innerBlit_8_ + 0.0F) / (float)p_innerBlit_10_, (p_innerBlit_8_ + (float)p_innerBlit_6_) / (float)p_innerBlit_10_, alpha);
    }

    protected static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, float p_innerBlit_5_, float p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_, float alpha) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex((double)p_innerBlit_0_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).color(1, 1, 1, alpha).uv(p_innerBlit_5_, p_innerBlit_8_).endVertex();
        bufferbuilder.vertex((double)p_innerBlit_1_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).color(1, 1, 1, alpha).uv(p_innerBlit_6_, p_innerBlit_8_).endVertex();
        bufferbuilder.vertex((double)p_innerBlit_1_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).color(1, 1, 1, alpha).uv(p_innerBlit_6_, p_innerBlit_7_).endVertex();
        bufferbuilder.vertex((double)p_innerBlit_0_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).color(1, 1, 1, alpha).uv(p_innerBlit_5_, p_innerBlit_7_).endVertex();
        bufferbuilder.end();
        RenderSystem.enableAlphaTest();
        BufferUploader.end(bufferbuilder);
    }
}
