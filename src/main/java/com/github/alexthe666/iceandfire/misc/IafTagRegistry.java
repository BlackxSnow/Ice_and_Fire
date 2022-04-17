package com.github.alexthe666.iceandfire.misc;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class IafTagRegistry {
    public static final TagKey<Item> DRAGON_ARROWS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("iceandfire", "dragon_arrows"));
    public static final TagKey<Block> MYRMEX_HARVESTABLES = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("iceandfire", "myrmex_harvestables"));
    public static final TagKey<EntityType<?>> CHICKENS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "chickens"));
    public static final TagKey<EntityType<?>> FEAR_DRAGONS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "chickens"));
    public static final TagKey<EntityType<?>> SCARES_COCKATRICES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "scares_cockatrices"));
    public static final TagKey<EntityType<?>> SHEEP = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "sheep"));
    public static final TagKey<EntityType<?>> VILLAGERS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "villagers"));
    public static final TagKey<EntityType<?>> ICE_DRAGON_TARGETS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "ice_dragon_targets"));
    public static final TagKey<EntityType<?>> FIRE_DRAGON_TARGETS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "fire_dragon_targets"));
    public static final TagKey<EntityType<?>> LIGHTNING_DRAGON_TARGETS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "lightning_dragon_targets"));
    public static final TagKey<EntityType<?>> CYCLOPS_UNLIFTABLES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("iceandfire", "cyclops_unliftables"));
    public static final TagKey<MobEffect> BLINDED = TagKey.create(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation("iceandfire", "blinded"));

}