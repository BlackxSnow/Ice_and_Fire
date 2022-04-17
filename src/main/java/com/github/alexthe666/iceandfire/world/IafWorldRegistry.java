package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.feature.*;
import com.github.alexthe666.iceandfire.world.gen.*;
import com.github.alexthe666.iceandfire.world.structure.DreadMausoleumStructure;
import com.github.alexthe666.iceandfire.world.structure.DummyPiece;
import com.github.alexthe666.iceandfire.world.structure.GorgonTempleStructure;
import com.github.alexthe666.iceandfire.world.structure.GraveyardStructure;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.data.worldgen.Features;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class IafWorldRegistry {

    public static Feature<NoneFeatureConfiguration> FIRE_DRAGON_ROOST;
    public static Feature<NoneFeatureConfiguration> ICE_DRAGON_ROOST;
    public static Feature<NoneFeatureConfiguration> LIGHTNING_DRAGON_ROOST;
    public static Feature<NoneFeatureConfiguration> FIRE_DRAGON_CAVE;
    public static Feature<NoneFeatureConfiguration> ICE_DRAGON_CAVE;
    public static Feature<NoneFeatureConfiguration> LIGHTNING_DRAGON_CAVE;
    public static Feature<NoneFeatureConfiguration> CYCLOPS_CAVE;
    public static Feature<NoneFeatureConfiguration> PIXIE_VILLAGE;
    public static Feature<NoneFeatureConfiguration> SIREN_ISLAND;
    public static Feature<NoneFeatureConfiguration> HYDRA_CAVE;
    public static Feature<NoneFeatureConfiguration> MYRMEX_HIVE_DESERT;
    public static Feature<NoneFeatureConfiguration> MYRMEX_HIVE_JUNGLE;
    public static Feature<NoneFeatureConfiguration> SPAWN_DEATH_WORM;
    public static Feature<NoneFeatureConfiguration> SPAWN_DRAGON_SKELETON_L;
    public static Feature<NoneFeatureConfiguration> SPAWN_DRAGON_SKELETON_F;
    public static Feature<NoneFeatureConfiguration> SPAWN_DRAGON_SKELETON_I;
    public static Feature<NoneFeatureConfiguration> SPAWN_HIPPOCAMPUS;
    public static Feature<NoneFeatureConfiguration> SPAWN_SEA_SERPENT;
    public static Feature<NoneFeatureConfiguration> SPAWN_STYMPHALIAN_BIRD;
    public static Feature<NoneFeatureConfiguration> SPAWN_WANDERING_CYCLOPS;
    public static StructurePieceType DUMMY_PIECE;
    public static StructureFeature<NoneFeatureConfiguration> MAUSOLEUM = new DreadMausoleumStructure(NoneFeatureConfiguration.CODEC);
    public static StructureFeature<NoneFeatureConfiguration> GORGON_TEMPLE = new GorgonTempleStructure(NoneFeatureConfiguration.CODEC);
    public static StructureFeature<NoneFeatureConfiguration> GRAVEYARD = new GraveyardStructure(NoneFeatureConfiguration.CODEC);
    public static ConfiguredFeature FIRE_LILY_CF;
    public static ConfiguredFeature FROST_LILY_CF;
    public static ConfiguredFeature LIGHTNING_LILY_CF;
    public static ConfiguredFeature COPPER_ORE_CF;
    public static ConfiguredFeature SILVER_ORE_CF;
    public static ConfiguredFeature SAPPHIRE_ORE_CF;
    public static ConfiguredFeature AMETHYST_ORE_CF;
    public static ConfiguredFeature FIRE_DRAGON_ROOST_CF;
    public static ConfiguredFeature ICE_DRAGON_ROOST_CF;
    public static ConfiguredFeature LIGHTNING_DRAGON_ROOST_CF;
    public static ConfiguredFeature FIRE_DRAGON_CAVE_CF;
    public static ConfiguredFeature ICE_DRAGON_CAVE_CF;
    public static ConfiguredFeature LIGHTNING_DRAGON_CAVE_CF;
    public static ConfiguredFeature CYCLOPS_CAVE_CF;
    public static ConfiguredFeature PIXIE_VILLAGE_CF;
    public static ConfiguredFeature SIREN_ISLAND_CF;
    public static ConfiguredFeature HYDRA_CAVE_CF;
    public static ConfiguredFeature MYRMEX_HIVE_DESERT_CF;
    public static ConfiguredFeature MYRMEX_HIVE_JUNGLE_CF;
    public static ConfiguredFeature SPAWN_DEATH_WORM_CF;
    public static ConfiguredFeature SPAWN_DRAGON_SKELETON_L_CF;
    public static ConfiguredFeature SPAWN_DRAGON_SKELETON_F_CF;
    public static ConfiguredFeature SPAWN_DRAGON_SKELETON_I_CF;
    public static ConfiguredFeature SPAWN_HIPPOCAMPUS_CF;
    public static ConfiguredFeature SPAWN_SEA_SERPENT_CF;
    public static ConfiguredFeature SPAWN_STYMPHALIAN_BIRD_CF;
    public static ConfiguredFeature SPAWN_WANDERING_CYCLOPS_CF;
    public static ConfiguredStructureFeature GORGON_TEMPLE_CF;
    public static ConfiguredStructureFeature MAUSOLEUM_CF;
    public static ConfiguredStructureFeature GRAVEYARD_CF;
    public static List<Feature<?>> featureList = new ArrayList<>();
    public static List<StructureFeature<?>> structureFeatureList = new ArrayList<>();


    public static void register() {
        FIRE_DRAGON_ROOST = registerFeature("iceandfire:fire_dragon_roost", new WorldGenFireDragonRoosts(NoneFeatureConfiguration.CODEC));
        ICE_DRAGON_ROOST = registerFeature("iceandfire:ice_dragon_roost", new WorldGenIceDragonRoosts(NoneFeatureConfiguration.CODEC));
        LIGHTNING_DRAGON_ROOST = registerFeature("iceandfire:lightning_dragon_roost", new WorldGenLightningDragonRoosts(NoneFeatureConfiguration.CODEC));
        FIRE_DRAGON_CAVE = registerFeature("iceandfire:fire_dragon_cave", new WorldGenFireDragonCave(NoneFeatureConfiguration.CODEC));
        ICE_DRAGON_CAVE = registerFeature("iceandfire:ice_dragon_cave", new WorldGenIceDragonCave(NoneFeatureConfiguration.CODEC));
        LIGHTNING_DRAGON_CAVE = registerFeature("iceandfire:lightning_dragon_cave", new WorldGenLightningDragonCave(NoneFeatureConfiguration.CODEC));
        CYCLOPS_CAVE = registerFeature("iceandfire:cyclops_cave", new WorldGenCyclopsCave(NoneFeatureConfiguration.CODEC));
        PIXIE_VILLAGE = registerFeature("iceandfire:pixie_village", new WorldGenPixieVillage(NoneFeatureConfiguration.CODEC));
        SIREN_ISLAND = registerFeature("iceandfire:siren_island", new WorldGenSirenIsland(NoneFeatureConfiguration.CODEC));
        HYDRA_CAVE = registerFeature("iceandfire:hydra_cave", new WorldGenHydraCave(NoneFeatureConfiguration.CODEC));
        MYRMEX_HIVE_DESERT = registerFeature("iceandfire:myrmex_hive_desert", new WorldGenMyrmexHive(false, false, NoneFeatureConfiguration.CODEC));
        MYRMEX_HIVE_JUNGLE = registerFeature("iceandfire:myrmex_hive_jungle", new WorldGenMyrmexHive(false, true, NoneFeatureConfiguration.CODEC));

        SPAWN_DEATH_WORM = registerFeature("iceandfire:spawn_death_worm", new SpawnDeathWorm(NoneFeatureConfiguration.CODEC));
        SPAWN_DRAGON_SKELETON_L = registerFeature("iceandfire:spawn_dragon_skeleton_l", new SpawnDragonSkeleton(IafEntityRegistry.LIGHTNING_DRAGON, NoneFeatureConfiguration.CODEC));
        SPAWN_DRAGON_SKELETON_F = registerFeature("iceandfire:spawn_dragon_skeleton_f", new SpawnDragonSkeleton(IafEntityRegistry.FIRE_DRAGON, NoneFeatureConfiguration.CODEC));
        SPAWN_DRAGON_SKELETON_I = registerFeature("iceandfire:spawn_dragon_skeleton_i", new SpawnDragonSkeleton(IafEntityRegistry.ICE_DRAGON, NoneFeatureConfiguration.CODEC));
        SPAWN_HIPPOCAMPUS = registerFeature("iceandfire:spawn_hippocampus", new SpawnHippocampus(NoneFeatureConfiguration.CODEC));
        SPAWN_SEA_SERPENT = registerFeature("iceandfire:spawn_sea_serpent", new SpawnSeaSerpent(NoneFeatureConfiguration.CODEC));
        SPAWN_STYMPHALIAN_BIRD = registerFeature("iceandfire:spawn_stymphalian_bird", new SpawnStymphalianBird(NoneFeatureConfiguration.CODEC));
        SPAWN_WANDERING_CYCLOPS = registerFeature("iceandfire:spawn_wandering_cyclops", new SpawnWanderingCyclops(NoneFeatureConfiguration.CODEC));

        // Technically we don't need the piece classes anymore but we should register dummy pieces
        // under the same registry name or else player's will get logspammed by Minecraft in existing worlds.
        DUMMY_PIECE = Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:gorgon_piece", DummyPiece::new);
        Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:mausoleum_piece", DummyPiece::new);
        Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:gorgon_piece_empty", DummyPiece::new);
        Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:graveyard_piece", DummyPiece::new);

        MAUSOLEUM = registerStructureFeature( "iceandfire:mausoleum", MAUSOLEUM);
        putStructureOnAList("iceandfire:mausoleum", MAUSOLEUM);

        GORGON_TEMPLE = registerStructureFeature( "iceandfire:gorgon_temple", GORGON_TEMPLE);
        putStructureOnAList("iceandfire:gorgon_temple", GORGON_TEMPLE);

        GRAVEYARD = registerStructureFeature( "iceandfire:graveyard", GRAVEYARD);
        putStructureOnAList("iceandfire:graveyard", GRAVEYARD);

        addStructureSeperation(NoiseGeneratorSettings.OVERWORLD, GORGON_TEMPLE, new StructureFeatureConfiguration(Math.max(IafConfig.spawnGorgonsChance, 2), Math.max(IafConfig.spawnGorgonsChance / 2, 1), 342226450));
        addStructureSeperation(NoiseGeneratorSettings.OVERWORLD, MAUSOLEUM, new StructureFeatureConfiguration(Math.max(IafConfig.generateMausoleumChance, 2), Math.max(IafConfig.generateMausoleumChance / 2, 1), 342226451));
        addStructureSeperation(NoiseGeneratorSettings.OVERWORLD, GRAVEYARD, new StructureFeatureConfiguration(Math.max(IafConfig.generateGraveyardChance * 3, 2), Math.max(IafConfig.generateGraveyardChance * 3 / 2, 1), 342226440));

        GORGON_TEMPLE_CF = Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, "iceandfire:gorgon_temple", GORGON_TEMPLE.configured(FeatureConfiguration.NONE));
        MAUSOLEUM_CF = Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, "iceandfire:mausoleum", MAUSOLEUM.configured(FeatureConfiguration.NONE));
        GRAVEYARD_CF = Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, "iceandfire:graveyard", GRAVEYARD.configured(FeatureConfiguration.NONE));

        StructureFeature.NOISE_AFFECTING_FEATURES= ImmutableList.<StructureFeature<?>>builder().addAll(StructureFeature.NOISE_AFFECTING_FEATURES).add(GORGON_TEMPLE, MAUSOLEUM, GRAVEYARD).build();

        COPPER_ORE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:copper_ore", Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, IafBlockRegistry.COPPER_ORE.defaultBlockState(), 5)).range(128).squared().count(5));
        SILVER_ORE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:silver_ore", Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, IafBlockRegistry.SILVER_ORE.defaultBlockState(), 8)).range(32).squared().count(2));
        SAPPHIRE_ORE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:sapphire_ore", Feature.EMERALD_ORE.configured(new ReplaceBlockConfiguration(Blocks.STONE.defaultBlockState(), IafBlockRegistry.SAPPHIRE_ORE.defaultBlockState())).decorated(FeatureDecorator.EMERALD_ORE.configured(DecoratorConfiguration.NONE)));
        AMETHYST_ORE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:amythest_ore", Feature.EMERALD_ORE.configured(new ReplaceBlockConfiguration(Blocks.STONE.defaultBlockState(), IafBlockRegistry.AMYTHEST_ORE.defaultBlockState())).decorated(FeatureDecorator.EMERALD_ORE.configured(DecoratorConfiguration.NONE)));
        FIRE_LILY_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:fire_lily", Feature.FLOWER.configured(new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(IafBlockRegistry.FIRE_LILY.defaultBlockState()), new SimpleBlockPlacer()).tries(1).build()).decorated(Features.Decorators.ADD_32).decorated(Features.Decorators.HEIGHTMAP_SQUARE));
        FROST_LILY_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:frost_lily", Feature.FLOWER.configured(new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(IafBlockRegistry.FROST_LILY.defaultBlockState()), new SimpleBlockPlacer()).tries(1).build()).decorated(Features.Decorators.ADD_32).decorated(Features.Decorators.HEIGHTMAP_SQUARE));
        LIGHTNING_LILY_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:lightning_lily", Feature.FLOWER.configured(new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(IafBlockRegistry.LIGHTNING_LILY.defaultBlockState()), new SimpleBlockPlacer()).tries(1).build()).decorated(Features.Decorators.ADD_32).decorated(Features.Decorators.HEIGHTMAP_SQUARE));
        FIRE_DRAGON_ROOST_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:fire_dragon_roost", FIRE_DRAGON_ROOST.configured(FeatureConfiguration.NONE));
        ICE_DRAGON_ROOST_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:ice_dragon_roost", ICE_DRAGON_ROOST.configured(FeatureConfiguration.NONE));
        LIGHTNING_DRAGON_ROOST_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:lightning_dragon_roost", LIGHTNING_DRAGON_ROOST.configured(FeatureConfiguration.NONE));
        FIRE_DRAGON_CAVE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:fire_dragon_cave", FIRE_DRAGON_CAVE.configured(FeatureConfiguration.NONE));
        ICE_DRAGON_CAVE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:ice_dragon_cave", ICE_DRAGON_CAVE.configured(FeatureConfiguration.NONE));
        LIGHTNING_DRAGON_CAVE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:lightning_dragon_cave", LIGHTNING_DRAGON_CAVE.configured(FeatureConfiguration.NONE));
        CYCLOPS_CAVE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:cyclops_cave", CYCLOPS_CAVE.configured(FeatureConfiguration.NONE));
        PIXIE_VILLAGE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:pixie_village", PIXIE_VILLAGE.configured(FeatureConfiguration.NONE));
        SIREN_ISLAND_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:siren_island", SIREN_ISLAND.configured(FeatureConfiguration.NONE));
        HYDRA_CAVE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:hydra_cave", HYDRA_CAVE.configured(FeatureConfiguration.NONE));
        MYRMEX_HIVE_DESERT_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:myrmex_hive_desert", MYRMEX_HIVE_DESERT.configured(FeatureConfiguration.NONE));
        MYRMEX_HIVE_JUNGLE_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:myrmex_hive_jungle", MYRMEX_HIVE_JUNGLE.configured(FeatureConfiguration.NONE));
        
        SPAWN_DEATH_WORM_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_death_worm_misc", SPAWN_DEATH_WORM.configured(FeatureConfiguration.NONE));
        SPAWN_DRAGON_SKELETON_L_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_dragon_skeleton_l_misc", SPAWN_DRAGON_SKELETON_L.configured(FeatureConfiguration.NONE));
        SPAWN_DRAGON_SKELETON_F_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_dragon_skeleton_f_misc", SPAWN_DRAGON_SKELETON_F.configured(FeatureConfiguration.NONE));
        SPAWN_DRAGON_SKELETON_I_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_dragon_skeleton_i_misc", SPAWN_DRAGON_SKELETON_I.configured(FeatureConfiguration.NONE));
        SPAWN_HIPPOCAMPUS_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_hippocampus_misc", SPAWN_HIPPOCAMPUS.configured(FeatureConfiguration.NONE));
        SPAWN_SEA_SERPENT_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_sea_serpent_misc", SPAWN_SEA_SERPENT.configured(FeatureConfiguration.NONE));
        SPAWN_STYMPHALIAN_BIRD_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_stymphalian_bird_misc", SPAWN_STYMPHALIAN_BIRD.configured(FeatureConfiguration.NONE));
        SPAWN_WANDERING_CYCLOPS_CF = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_wandering_cyclops_misc", SPAWN_WANDERING_CYCLOPS.configured(FeatureConfiguration.NONE));
    }

    public static void addStructureSeperation(ResourceKey<NoiseGeneratorSettings> preset, StructureFeature structure, StructureFeatureConfiguration settings) {
        BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(preset).structureSettings().structureConfig().put(structure, settings);
    }

    public static <F extends StructureFeature<?>> void putStructureOnAList(String nameForList, F structure) {
        StructureFeature.STRUCTURES_REGISTRY.put(nameForList.toLowerCase(Locale.ROOT), structure);
    }

    private static Feature<NoneFeatureConfiguration> registerFeature(String registryName, Feature<NoneFeatureConfiguration> feature) {
        featureList.add(feature.setRegistryName(registryName));
        return feature;
    }


    private static StructureFeature<NoneFeatureConfiguration> registerStructureFeature(String registryName, StructureFeature<NoneFeatureConfiguration> feature) {
        structureFeatureList.add(feature);
        return feature;
    }

    public static void setup() {
    }

    public static boolean isFarEnoughFromSpawn(LevelAccessor world, BlockPos pos) {
        BlockPos spawnRelative = new BlockPos(0, pos.getY(), 0);
        boolean spawnCheck = !spawnRelative.closerThan(pos, IafConfig.dangerousWorldGenDistanceLimit);
        return spawnCheck;
    }

    public static boolean isDimensionListedForFeatures(ServerLevelAccessor world) {
        ResourceLocation name = world.getLevel().dimension().location();
        if (name == null) {
            return false;
        }
        if (IafConfig.useDimensionBlackList) {
            for (String blacklisted : IafConfig.featureBlacklistedDimensions) {
                if (name.toString().equals(blacklisted)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String whitelist : IafConfig.featureWhitelistedDimensions) {
                if (name.toString().equals(whitelist)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isDimensionListedForDragons(ServerLevelAccessor world) {
        ResourceLocation name = world.getLevel().dimension().location();
        if (name == null) {
            return false;
        }
        if (IafConfig.useDimensionBlackList) {
            for (String blacklisted : IafConfig.dragonBlacklistedDimensions) {
                if (name.toString().equals(blacklisted)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String whitelist : IafConfig.dragonWhitelistedDimensions) {
                if (name.toString().equals(whitelist)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isDimensionListedForMobs(ServerLevelAccessor world) {
        ResourceLocation name = world.getLevel().dimension().location();
        if (name == null) {
            return false;
        }
        if (IafConfig.useDimensionBlackList) {
            for (String blacklisted : IafConfig.mobBlacklistedDimensions) {
                if (name.toString().equals(blacklisted)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String whitelist : IafConfig.mobWhitelistedDimensions) {
                if (name.toString().equals(whitelist)) {
                    return true;
                }
            }
            return false;
        }
    }
    public static boolean isFarEnoughFromDangerousGen(ServerLevelAccessor world, BlockPos pos) {
        boolean canGen = true;
        IafWorldData data = IafWorldData.get(world.getLevel());
        if (data != null) {
            BlockPos last = data.lastGeneratedDangerousStructure;
            canGen = last.distSqr(pos) > IafConfig.dangerousWorldGenSeparationLimit * IafConfig.dangerousWorldGenSeparationLimit;
            if (canGen) {
                data.setLastGeneratedDangerousStructure(pos);
            }

        }
        return canGen;
    }

    public static HashMap<String, Boolean> LOADED_FEATURES;
    static {
    	LOADED_FEATURES = new HashMap<String, Boolean>();
    	LOADED_FEATURES.put("FIRE_LILY_CF", false);
    	LOADED_FEATURES.put("FROST_LILY_CF", false);
    	LOADED_FEATURES.put("LIGHTNING_LILY_CF", false);
    	LOADED_FEATURES.put("COPPER_ORE_CF", false);
    	LOADED_FEATURES.put("SILVER_ORE_CF", false);
    	LOADED_FEATURES.put("SAPPHIRE_ORE_CF", false);
    	LOADED_FEATURES.put("AMETHYST_ORE_CF", false);
    	LOADED_FEATURES.put("FIRE_DRAGON_ROOST_CF", false);
    	LOADED_FEATURES.put("ICE_DRAGON_ROOST_CF", false);
    	LOADED_FEATURES.put("LIGHTNING_DRAGON_ROOST_CF", false);
    	LOADED_FEATURES.put("FIRE_DRAGON_CAVE_CF", false);
    	LOADED_FEATURES.put("ICE_DRAGON_CAVE_CF", false);
    	LOADED_FEATURES.put("LIGHTNING_DRAGON_CAVE_CF", false);
    	LOADED_FEATURES.put("CYCLOPS_CAVE_CF", false);
    	LOADED_FEATURES.put("PIXIE_VILLAGE_CF", false);
    	LOADED_FEATURES.put("SIREN_ISLAND_CF", false);
    	LOADED_FEATURES.put("HYDRA_CAVE_CF", false);
    	LOADED_FEATURES.put("MYRMEX_HIVE_DESERT_CF", false);
    	LOADED_FEATURES.put("MYRMEX_HIVE_JUNGLE_CF", false);
    	LOADED_FEATURES.put("SPAWN_DEATH_WORM_CF", false);
    	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_L_CF", false);
    	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_F_CF", false);
    	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_I_CF", false);
    	LOADED_FEATURES.put("SPAWN_HIPPOCAMPUS_CF", false);
    	LOADED_FEATURES.put("SPAWN_SEA_SERPENT_CF", false);
    	LOADED_FEATURES.put("SPAWN_STYMPHALIAN_BIRD_CF", false);
    	LOADED_FEATURES.put("SPAWN_WANDERING_CYCLOPS_CF", false);
    	LOADED_FEATURES.put("GORGON_TEMPLE_CF", false);
    	LOADED_FEATURES.put("MAUSOLEUM_CF", false);
    	LOADED_FEATURES.put("GRAVEYARD_CF", false);
    }
    public static void onBiomesLoad(BiomeLoadingEvent event) {
    	Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());

    	if (safelyTestBiome(BiomeConfig.fireLilyBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FIRE_LILY_CF);
        	LOADED_FEATURES.put("FIRE_LILY_CF", true);
        }
    	if (safelyTestBiome(BiomeConfig.lightningLilyBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, LIGHTNING_LILY_CF);
        	LOADED_FEATURES.put("LIGHTNING_LILY_CF", true);
        }
    	if (safelyTestBiome(BiomeConfig.iceLilyBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FROST_LILY_CF);
        	LOADED_FEATURES.put("FROST_LILY_CF", true);
        }
    	if (safelyTestBiome(BiomeConfig.oreGenBiomes, biome)) {
            if (IafConfig.generateSilverOre) {
                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SILVER_ORE_CF);
            	LOADED_FEATURES.put("SILVER_ORE_CF", true);
            }
            if (IafConfig.generateCopperOre) {
                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, COPPER_ORE_CF);
            	LOADED_FEATURES.put("COPPER_ORE_CF", true);
            }
        }
        if (IafConfig.generateSapphireOre && safelyTestBiome(BiomeConfig.sapphireBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, SAPPHIRE_ORE_CF);
        	LOADED_FEATURES.put("SAPPHIRE_ORE_CF", true);
        }
        if (IafConfig.generateAmythestOre && safelyTestBiome(BiomeConfig.amethystBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AMETHYST_ORE_CF);
        	LOADED_FEATURES.put("AMETHYST_ORE_CF", true);
        }
        if (IafConfig.generateDragonRoosts) {
            if (safelyTestBiome(BiomeConfig.fireDragonBiomes, biome)) {
                event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, FIRE_DRAGON_ROOST_CF);
                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, FIRE_DRAGON_CAVE_CF);
            	LOADED_FEATURES.put("FIRE_DRAGON_ROOST_CF", true);
            	LOADED_FEATURES.put("FIRE_DRAGON_CAVE_CF", true);
            }
            if (safelyTestBiome(BiomeConfig.lightningDragonBiomes, biome)) {
                event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, LIGHTNING_DRAGON_ROOST_CF);
                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, LIGHTNING_DRAGON_CAVE_CF);
            	LOADED_FEATURES.put("LIGHTNING_DRAGON_ROOST_CF", true);
            	LOADED_FEATURES.put("LIGHTNING_DRAGON_CAVE_CF", true);
            }
            if (safelyTestBiome(BiomeConfig.iceDragonBiomes, biome)) {
                event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ICE_DRAGON_ROOST_CF);
                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, ICE_DRAGON_CAVE_CF);
            	LOADED_FEATURES.put("ICE_DRAGON_ROOST_CF", true);
            	LOADED_FEATURES.put("ICE_DRAGON_CAVE_CF", true);
            }
        }
        if (IafConfig.generateCyclopsCaves && safelyTestBiome(BiomeConfig.cyclopsCaveBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, CYCLOPS_CAVE_CF);
        	LOADED_FEATURES.put("CYCLOPS_CAVE_CF", true);
        }
        if (IafConfig.spawnGorgons && safelyTestBiome(BiomeConfig.gorgonTempleBiomes, biome)) {
            event.getGeneration().addStructureStart(GORGON_TEMPLE_CF);
        	LOADED_FEATURES.put("GORGON_TEMPLE_CF", true);
        }
        if (IafConfig.spawnPixies && safelyTestBiome(BiomeConfig.pixieBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, PIXIE_VILLAGE_CF);
        }
        if (IafConfig.generateHydraCaves && safelyTestBiome(BiomeConfig.hydraBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, HYDRA_CAVE_CF);
        	LOADED_FEATURES.put("HYDRA_CAVE_CF", true);
        }
        if (IafConfig.generateMausoleums && safelyTestBiome(BiomeConfig.mausoleumBiomes, biome)) {
            event.getGeneration().addStructureStart(MAUSOLEUM_CF);
        	LOADED_FEATURES.put("MAUSOLEUM_CF", true);
        }
        if (IafConfig.generateGraveyards && safelyTestBiome(BiomeConfig.graveyardBiomes, biome)) {
            event.getGeneration().addStructureStart(GRAVEYARD_CF);
        	LOADED_FEATURES.put("GRAVEYARD_CF", true);
        }
        if (IafConfig.generateMyrmexColonies && safelyTestBiome(BiomeConfig.desertMyrmexBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MYRMEX_HIVE_DESERT_CF);
        	LOADED_FEATURES.put("MYRMEX_HIVE_DESERT_CF", true);
        }
        if (IafConfig.generateMyrmexColonies && safelyTestBiome(BiomeConfig.jungleMyrmexBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, MYRMEX_HIVE_JUNGLE_CF);
        	LOADED_FEATURES.put("MYRMEX_HIVE_JUNGLE_CF", true);
        }
        if (IafConfig.generateSirenIslands && safelyTestBiome(BiomeConfig.sirenBiomes, biome)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SIREN_ISLAND_CF);
        	LOADED_FEATURES.put("SIREN_ISLAND_CF", true);
        }
    	if (IafConfig.spawnDeathWorm && safelyTestBiome(BiomeConfig.deathwormBiomes, biome)) {
    		event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_DEATH_WORM_CF);
        	LOADED_FEATURES.put("SPAWN_DEATH_WORM_CF", true);
    	}
        if (IafConfig.generateWanderingCyclops && safelyTestBiome(BiomeConfig.wanderingCyclopsBiomes, biome)) {
        	event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_WANDERING_CYCLOPS_CF);
        	LOADED_FEATURES.put("SPAWN_WANDERING_CYCLOPS_CF", true);
        }
        if (IafConfig.generateDragonSkeletons) {
            if (safelyTestBiome(BiomeConfig.lightningDragonSkeletonBiomes, biome)) {
        		event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_DRAGON_SKELETON_L_CF);
            	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_L_CF", true);
            }
            if (safelyTestBiome(BiomeConfig.fireDragonSkeletonBiomes, biome)) {
        		event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_DRAGON_SKELETON_F_CF);
            	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_F_CF", true);
            }
            if (safelyTestBiome(BiomeConfig.iceDragonSkeletonBiomes, biome)) {
            	event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_DRAGON_SKELETON_I_CF);
            	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_I_CF", true);
            }
        }
    	if (IafConfig.spawnHippocampus && safelyTestBiome(BiomeConfig.hippocampusBiomes, biome)) {
    		event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_HIPPOCAMPUS_CF);
        	LOADED_FEATURES.put("SPAWN_HIPPOCAMPUS_CF", true);
    	}
        if (IafConfig.spawnSeaSerpents && safelyTestBiome(BiomeConfig.seaSerpentBiomes, biome)) {
        	event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_SEA_SERPENT_CF);
        	LOADED_FEATURES.put("SPAWN_SEA_SERPENT_CF", true);
        }
        if (IafConfig.spawnStymphalianBirds && safelyTestBiome(BiomeConfig.stymphalianBiomes, biome)) {
        	event.getGeneration().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, SPAWN_STYMPHALIAN_BIRD_CF);
        	LOADED_FEATURES.put("SPAWN_STYMPHALIAN_BIRD_CF", true);
        }
    }

    private static boolean safelyTestBiome(Pair<String, SpawnBiomeData> entry, Biome biome){
        try{
            return BiomeConfig.test(entry, biome);
        }catch (Exception e){
            return false;
        }
    }
}
