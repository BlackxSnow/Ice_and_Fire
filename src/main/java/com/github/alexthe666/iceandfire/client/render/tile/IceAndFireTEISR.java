package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.client.model.ModelTideTrident;
import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.client.render.entity.RenderTideTrident;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDeathwormGauntlet;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceAndFireTEISR extends BlockEntityWithoutLevelRenderer {

    private static final ModelTideTrident TIDE_TRIDENT_MODEL = new ModelTideTrident();
    private final RenderTrollWeapon renderTrollWeapon = new RenderTrollWeapon();
    private final RenderDeathWormGauntlet renderDeathWormGauntlet = new RenderDeathWormGauntlet();
    private final RenderDreadPortal renderDreadPortal = new RenderDreadPortal(BlockEntityRenderDispatcher.instance);
    private final RenderGorgonHead renderGorgonHead = new RenderGorgonHead(true);
    private final RenderGorgonHead renderGorgonHeadDead = new RenderGorgonHead(false);
    private final RenderPixieHouse renderPixieHouse = new RenderPixieHouse(BlockEntityRenderDispatcher.instance);
    private final TileEntityDreadPortal dreadPortalDummy = new TileEntityDreadPortal();
    private final RenderGhostChest renderGhostChest = new RenderGhostChest(BlockEntityRenderDispatcher.instance);
    private final TileEntityGhostChest ghostChestDummy = new TileEntityGhostChest();

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (itemStackIn.getItem() == IafItemRegistry.GORGON_HEAD) {
            if (itemStackIn.getTag() != null) {
                if (itemStackIn.getTag().getBoolean("Active")) {
                    renderGorgonHead.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
                } else {
                    renderGorgonHeadDead.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
                }
            }
        }
        if (itemStackIn.getItem() == IafBlockRegistry.GHOST_CHEST.asItem()) {
            renderGhostChest.render(ghostChestDummy, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }

        if (itemStackIn.getItem() instanceof ItemTrollWeapon) {
            ItemTrollWeapon weaponItem = (ItemTrollWeapon) itemStackIn.getItem();
            renderTrollWeapon.renderItem(weaponItem.weapon, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() instanceof ItemDeathwormGauntlet) {
            renderDeathWormGauntlet.renderItem(itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() instanceof BlockItem && ((BlockItem) itemStackIn.getItem()).getBlock() == IafBlockRegistry.DREAD_PORTAL) {
            renderDreadPortal.render(dreadPortalDummy, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() instanceof BlockItem && ((BlockItem) itemStackIn.getItem()).getBlock() instanceof BlockPixieHouse) {
            renderPixieHouse.metaOverride = (BlockItem) itemStackIn.getItem();
            renderPixieHouse.render(null, 0, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (itemStackIn.getItem() == IafItemRegistry.TIDE_TRIDENT) {
            matrixStackIn.translate(0.5F, 0.5f, 0.5f);
            if (p_239207_2_ == ItemTransforms.TransformType.GUI || p_239207_2_ == ItemTransforms.TransformType.FIXED || p_239207_2_ == ItemTransforms.TransformType.NONE || p_239207_2_ == ItemTransforms.TransformType.GROUND) {
                Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(IafItemRegistry.TIDE_TRIDENT_INVENTORY), p_239207_2_, p_239207_2_ == ItemTransforms.TransformType.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn);
            } else {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0, 0.2F, -0.15F);
                if(p_239207_2_.firstPerson()){
                    matrixStackIn.translate(p_239207_2_ == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.2F, -0.2F);
                }else{
                    matrixStackIn.translate(0, 0.6F, 0.0F);
                }
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(160));
                TIDE_TRIDENT_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(RenderTideTrident.TRIDENT)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }

        }
    }
}
