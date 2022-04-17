package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.props.MiscProperties;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDeathwormGauntlet extends Item implements ICustomRendered {

    private boolean deathwormReceded = true;
    private boolean deathwormLaunched = false;
    private int specialDamage = 0;

    public ItemDeathwormGauntlet(String color) {
        super(IceAndFire.PROXY.setupISTER(new Item.Properties().durability(500).tab(IceAndFire.TAB_ITEMS)));
        this.setRegistryName(IceAndFire.MODID, "deathworm_gauntlet_" + color);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        playerIn.startUsingItem(hand);
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, itemStackIn);
    }

    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (stack.getTag() != null) {
            if (deathwormReceded || deathwormLaunched) {
                return;
            } else {
                if (player instanceof Player) {
                    if (stack.getTag().getInt("HolderID") != player.getId()) {
                        stack.getTag().putInt("HolderID", player.getId());
                    }
                    if (((Player) player).getCooldowns().getCooldownPercent(this, 0.0F) == 0) {
                        ((Player) player).getCooldowns().addCooldown(this, 10);
                        player.playSound(IafSoundRegistry.DEATHWORM_ATTACK, 1F, 1F);
                        deathwormReceded = false;
                        deathwormLaunched = true;
                    }
                }
            }
        }
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity LivingEntity, int timeLeft) {
        if (specialDamage > 0) {
            stack.hurtAndBreak(specialDamage, LivingEntity, (player) -> {
                player.broadcastBreakEvent(LivingEntity.getUsedItemHand());
            });
            specialDamage = 0;
        }
        if (stack.getTag().getInt("HolderID") != -1) {
            stack.getTag().putInt("HolderID", -1);
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.sameItem(newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        boolean hitMob = false;
        if (stack.getTag() == null) {
            stack.setTag(new CompoundTag());
        } else {
            if (!(entity instanceof LivingEntity))
                return;
            int tempLungeTicks = MiscProperties.getLungeTicks((LivingEntity) entity);
            if (deathwormReceded) {
                if (tempLungeTicks > 0) {
                    tempLungeTicks = tempLungeTicks - 4;
                }
                if (tempLungeTicks <= 0) {
                    tempLungeTicks = 0;
                    deathwormReceded = false;
                    deathwormLaunched = false;
                }
            } else if (deathwormLaunched) {
                tempLungeTicks = 4 + tempLungeTicks;
                if (tempLungeTicks > 20 && !deathwormReceded) {
                    deathwormReceded = true;
                }
            }

            if (MiscProperties.getLungeTicks((LivingEntity) entity) == 20) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    Vec3 Vector3d = player.getViewVector(1.0F).normalize();
                    double range = 5;
                    for (Mob LivingEntity : world.getEntitiesOfClass(Mob.class, new AABB(player.getX() - range, player.getY() - range, player.getZ() - range, player.getX() + range, player.getY() + range, player.getZ() + range))) {
                        Vec3 Vector3d1 = new Vec3(LivingEntity.getX() - player.getX(), LivingEntity.getY() - player.getY(), LivingEntity.getZ() - player.getZ());
                        double d0 = Vector3d1.length();
                        Vector3d1 = Vector3d1.normalize();
                        double d1 = Vector3d.dot(Vector3d1);
                        boolean canSee = d1 > 1.0D - 0.5D / d0 && player.canSee(LivingEntity);
                        if (canSee) {
                            specialDamage++;
                            LivingEntity.hurt(DamageSource.playerAttack((Player) entity), 3F);
                            LivingEntity.knockback(0.5F, LivingEntity.getX() - player.getX(), LivingEntity.getZ() - player.getZ());
                        }
                    }
                }
            }
            MiscProperties.setLungeTicks((LivingEntity) entity, tempLungeTicks);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.deathworm_gauntlet.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.iceandfire.deathworm_gauntlet.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
