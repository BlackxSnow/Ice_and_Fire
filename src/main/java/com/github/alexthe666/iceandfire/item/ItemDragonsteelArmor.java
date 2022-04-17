package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class ItemDragonsteelArmor extends ArmorItem implements IProtectAgainstDragonItem {

    private ArmorMaterial material;
    private Multimap<Attribute, AttributeModifier> attributeModifierMultimap;
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public ItemDragonsteelArmor(ArmorMaterial material, int renderIndex, EquipmentSlot slot, String gameName, String name) {
        super(material, slot, new Item.Properties().tab(IceAndFire.TAB_ITEMS));
        this.material = material;
        this.setRegistryName(IceAndFire.MODID, gameName);
        this.attributeModifierMultimap = createAttributeMap();

    }

    //Workaround for armor attributes being registered before the config gets loaded
    private Multimap<Attribute, AttributeModifier> createAttributeMap(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", material.getDefenseForSlot(slot), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", material.getToughness(), AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double) this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }

    private Multimap<Attribute, AttributeModifier> getOrUpdateAttributeMap(){
        //If the armor values have changed recreate the map
        //There might be a prettier way of accomplishing this but it works
        if (this.attributeModifierMultimap.containsKey(Attributes.ARMOR)
            && !this.attributeModifierMultimap.get(Attributes.ARMOR).isEmpty()
            && this.attributeModifierMultimap.get(Attributes.ARMOR).toArray()[0] instanceof AttributeModifier
            && ((AttributeModifier)this.attributeModifierMultimap.get(Attributes.ARMOR).toArray()[0]).getAmount() != getDefense()
        )
        {
            this.attributeModifierMultimap = createAttributeMap();
        }
        return attributeModifierMultimap;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (this.slot !=null) {
            return (this.getMaterial()).getDurabilityForSlot(this.slot);
        }
        return super.getMaxDamage(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
        int legs = 11;
        int armor = 10;
        if (material == IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL) {
            legs = 13;
            armor = 12;
        }
        if (material == IafItemRegistry.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL) {
            legs = 21;
            armor = 20;
        }
        return (A) IceAndFire.PROXY.getArmorModel(slot == EquipmentSlot.LEGS ? legs : armor);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == this.slot ? getOrUpdateAttributeMap() : super.getDefaultAttributeModifiers(equipmentSlot);
    }
    @Override
    public int getDefense() {
        if (this.material != null)
            return this.material.getDefenseForSlot(this.getSlot());
        return super.getDefense();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (material == IafItemRegistry.DRAGONSTEEL_FIRE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_fire" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        } else if (material == IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_ice" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        } else {
            return "iceandfire:textures/models/armor/armor_dragonsteel_lightning" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        }
    }
}
