package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

public class RenderGhostChest extends BlockEntityRenderer<TileEntityGhostChest> {

    private static final ResourceLocation GHOST_CHEST = new ResourceLocation("iceandfire:textures/models/ghost/ghost_chest.png");
    private static final ResourceLocation GHOST_CHEST_LEFT = new ResourceLocation("iceandfire:textures/models/ghost/ghost_chest_left.png");
    private static final ResourceLocation GHOST_CHEST_RIGHT = new ResourceLocation("iceandfire:textures/models/ghost/ghost_chest_right.png");
    private final ModelPart singleLid;
    private final ModelPart singleBottom;
    private final ModelPart singleLatch;
    private final ModelPart rightLid;
    private final ModelPart rightBottom;
    private final ModelPart rightLatch;
    private final ModelPart leftLid;
    private final ModelPart leftBottom;
    private final ModelPart leftLatch;


    public RenderGhostChest(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.singleBottom = new ModelPart(64, 64, 0, 19);
        this.singleBottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
        this.singleLid = new ModelPart(64, 64, 0, 0);
        this.singleLid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
        this.singleLid.y = 9.0F;
        this.singleLid.z = 1.0F;
        this.singleLatch = new ModelPart(64, 64, 0, 0);
        this.singleLatch.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
        this.singleLatch.y = 8.0F;
        this.rightBottom = new ModelPart(64, 64, 0, 19);
        this.rightBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.rightLid = new ModelPart(64, 64, 0, 0);
        this.rightLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.rightLid.y = 9.0F;
        this.rightLid.z = 1.0F;
        this.rightLatch = new ModelPart(64, 64, 0, 0);
        this.rightLatch.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.rightLatch.y = 8.0F;
        this.leftBottom = new ModelPart(64, 64, 0, 19);
        this.leftBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
        this.leftLid = new ModelPart(64, 64, 0, 0);
        this.leftLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
        this.leftLid.y = 9.0F;
        this.leftLid.z = 1.0F;
        this.leftLatch = new ModelPart(64, 64, 0, 0);
        this.leftLatch.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
        this.leftLatch.y = 8.0F;
    }

    protected ResourceLocation getMaterial(TileEntityGhostChest tileEntity, ChestType chestType) {
        return getChestMaterial(chestType, GHOST_CHEST, GHOST_CHEST_LEFT, GHOST_CHEST_RIGHT);
    }


    public void render(TileEntityGhostChest tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Level world = tileEntityIn.getLevel();
        boolean flag = world != null;
        BlockState blockstate = flag ? tileEntityIn.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Block block = blockstate.getBlock();
        if (block instanceof AbstractChestBlock) {
            AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock)block;
            boolean flag1 = chesttype != ChestType.SINGLE;
            matrixStackIn.pushPose();
            float f = blockstate.getValue(ChestBlock.FACING).toYRot();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-f));
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> icallbackwrapper;
            if (flag) {
                icallbackwrapper = abstractchestblock.combine(blockstate, world, tileEntityIn.getBlockPos(), true);
            } else {
                icallbackwrapper = DoubleBlockCombiner.Combiner::acceptNone;
            }

            float f1 = icallbackwrapper.<Float2FloatFunction>apply(ChestBlock.opennessCombiner(tileEntityIn)).get(partialTicks);
            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            int i = icallbackwrapper.<Int2IntFunction>apply(new BrightnessCombiner<>()).applyAsInt(combinedLightIn);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutout(getMaterial(tileEntityIn, chesttype)));
            if (flag1) {
                if (chesttype == ChestType.LEFT) {
                    this.renderModels(matrixStackIn, ivertexbuilder, this.leftLid, this.leftLatch, this.leftBottom, f1, i, combinedOverlayIn);
                } else {
                    this.renderModels(matrixStackIn, ivertexbuilder, this.rightLid, this.rightLatch, this.rightBottom, f1, i, combinedOverlayIn);
                }
            } else {
                this.renderModels(matrixStackIn, ivertexbuilder, this.singleLid, this.singleLatch, this.singleBottom, f1, i, combinedOverlayIn);
            }

            matrixStackIn.popPose();
        }
    }

    private void renderModels(PoseStack matrixStackIn, VertexConsumer bufferIn, ModelPart chestLid, ModelPart chestLatch, ModelPart chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
        chestLid.xRot = -(lidAngle * ((float)Math.PI / 2F));
        chestLatch.xRot = chestLid.xRot;
        chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestLatch.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    private static ResourceLocation getChestMaterial(ChestType chestType, ResourceLocation doubleMaterial, ResourceLocation leftMaterial, ResourceLocation rightMaterial) {
        switch(chestType) {
            case LEFT:
                return leftMaterial;
            case RIGHT:
                return rightMaterial;
            case SINGLE:
            default:
                return doubleMaterial;
        }
    }

}
