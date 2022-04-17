package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumTroll;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTrollArmor extends ArmorItem {

    public EnumTroll troll;

    public ItemTrollArmor(EnumTroll troll, CustomArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.troll = troll;
        this.setRegistryName(troll.name().toLowerCase(Locale.ROOT) + "_troll_leather_" + getArmorPart(slot));
    }

    public ArmorMaterial getMaterial() {
        return troll.material;
    }


    private String getArmorPart(EquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return "helmet";
            case CHEST:
                return "chestplate";
            case LEGS:
                return "leggings";
            case FEET:
                return "boots";
        }
        return "";
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        return (A) IceAndFire.PROXY.getArmorModel(slot == EquipmentSlot.LEGS ? 7 : 6);
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/armor_troll_" + troll.name().toLowerCase(Locale.ROOT) + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.iceandfire.troll_leather_armor_" + getArmorPart(slot) + ".desc").withStyle(ChatFormatting.GREEN));
    }
}
