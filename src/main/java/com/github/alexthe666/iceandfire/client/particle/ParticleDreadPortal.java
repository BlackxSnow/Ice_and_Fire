package com.github.alexthe666.iceandfire.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

public class ParticleDreadPortal extends TextureSheetParticle {
    private static final ResourceLocation SNOWFLAKE = new ResourceLocation("iceandfire:textures/particles/snowflake_0.png");
    private static final ResourceLocation SNOWFLAKE_BIG = new ResourceLocation("iceandfire:textures/particles/snowflake_1.png");

    private boolean big;

    public ParticleDreadPortal(ClientLevel world, double x, double y, double z, double motX, double motY, double motZ, float size) {
        super(world, x, y, z, motX, motY, motZ);
        this.setPos(x, y, z);
        big = random.nextBoolean();
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 inerp = renderInfo.getPosition();
        quadSize = 0.125F * (this.lifetime - (this.age));
        quadSize = quadSize * 0.09F;
        if (age > this.getLifetime()) {
            this.remove();
        }

        Vec3 Vector3d = renderInfo.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - Vector3d.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - Vector3d.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - Vector3d.z());
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = renderInfo.rotation();
        } else {
            quaternion = new Quaternion(renderInfo.rotation());
            float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        Minecraft.getInstance().getTextureManager().bind(big ? SNOWFLAKE_BIG : SNOWFLAKE);
        int j = this.getLightColor(partialTicks);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuilder();
        vertexbuffer.begin(7, DefaultVertexFormat.PARTICLE);
        vertexbuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexbuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexbuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexbuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        Tesselator.getInstance().end();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    public int getLightColor(float partialTick) {
        return 240;
    }

    public int getFXLayer() {
        return 3;
    }


}
