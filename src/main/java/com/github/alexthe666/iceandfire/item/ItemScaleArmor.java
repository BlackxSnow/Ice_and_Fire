package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemScaleArmor extends ArmorItem implements IProtectAgainstDragonItem {

    public EnumDragonArmor armor_type;
    public EnumDragonEgg eggType;

    public ItemScaleArmor(EnumDragonEgg eggType, EnumDragonArmor armorType, CustomArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.armor_type = armorType;
        this.eggType = eggType;
    }

    public String getDescriptionId() {
        switch (this.slot){
            case HEAD:
                return "item.iceandfire.dragon_helmet";
            case CHEST:
                return "item.iceandfire.dragon_chestplate";
            case LEGS:
                return "item.iceandfire.dragon_leggings";
            case FEET:
                return "item.iceandfire.dragon_boots";
        }
        return "item.iceandfire.dragon_helmet";
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        int dragonType = DragonType.getIntFromType(armor_type.eggType.dragonType);
        if(dragonType == 0){
            return (A) IceAndFire.PROXY.getArmorModel((slot == EquipmentSlot.LEGS ? 1 : 0));
        }else if(dragonType == 1){
            return (A) IceAndFire.PROXY.getArmorModel((slot == EquipmentSlot.LEGS ? 3 : 2));
        }else if(dragonType == 2){
            return (A) IceAndFire.PROXY.getArmorModel((slot == EquipmentSlot.LEGS ? 19 : 18));
        }else{
            return null;
        }

    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/" + armor_type.name() + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("dragon." + eggType.toString().toLowerCase()).withStyle(eggType.color));
        tooltip.add(new TranslatableComponent("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }
}
