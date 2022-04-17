package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class ItemStoneStatue extends Item {

    public ItemStoneStatue() {
        super(new Item.Properties().stacksTo(1));
        this.setRegistryName(IceAndFire.MODID, "stone_statue");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.getTag() != null) {
            boolean isPlayer = stack.getTag().getBoolean("IAFStoneStatuePlayerEntity");
            String id = stack.getTag().getString("IAFStoneStatueEntityID");
            if (EntityType.byString(id).orElse(null) != null) {
                EntityType type = EntityType.byString(id).orElse(null);
                TranslatableComponent untranslated = isPlayer ? new TranslatableComponent("entity.player.name") : new TranslatableComponent(type.getDescriptionId());
                tooltip.add(untranslated.withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Level world, Player player) {
        itemStack.setTag(new CompoundTag());
        itemStack.getTag().putBoolean("IAFStoneStatuePlayerEntity", true);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.FAIL;
        } else {
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            if (stack.getTag() != null) {
                String id = stack.getTag().getString("IAFStoneStatueEntityID");
                CompoundTag statueNBT = stack.getTag().getCompound("IAFStoneStatueNBT");
                EntityStoneStatue statue = new EntityStoneStatue(IafEntityRegistry.STONE_STATUE, context.getLevel());
                statue.readAdditionalSaveData(statueNBT);
                statue.setTrappedEntityTypeString(id);
                double d1 = context.getPlayer().getX() - (context.getClickedPos().getX() + 0.5);
                double d2 = context.getPlayer().getZ() - (context.getClickedPos().getZ() + 0.5);
                float yaw = (float)(Mth.atan2(d2, d1) * (double)(180F / (float)Math.PI)) - 90;
                statue.yRotO = yaw;
                statue.yRot = yaw;
                statue.yHeadRot = yaw;
                statue.yBodyRot = yaw;
                statue.yBodyRotO = yaw;
                statue.absMoveTo(context.getClickedPos().getX() + 0.5, context.getClickedPos().getY() + 1, context.getClickedPos().getZ() + 0.5, yaw, 0);
                if (!context.getLevel().isClientSide) {
                    context.getLevel().addFreshEntity(statue);
                    statue.readAdditionalSaveData(stack.getTag());
                }
                statue.setCrackAmount(0);

                if (!context.getPlayer().isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
