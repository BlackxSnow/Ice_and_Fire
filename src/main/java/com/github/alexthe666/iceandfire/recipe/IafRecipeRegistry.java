package com.github.alexthe666.iceandfire.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityAmphithereArrow;
import com.github.alexthe666.iceandfire.entity.EntityCockatriceEgg;
import com.github.alexthe666.iceandfire.entity.EntityDeathWormEgg;
import com.github.alexthe666.iceandfire.entity.EntityDragonArrow;
import com.github.alexthe666.iceandfire.entity.EntityHippogryphEgg;
import com.github.alexthe666.iceandfire.entity.EntityHydraArrow;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpentArrow;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianArrow;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumSkullType;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.apache.logging.log4j.Level;

public class IafRecipeRegistry extends SimpleJsonResourceReloadListener {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(DragonForgeRecipe.class, new DragonForgeRecipe.Deserializer()).create();
    public static final BannerPattern PATTERN_FIRE = addBanner("fire", new ItemStack(IafItemRegistry.FIRE_DRAGON_HEART));
    public static final BannerPattern PATTERN_ICE = addBanner("ice", new ItemStack(IafItemRegistry.ICE_DRAGON_HEART));
    public static final BannerPattern PATTERN_LIGHTNING = addBanner("lightning", new ItemStack(IafItemRegistry.LIGHTNING_DRAGON_HEART));
    public static final BannerPattern PATTERN_FIRE_HEAD = addBanner("fire_head", new ItemStack(IafItemRegistry.DRAGON_SKULL_FIRE));
    public static final BannerPattern PATTERN_ICE_HEAD = addBanner("ice_head", new ItemStack(IafItemRegistry.DRAGON_SKULL_ICE));
    public static final BannerPattern PATTERN_LIGHTNING_HEAD = addBanner("lightning_head", new ItemStack(IafItemRegistry.DRAGON_SKULL_LIGHTNING));
    public static final BannerPattern PATTERN_AMPHITHERE = addBanner("amphithere", new ItemStack(IafItemRegistry.AMPHITHERE_FEATHER));
    public static final BannerPattern PATTERN_BIRD = addBanner("bird", new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER));
    public static final BannerPattern PATTERN_EYE = addBanner("eye", new ItemStack(IafItemRegistry.CYCLOPS_EYE));
    public static final BannerPattern PATTERN_FAE = addBanner("fae", new ItemStack(IafItemRegistry.PIXIE_WINGS));
    public static final BannerPattern PATTERN_FEATHER = addBanner("feather", new ItemStack(Items.FEATHER));
    public static final BannerPattern PATTERN_GORGON = addBanner("gorgon", new ItemStack(IafItemRegistry.GORGON_HEAD));
    public static final BannerPattern PATTERN_HIPPOCAMPUS = addBanner("hippocampus", new ItemStack(IafItemRegistry.HIPPOCAMPUS_FIN));
    public static final BannerPattern PATTERN_HIPPOGRYPH_HEAD = addBanner("hippogryph_head", new ItemStack(EnumSkullType.HIPPOGRYPH.skull_item));
    public static final BannerPattern PATTERN_MERMAID = addBanner("mermaid", new ItemStack(IafItemRegistry.SIREN_TEAR));
    public static final BannerPattern PATTERN_SEA_SERPENT = addBanner("sea_serpent", new ItemStack(IafItemRegistry.SERPENT_FANG));
    public static final BannerPattern PATTERN_TROLL = addBanner("troll", new ItemStack(IafItemRegistry.TROLL_TUSK));
    public static final BannerPattern PATTERN_WEEZER = addBanner("weezer", new ItemStack(IafItemRegistry.WEEZER_BLUE_ALBUM));
    public static final BannerPattern PATTERN_DREAD = addBanner("dread", new ItemStack(IafItemRegistry.DREAD_SHARD));
    public static List<DragonForgeRecipe> ALL_FORGE_RECIPES = new ArrayList<>();
    public static List<DragonForgeRecipe> FIRE_FORGE_RECIPES = new ArrayList<>();
    public static List<DragonForgeRecipe> ICE_FORGE_RECIPES = new ArrayList<>();
    public static List<DragonForgeRecipe> LIGHTNING_FORGE_RECIPES = new ArrayList<>();
    public static List<ItemStack> BANNER_ITEMS = new ArrayList<>();

    public IafRecipeRegistry() {
        super(GSON, "dragonforge_recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        ImmutableMap.Builder<ResourceLocation, DragonForgeRecipe> builder = ImmutableMap.builder();
        ALL_FORGE_RECIPES.clear();
        IceAndFire.LOGGER.log(Level.ALL, "Loading in dragonforge_recipes jsons...");
        splashList.forEach((p_223385_1_, p_223385_2_) -> {
            try {
                DragonForgeRecipe fold = GSON.fromJson(p_223385_2_, DragonForgeRecipe.class);
                builder.put(p_223385_1_, fold);
            } catch (Exception exception) {
                IceAndFire.LOGGER.error("Couldn't parse dragonforge recipe {}", p_223385_1_, exception);
            }
        });
        ImmutableMap<ResourceLocation, DragonForgeRecipe> immutablemap = builder.build();
        immutablemap.forEach((p_215305_2_, p_215305_3_) -> {
            ALL_FORGE_RECIPES.add((DragonForgeRecipe)p_215305_3_);
        });
        FIRE_FORGE_RECIPES.clear();
        ICE_FORGE_RECIPES.clear();
        LIGHTNING_FORGE_RECIPES.clear();
        for(DragonForgeRecipe recipe : ALL_FORGE_RECIPES){
            if(recipe.getDragonType().equals("fire")){
                FIRE_FORGE_RECIPES.add(recipe);
            }
            if(recipe.getDragonType().equals("ice")){
                ICE_FORGE_RECIPES.add(recipe);
            }
            if(recipe.getDragonType().equals("lightning")){
                LIGHTNING_FORGE_RECIPES.add(recipe);
            }
        }
    }

    public static void preInit() {
        DispenserBlock.registerBehavior(IafItemRegistry.STYMPHALIAN_ARROW, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityStymphalianArrow entityarrow = new EntityStymphalianArrow(IafEntityRegistry.STYMPHALIAN_ARROW, worldIn, position.x(), position.y(), position.z());
                entityarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.AMPHITHERE_ARROW, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityAmphithereArrow entityarrow = new EntityAmphithereArrow(IafEntityRegistry.AMPHITHERE_ARROW, worldIn, position.x(), position.y(), position.z());
                entityarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.SEA_SERPENT_ARROW, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntitySeaSerpentArrow entityarrow = new EntitySeaSerpentArrow(IafEntityRegistry.SEA_SERPENT_ARROW, worldIn, position.x(), position.y(), position.z());
                entityarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.DRAGONBONE_ARROW, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityDragonArrow entityarrow = new EntityDragonArrow(IafEntityRegistry.DRAGON_ARROW, position.x(), position.y(), position.z(), worldIn);
                entityarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.HYDRA_ARROW, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                EntityHydraArrow entityarrow = new EntityHydraArrow(IafEntityRegistry.HYDRA_ARROW, worldIn, position.x(), position.y(), position.z());
                entityarrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return entityarrow;
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.HIPPOGRYPH_EGG, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                return new EntityHippogryphEgg(IafEntityRegistry.HIPPOGRYPH_EGG, worldIn, position.x(), position.y(), position.z(), stackIn);
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.ROTTEN_EGG, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                return new EntityCockatriceEgg(IafEntityRegistry.COCKATRICE_EGG, position.x(), position.y(), position.z(), worldIn);
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.DEATHWORM_EGG, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                return new EntityDeathWormEgg(IafEntityRegistry.DEATH_WORM_EGG, position.x(), position.y(), position.z(), worldIn, false);
            }
        });
        DispenserBlock.registerBehavior(IafItemRegistry.DEATHWORM_EGG_GIGANTIC, new AbstractProjectileDispenseBehavior() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                return new EntityDeathWormEgg(IafEntityRegistry.DEATH_WORM_EGG, position.x(), position.y(), position.z(), worldIn, true);
            }
        });
        IafItemRegistry.BLINDFOLD_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(Items.STRING)));
        IafItemRegistry.SILVER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.SILVER_INGOT)));
        IafItemRegistry.SILVER_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.SILVER_INGOT)));
        IafItemRegistry.DRAGONBONE_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGON_BONE)));
        IafItemRegistry.FIRE_DRAGONBONE_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGON_BONE)));
        IafItemRegistry.ICE_DRAGONBONE_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGON_BONE)));
        for (EnumDragonArmor armor : EnumDragonArmor.values()) {
            armor.armorMaterial.setRepairMaterial(Ingredient.of(new ItemStack(EnumDragonArmor.getScaleItem(armor))));
        }
        IafItemRegistry.DRAGONSTEEL_FIRE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGONSTEEL_FIRE_INGOT)));
        IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGONSTEEL_ICE_INGOT)));
        IafItemRegistry.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT)));
        IafItemRegistry.SHEEP_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(Blocks.WHITE_WOOL)));
        IafItemRegistry.EARPLUGS_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(Blocks.OAK_BUTTON)));
        IafItemRegistry.DEATHWORM_0_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DEATH_WORM_CHITIN_YELLOW)));
        IafItemRegistry.DEATHWORM_1_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DEATH_WORM_CHITIN_RED)));
        IafItemRegistry.DEATHWORM_2_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DEATH_WORM_CHITIN_WHITE)));
        IafItemRegistry.TROLL_WEAPON_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(Blocks.STONE)));
        IafItemRegistry.TROLL_MOUNTAIN_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(EnumTroll.MOUNTAIN.leather)));
        IafItemRegistry.TROLL_FOREST_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(EnumTroll.FOREST.leather)));
        IafItemRegistry.TROLL_FROST_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(EnumTroll.FROST.leather)));
        IafItemRegistry.HIPPOGRYPH_SWORD_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.HIPPOGRYPH_TALON)));
        IafItemRegistry.HIPPOCAMPUS_SWORD_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.SHINY_SCALES)));
        IafItemRegistry.AMPHITHERE_SWORD_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.AMPHITHERE_FEATHER)));
        IafItemRegistry.DRAGONSTEEL_FIRE_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGONSTEEL_FIRE_INGOT)));
        IafItemRegistry.DRAGONSTEEL_ICE_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGONSTEEL_ICE_INGOT)));
        IafItemRegistry.DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT)));
        IafItemRegistry.STYMHALIAN_SWORD_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER)));
        IafItemRegistry.MYRMEX_CHITIN_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.MYRMEX_DESERT_CHITIN)));
        IafItemRegistry.MYRMEX_DESERT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.MYRMEX_DESERT_CHITIN)));
        IafItemRegistry.MYRMEX_JUNGLE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_CHITIN)));
        IafItemRegistry.DREAD_SWORD_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DREAD_SHARD)));
        IafItemRegistry.DREAD_KNIGHT_TOOL_MATERIAL.setRepairMaterial(Ingredient.of(new ItemStack(IafItemRegistry.DREAD_SHARD)));
        for (EnumSeaSerpent serpent : EnumSeaSerpent.values()) {
            serpent.armorMaterial.setRepairMaterial(Ingredient.of(new ItemStack(serpent.scale)));
        }
        BrewingRecipeRegistry.addRecipe(Ingredient.of(createPotion(Potions.WATER).getItem()), Ingredient.of(IafItemRegistry.SHINY_SCALES), createPotion(Potions.WATER_BREATHING));
    }

    public static ItemStack createPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    public static BannerPattern addBanner(String name, ItemStack craftingStack) {
        return BannerPattern.create(name.toUpperCase(), name, "iceandfire." + name, true);
    }

    public static DragonForgeRecipe getFireForgeRecipe(ItemStack stack) {
        for (DragonForgeRecipe recipe : FIRE_FORGE_RECIPES) {
            if (recipe.getInput().test(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static DragonForgeRecipe getIceForgeRecipe(ItemStack stack) {
        for (DragonForgeRecipe recipe : ICE_FORGE_RECIPES) {
            if (recipe.getInput().test(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static DragonForgeRecipe getLightningForgeRecipe(ItemStack stack) {
        for (DragonForgeRecipe recipe : LIGHTNING_FORGE_RECIPES) {
            if (recipe.getInput().test(stack)) {
                return recipe;
            }
        }
        return null;
    }


    public static DragonForgeRecipe getFireForgeRecipeForBlood(ItemStack stack) {
        for (DragonForgeRecipe recipe : FIRE_FORGE_RECIPES) {
            if (recipe.getBlood().test(stack)) {
                return recipe;
            }
        }
        return null;
    }


    public static DragonForgeRecipe getIceForgeRecipeForBlood(ItemStack stack) {
        for (DragonForgeRecipe recipe : ICE_FORGE_RECIPES) {
            if (recipe.getBlood().test(stack)) {
                return recipe;
            }
        }
        return null;
    }

    public static DragonForgeRecipe getLightningForgeRecipeForBlood(ItemStack stack) {
        for (DragonForgeRecipe recipe : LIGHTNING_FORGE_RECIPES) {
            if (recipe.getBlood().test(stack)) {
                return recipe;
            }
        }
        return null;
    }
}
