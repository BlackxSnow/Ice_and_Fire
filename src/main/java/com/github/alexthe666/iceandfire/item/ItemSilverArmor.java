package com.github.alexthe666.iceandfire.item;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemSilverArmor extends ArmorItem {

    public ItemSilverArmor(ArmorMaterial material, EquipmentSlot slot, String name) {
        super(material, slot, new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, name);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        return (A) IceAndFire.PROXY.getArmorModel(slot == EquipmentSlot.LEGS ? 15 : 14);
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "armor_silver_metal_layer_2" : "armor_silver_metal_layer_1") + ".png";
    }


}