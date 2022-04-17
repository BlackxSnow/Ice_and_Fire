package com.github.alexthe666.iceandfire.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.ForgeHooksClient;

public class IceAndFireMainMenu extends TitleScreen {
    public static final int LAYER_COUNT = 2;
    public static final ResourceLocation splash = new ResourceLocation(IceAndFire.MODID, "splashes.txt");
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation BESTIARY_TEXTURE = new ResourceLocation("iceandfire:textures/gui/main_menu/bestiary_menu.png");
    private static final ResourceLocation TABLE_TEXTURE = new ResourceLocation("iceandfire:textures/gui/main_menu/table.png");
    public static ResourceLocation[] pageFlipTextures;
    public static ResourceLocation[] drawingTextures = new ResourceLocation[22];
    private final String[] namePartsArray = "Go Play My Other Mods Like Fossils Archeology Revival and Rats And Soon To Be Joined By Other Cool Stuff Too Dont Play The Knock Off Mods".split(" ");
    private int layerTick;
    private String splashText;
    private boolean isFlippingPage = false;
    private int pageFlip = 0;
    private Picture[] drawnPictures;
    private Enscription[] drawnEnscriptions;
    private float globalAlpha = 1F;
    private int zLevel = 200;

    public IceAndFireMainMenu() {
        pageFlipTextures = new ResourceLocation[]{new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_1.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_2.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_3.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_4.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_5.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_6.png")};
        for (int i = 0; i < drawingTextures.length; i++) {
            drawingTextures[i] = new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/drawing_" + (i + 1) + ".png");
        }
        resetDrawnImages();
        BufferedReader reader = null;
        try {
            List<String> list = new ArrayList<>();
            String branch = "1.16.3";
            reader = getURLContents("https://raw.githubusercontent.com/Alex-the-666/Ice_and_Fire/" + branch + "/src/main/resources/assets/iceandfire/splashes.txt", "assets/iceandfire/splashes.txt");
            String s;

            while ((s = reader.readLine()) != null) {
                s = s.trim();

                if (!s.isEmpty()) {
                    list.add(s);
                }
            }

            if (!list.isEmpty()) {
                do {
                    this.splashText = list.get(new Random().nextInt(list.size()));
                } while (this.splashText.hashCode() == 125780783);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static BufferedReader getURLContents(String urlString, String backupFileLoc) {
        BufferedReader reader = null;
        boolean useBackup = false;
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            url = null;
            useBackup = true;
        }
        if (url != null) {
            URLConnection connection = null;
            try {
                connection = url.openConnection();
                connection.setConnectTimeout(200);
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
            } catch (IOException e) {
                IceAndFire.LOGGER.warn("Ice and Fire couldn't download splash texts for main menu");
                useBackup = true;
            }
        }
        if (useBackup) {
            InputStream is = IceAndFireMainMenu.class.getClassLoader().getResourceAsStream(backupFileLoc);
            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        }
        return reader;
    }



    private void resetDrawnImages() {
        globalAlpha = 0;
        Random random = new Random();
        drawnPictures = new Picture[1 + random.nextInt(2)];
        int cornerRight = 32;
        int cornerLeft = 32;
        boolean left = random.nextBoolean();
        for (int i = 0; i < drawnPictures.length; i++) {
            left = !left;
            int x;
            int y = random.nextInt(25);
            if (left) {
                x = -15 - random.nextInt(20) - 128;
            } else {
                x = 30 + random.nextInt(20);
            }
            drawnPictures[i] = new Picture(random.nextInt(drawingTextures.length - 1), x, y, 0.5F, random.nextFloat() * 0.5F + 0.5F);
        }
        drawnEnscriptions = new Enscription[4 + random.nextInt(8)];
        for (int i = 0; i < drawnEnscriptions.length; i++) {
            left = !left;
            int x;
            int y = 10 + random.nextInt(130);
            if (left) {
                x = -30 - random.nextInt(30) - 50;
            } else {
                x = 30 + random.nextInt(30);
            }
            String s1 = "missingno";
            drawnEnscriptions[i] = new Enscription(s1, x, y, random.nextFloat() * 0.5F + 0.5F, 0X9C8B7B);
        }
    }

    @Override
    public void tick() {
        super.tick();
        float flipTick = layerTick % 40;
        if (globalAlpha < 1 && !isFlippingPage && flipTick < 30) {
            globalAlpha += 0.1F;
        }

        if (globalAlpha > 0 && flipTick > 30) {
            globalAlpha -= 0.1F;
        }
        if (flipTick == 0 && !isFlippingPage) {
            isFlippingPage = true;
        }
        if (isFlippingPage) {
            if (layerTick % 2 == 0) {
                pageFlip++;
            }
            if (pageFlip == 6) {
                pageFlip = 0;
                isFlippingPage = false;
                resetDrawnImages();
            }
        }

        this.layerTick++;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        GlStateManager._enableTexture();
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._enableBlend();
        this.getMinecraft().getTextureManager().bind(TABLE_TEXTURE);
        int width = this.width;
        int height = this.height;
        blit(ms, 0, 0, 0, 0, width, height, width, height);
        this.getMinecraft().getTextureManager().bind(BESTIARY_TEXTURE);
        blit(ms, 50, 0, 0, 0, width - 100, height, width - 100, height);
        if (this.isFlippingPage) {
            this.getMinecraft().getTextureManager().bind(pageFlipTextures[Math.min(5, pageFlip)]);
            blit(ms, 50, 0, 0, 0, width - 100, height, width - 100, height);
        } else {
            int middleX = width / 2;
            int middleY = height / 5;
            float widthScale = width / 427F;
            float imageScale = widthScale * 128;
            for (Enscription enscription : drawnEnscriptions) {
                float f2 = (float) 60 - partialTicks;
                int color = 0X9C8B7B;
                int opacity = 10 + (int) (255 * enscription.alpha * globalAlpha);
            //    fontrenderer.drawStringWithShadow(ms, enscription.text, (int) (enscription.x * widthScale) + middleX, (int) (enscription.y * widthScale) + middleY, color | (opacity << 24));
            }
            for (Picture picture : drawnPictures) {
                float alpha = (picture.alpha * globalAlpha + 0.01F);
                RenderSystem.enableBlend();
                this.getMinecraft().getTextureManager().bind(drawingTextures[picture.image]);
                //3 -> 1
                //1 -> 3
                GuiMainMenuBlit.blit((int) ((picture.x * widthScale) + middleX), (int) ((picture.y * widthScale) + middleY), 0, 0, (int) imageScale, (int) imageScale, (int) imageScale, (int) imageScale, alpha);
            }
        }
        GlStateManager._enableTexture();
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager._enableBlend();
        this.getMinecraft().font.draw(ms, "Ice and Fire " + ChatFormatting.YELLOW + IceAndFire.VERSION, 2, height - 10, 0xFFFFFFFF);
        GlStateManager._pushMatrix();
        this.getMinecraft().getTextureManager().bind(MINECRAFT_TITLE_TEXTURES);
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(ms, width / 2 - 274 / 2, 10, 0, 0, 155, 44);
        blit(ms, width / 2 - 274 / 2 + 155, 10, 0, 45, 155, 44);
        GlStateManager._translatef((float) (width / 2 + 100), 85.0F, 0.0F);
        GlStateManager._rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - Mth.abs(Mth.sin((float) (System.currentTimeMillis() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
        f1 = f1 * 100.0F / (float) (this.getMinecraft().font.width(this.splashText) + 32);
        GlStateManager._translatef(0, f1 * 10, 0.0F);
        GlStateManager._scalef(f1, f1, f1);
        this.drawCenteredString(ms, this.getMinecraft().font, this.splashText, 0, -40, ChatFormatting.YELLOW.getColor());
        GlStateManager._popMatrix();

        ForgeHooksClient.renderMainMenu(this, ms, this.getMinecraft().font, width, height);
        String s1 = "Copyright Mojang AB. Do not distribute!";
        Font font = this.getMinecraft().font;
        this.drawString(ms, font, s1, width - this.getMinecraft().font.width(s1) - 2, height - 10, 0xFFFFFFFF);
        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(ms, mouseX, mouseY, partialTicks);
        }
        for (int i = 0; i < this.buttons.size(); i++) {
            buttons.get(i).render(ms, mouseX, mouseY, getMinecraft().getFrameTime());
        }
    }

    private class Picture {
        int image;
        int x;
        int y;
        float alpha;
        float scale;

        public Picture(int image, int x, int y, float alpha, float scale) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.alpha = alpha;
            this.scale = scale;
        }
    }

    private class Enscription {
        String text;
        int x;
        int y;
        int color;
        float alpha;

        public Enscription(String text, int x, int y, float alpha, int color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.alpha = alpha;
            this.color = color;
        }
    }
}

