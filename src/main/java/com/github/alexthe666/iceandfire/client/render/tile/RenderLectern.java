package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockLectern;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public class RenderLectern<T extends TileEntityLectern> extends BlockEntityRenderer<T> {

    private static final RenderType ENCHANTMENT_TABLE_BOOK_TEXTURE = RenderType.entityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/lectern_book.png"));
    private BookModel book = new BookModel();

    public RenderLectern(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        TileEntityLectern lectern = entity;
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 1.1F, 0.5F);
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
        matrixStackIn.mulPose(new Quaternion(Vector3f.YP, this.getRotation(lectern), true));
        matrixStackIn.mulPose(new Quaternion(Vector3f.XP, 112, true));
        matrixStackIn.mulPose(new Quaternion(Vector3f.YP, 90, true));
        float f4 = lectern.pageFlipPrev + (lectern.pageFlip - lectern.pageFlipPrev) * partialTicks + 0.25F;
        float f5 = lectern.pageFlipPrev + (lectern.pageFlip - lectern.pageFlipPrev) * partialTicks + 0.75F;
        f4 = (f4 - Mth.fastFloor(f4)) * 1.6F - 0.3F;
        f5 = (f5 - Mth.fastFloor(f5)) * 1.6F - 0.3F;

        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        if (f5 < 0.0F) {
            f5 = 0.0F;
        }

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        if (f5 > 1.0F) {
            f5 = 1.0F;
        }
        float f6 = 1.29F;

        this.book.setupAnim(partialTicks, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
        this.book.renderToBuffer(matrixStackIn, bufferIn.getBuffer(ENCHANTMENT_TABLE_BOOK_TEXTURE), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);
        matrixStackIn.popPose();
    }

    private float getRotation(TileEntityLectern lectern) {
        switch (lectern.getBlockState().getValue(BlockLectern.FACING)) {
            default:
                return 180;
            case EAST:
                return 90;
            case WEST:
                return -90;
            case SOUTH:
                return 0;

        }
    }

}
