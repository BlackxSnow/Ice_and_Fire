package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafEntityRegistry {

    public static final EntityType<EntityDragonPart> DRAGON_MULTIPART = registerEntity(EntityType.Builder.of(EntityDragonPart::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().setCustomClientFactory(EntityDragonPart::new), "dragon_multipart");
    public static final EntityType<EntitSlowPart> SLOW_MULTIPART = registerEntity(EntityType.Builder.of(EntitSlowPart::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().setCustomClientFactory(EntitSlowPart::new), "multipart");
    public static final EntityType<EntityHydraHead> HYDRA_MULTIPART = registerEntity(EntityType.Builder.of(EntityHydraHead::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().setCustomClientFactory(EntityHydraHead::new), "hydra_multipart");
    public static final EntityType<EntityCyclopsEye> CYCLOPS_MULTIPART = registerEntity(EntityType.Builder.of(EntityCyclopsEye::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().setCustomClientFactory(EntityCyclopsEye::new), "cylcops_multipart");
    public static final EntityType<EntityDragonEgg> DRAGON_EGG = registerEntity(EntityType.Builder.of(EntityDragonEgg::new, MobCategory.MISC).sized(0.45F, 0.55F).fireImmune(), "dragon_egg");
    public static final EntityType<EntityDragonArrow> DRAGON_ARROW = registerEntity(EntityType.Builder.of(EntityDragonArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityDragonArrow::new), "dragon_arrow");
    public static final EntityType<EntityDragonSkull> DRAGON_SKULL = registerEntity(EntityType.Builder.of(EntityDragonSkull::new, MobCategory.MISC).sized(0.9F, 0.65F), "dragon_skull");
    public static final EntityType<EntityDragonBase> FIRE_DRAGON = registerEntity(EntityType.Builder.of(EntityFireDragon::new, MobCategory.CREATURE).sized(0.78F, 1.2F).fireImmune().setTrackingRange(256), "fire_dragon");
    public static final EntityType<EntityDragonBase> ICE_DRAGON = registerEntity(EntityType.Builder.of(EntityIceDragon::new, MobCategory.CREATURE).sized(0.78F, 1.2F).setTrackingRange(256), "ice_dragon");
    public static final EntityType<EntityDragonBase> LIGHTNING_DRAGON = registerEntity(EntityType.Builder.of(EntityLightningDragon::new, MobCategory.CREATURE).sized(0.78F, 1.2F).setTrackingRange(256), "lightning_dragon");
    public static final EntityType<EntityDragonFireCharge> FIRE_DRAGON_CHARGE = registerEntity(EntityType.Builder.of(EntityDragonFireCharge::new, MobCategory.MISC).sized(0.9F, 0.9F).setCustomClientFactory(EntityDragonFireCharge::new), "fire_dragon_charge");
    public static final EntityType<EntityDragonIceCharge> ICE_DRAGON_CHARGE = registerEntity(EntityType.Builder.of(EntityDragonIceCharge::new, MobCategory.MISC).sized(0.9F, 0.9F).setCustomClientFactory(EntityDragonIceCharge::new), "ice_dragon_charge");
    public static final EntityType<EntityDragonLightningCharge> LIGHTNING_DRAGON_CHARGE = registerEntity(EntityType.Builder.of(EntityDragonLightningCharge::new, MobCategory.MISC).sized(0.9F, 0.9F).setCustomClientFactory(EntityDragonLightningCharge::new), "lightning_dragon_charge");
    public static final EntityType<EntityHippogryphEgg> HIPPOGRYPH_EGG = registerEntity(EntityType.Builder.of(EntityHippogryphEgg::new, MobCategory.MISC).sized(0.5F, 0.5F), "hippogryph_egg");
    public static final EntityType<EntityHippogryph> HIPPOGRYPH = registerEntity(EntityType.Builder.of(EntityHippogryph::new, MobCategory.CREATURE).sized(1.7F, 1.6F).setTrackingRange(128), "hippogryph");
    public static final EntityType<EntityStoneStatue> STONE_STATUE = registerEntity(EntityType.Builder.of(EntityStoneStatue::new, MobCategory.CREATURE).sized(0.5F, 0.5F), "stone_statue");
    public static final EntityType<EntityGorgon> GORGON = registerEntity(EntityType.Builder.of(EntityGorgon::new, MobCategory.CREATURE).sized(0.8F, 1.99F), "gorgon");
    public static final EntityType<EntityPixie> PIXIE = registerEntity(EntityType.Builder.of(EntityPixie::new, MobCategory.CREATURE).sized(0.4F, 0.8F), "pixie");
    public static final EntityType<EntityCyclops> CYCLOPS = registerEntity(EntityType.Builder.of(EntityCyclops::new, MobCategory.CREATURE).sized(1.95F, 7.4F), "cyclops");
    public static final EntityType<EntitySiren> SIREN = registerEntity(EntityType.Builder.of(EntitySiren::new, MobCategory.CREATURE).sized(1.6F, 0.9F), "siren");
    public static final EntityType<EntityHippocampus> HIPPOCAMPUS = registerEntity(EntityType.Builder.of(EntityHippocampus::new, MobCategory.CREATURE).sized(1.95F, 0.95F), "hippocampus");
    public static final EntityType<EntityDeathWorm> DEATH_WORM = registerEntity(EntityType.Builder.of(EntityDeathWorm::new, MobCategory.CREATURE).sized(0.8F, 0.8F).setTrackingRange(128), "deathworm");
    public static final EntityType<EntityDeathWormEgg> DEATH_WORM_EGG = registerEntity(EntityType.Builder.of(EntityDeathWormEgg::new, MobCategory.MISC).sized(0.5F, 0.5F), "deathworm_egg");
    public static final EntityType<EntityCockatrice> COCKATRICE = registerEntity(EntityType.Builder.of(EntityCockatrice::new, MobCategory.CREATURE).sized(0.95F, 0.95F), "cockatrice");
    public static final EntityType<EntityCockatriceEgg> COCKATRICE_EGG = registerEntity(EntityType.Builder.of(EntityCockatriceEgg::new, MobCategory.MISC).sized(0.5F, 0.5F), "cockatrice_egg");
    public static final EntityType<EntityStymphalianBird> STYMPHALIAN_BIRD = registerEntity(EntityType.Builder.of(EntityStymphalianBird::new, MobCategory.CREATURE).sized(1.3F, 1.2F).setTrackingRange(128), "stymphalian_bird");
    public static final EntityType<EntityStymphalianFeather> STYMPHALIAN_FEATHER = registerEntity(EntityType.Builder.of(EntityStymphalianFeather::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityStymphalianFeather::new), "stymphalian_feather");
    public static final EntityType<EntityStymphalianArrow> STYMPHALIAN_ARROW = registerEntity(EntityType.Builder.of(EntityStymphalianArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityStymphalianArrow::new), "stymphalian_arrow");
    public static final EntityType<EntityTroll> TROLL = registerEntity(EntityType.Builder.of(EntityTroll::new, MobCategory.MONSTER).sized(1.2F, 3.5F), "troll");
    public static final EntityType<EntityMyrmexWorker> MYRMEX_WORKER = registerEntity(EntityType.Builder.of(EntityMyrmexWorker::new, MobCategory.CREATURE).sized(0.9F, 0.9F), "myrmex_worker");
    public static final EntityType<EntityMyrmexSoldier> MYRMEX_SOLDIER = registerEntity(EntityType.Builder.of(EntityMyrmexSoldier::new, MobCategory.CREATURE).sized(0.9F, 0.95F), "myrmex_soldier");
    public static final EntityType<EntityMyrmexSentinel> MYRMEX_SENTINEL = registerEntity(EntityType.Builder.of(EntityMyrmexSentinel::new, MobCategory.CREATURE).sized(1.3F, 1.95F), "myrmex_sentinel");
    public static final EntityType<EntityMyrmexRoyal> MYRMEX_ROYAL = registerEntity(EntityType.Builder.of(EntityMyrmexRoyal::new, MobCategory.CREATURE).sized(1.9F, 1.86F), "myrmex_royal");
    public static final EntityType<EntityMyrmexQueen> MYRMEX_QUEEN = registerEntity(EntityType.Builder.of(EntityMyrmexQueen::new, MobCategory.CREATURE).sized(2.9F, 1.86F), "myrmex_queen");
    public static final EntityType<EntityMyrmexEgg> MYRMEX_EGG = registerEntity(EntityType.Builder.of(EntityMyrmexEgg::new, MobCategory.MISC).sized(0.45F, 0.55F), "myrmex_egg");
    public static final EntityType<EntityAmphithere> AMPHITHERE = registerEntity(EntityType.Builder.of(EntityAmphithere::new, MobCategory.CREATURE).sized(2.5F, 1.25F).setTrackingRange(128), "amphithere");
    public static final EntityType<EntityAmphithereArrow> AMPHITHERE_ARROW = registerEntity(EntityType.Builder.of(EntityAmphithereArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityAmphithereArrow::new), "amphithere_arrow");
    public static final EntityType<EntitySeaSerpent> SEA_SERPENT = registerEntity(EntityType.Builder.of(EntitySeaSerpent::new, MobCategory.CREATURE).sized(0.5F, 0.5F).setTrackingRange(256), "sea_serpent");
    public static final EntityType<EntitySeaSerpentBubbles> SEA_SERPENT_BUBBLES = registerEntity(EntityType.Builder.of(EntitySeaSerpentBubbles::new, MobCategory.MISC).sized(0.9F, 0.9F).setCustomClientFactory(EntitySeaSerpentBubbles::new), "sea_serpent_bubbles");
    public static final EntityType<EntitySeaSerpentArrow> SEA_SERPENT_ARROW = registerEntity(EntityType.Builder.of(EntitySeaSerpentArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntitySeaSerpentArrow::new), "sea_serpent_arrow");
    public static final EntityType<EntityChainTie> CHAIN_TIE = registerEntity(EntityType.Builder.of(EntityChainTie::new, MobCategory.MISC).sized(0.8F, 0.9F), "chain_tie");
    public static final EntityType<EntityPixieCharge> PIXIE_CHARGE = registerEntity(EntityType.Builder.of(EntityPixieCharge::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityPixieCharge::new), "pixie_charge");
    public static final EntityType<EntityMyrmexSwarmer> MYRMEX_SWARMER = registerEntity(EntityType.Builder.of(EntityMyrmexSwarmer::new, MobCategory.CREATURE).sized(0.5F, 0.5F), "myrmex_swarmer");
    public static final EntityType<EntityTideTrident> TIDE_TRIDENT = registerEntity(EntityType.Builder.of(EntityTideTrident::new, MobCategory.MISC).sized(0.85F, 0.5F).setCustomClientFactory(EntityTideTrident::new), "tide_trident");
    public static final EntityType<EntityMobSkull> MOB_SKULL = registerEntity(EntityType.Builder.of(EntityMobSkull::new, MobCategory.MISC).sized(0.85F, 0.85F), "mob_skull");
    public static final EntityType<EntityDreadThrall> DREAD_THRALL = registerEntity(EntityType.Builder.of(EntityDreadThrall::new, MobCategory.CREATURE).sized(0.6F, 1.8F), "dread_thrall");
    public static final EntityType<EntityDreadGhoul> DREAD_GHOUL = registerEntity(EntityType.Builder.of(EntityDreadGhoul::new, MobCategory.CREATURE).sized(0.6F, 1.8F), "dread_ghoul");
    public static final EntityType<EntityDreadBeast> DREAD_BEAST = registerEntity(EntityType.Builder.of(EntityDreadBeast::new, MobCategory.CREATURE).sized(1.2F, 0.9F), "dread_beast");
    public static final EntityType<EntityDreadScuttler> DREAD_SCUTTLER = registerEntity(EntityType.Builder.of(EntityDreadScuttler::new, MobCategory.CREATURE).sized(1.5F, 1.3F), "dread_scuttler");
    public static final EntityType<EntityDreadLich> DREAD_LICH = registerEntity(EntityType.Builder.of(EntityDreadLich::new, MobCategory.CREATURE).sized(0.6F, 1.8F), "dread_lich");
    public static final EntityType<EntityDreadLichSkull> DREAD_LICH_SKULL = registerEntity(EntityType.Builder.of(EntityDreadLichSkull::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityDreadLichSkull::new), "dread_lich_skull");
    public static final EntityType<EntityDreadKnight> DREAD_KNIGHT = registerEntity(EntityType.Builder.of(EntityDreadKnight::new, MobCategory.CREATURE).sized(0.6F, 1.8F), "dread_knight");
    public static final EntityType<EntityDreadHorse> DREAD_HORSE = registerEntity(EntityType.Builder.of(EntityDreadHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F), "dread_horse");
    public static final EntityType<EntityHydra> HYDRA = registerEntity(EntityType.Builder.of(EntityHydra::new, MobCategory.CREATURE).sized(2.8F, 1.39F), "hydra");
    public static final EntityType<EntityHydraBreath> HYDRA_BREATH = registerEntity(EntityType.Builder.of(EntityHydraBreath::new, MobCategory.MISC).sized(0.9F, 0.9F).setCustomClientFactory(EntityHydraBreath::new), "hydra_breath");
    public static final EntityType<EntityHydraArrow> HYDRA_ARROW = registerEntity(EntityType.Builder.of(EntityHydraArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityHydraArrow::new), "hydra_arrow");
    public static final EntityType<EntityGhost> GHOST = registerEntity(EntityType.Builder.of(EntityGhost::new, MobCategory.CREATURE).sized(0.8F, 1.9F).fireImmune(), "ghost");
    public static final EntityType<EntityGhostSword> GHOST_SWORD = registerEntity(EntityType.Builder.of(EntityGhostSword::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityGhostSword::new), "ghost_sword");


    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
        ResourceLocation nameLoc = new ResourceLocation(IceAndFire.MODID, entityName);
        return (EntityType) builder.build(entityName).setRegistryName(nameLoc);
    }

    public static void setup() {
    }

    @SubscribeEvent
    public static void bakeAttributes(EntityAttributeCreationEvent creationEvent){
        creationEvent.put(DRAGON_EGG, EntityDragonEgg.bakeAttributes().build());
        creationEvent.put(DRAGON_SKULL, EntityDragonSkull.bakeAttributes().build());
        creationEvent.put(FIRE_DRAGON, EntityFireDragon.bakeAttributes().build());
        creationEvent.put(ICE_DRAGON, EntityIceDragon.bakeAttributes().build());
        creationEvent.put(LIGHTNING_DRAGON, EntityLightningDragon.bakeAttributes().build());
        creationEvent.put(HIPPOGRYPH, EntityHippogryph.bakeAttributes().build());
        creationEvent.put(GORGON, EntityGorgon.bakeAttributes().build());
        creationEvent.put(STONE_STATUE, EntityStoneStatue.bakeAttributes().build());
        creationEvent.put(PIXIE, EntityPixie.bakeAttributes().build());
        creationEvent.put(CYCLOPS, EntityCyclops.bakeAttributes().build());
        creationEvent.put(SIREN, EntitySiren.bakeAttributes().build());
        creationEvent.put(HIPPOCAMPUS, EntityHippocampus.bakeAttributes().build());
        creationEvent.put(DEATH_WORM, EntityDeathWorm.bakeAttributes().build());
        creationEvent.put(COCKATRICE, EntityCockatrice.bakeAttributes().build());
        creationEvent.put(STYMPHALIAN_BIRD, EntityStymphalianBird.bakeAttributes().build());
        creationEvent.put(TROLL, EntityTroll.bakeAttributes().build());
        creationEvent.put(MYRMEX_WORKER, EntityMyrmexWorker.bakeAttributes().build());
        creationEvent.put(MYRMEX_SOLDIER, EntityMyrmexSoldier.bakeAttributes().build());
        creationEvent.put(MYRMEX_SENTINEL, EntityMyrmexSentinel.bakeAttributes().build());
        creationEvent.put(MYRMEX_ROYAL, EntityMyrmexRoyal.bakeAttributes().build());
        creationEvent.put(MYRMEX_QUEEN, EntityMyrmexQueen.bakeAttributes().build());
        creationEvent.put(MYRMEX_EGG, EntityMyrmexEgg.bakeAttributes().build());
        creationEvent.put(MYRMEX_SWARMER, EntityMyrmexSwarmer.bakeAttributes().build());
        creationEvent.put(AMPHITHERE, EntityAmphithere.bakeAttributes().build());
        creationEvent.put(SEA_SERPENT, EntitySeaSerpent.bakeAttributes().build());
        creationEvent.put(MOB_SKULL, EntityMobSkull.bakeAttributes().build());
        creationEvent.put(DREAD_THRALL, EntityDreadThrall.bakeAttributes().build());
        creationEvent.put(DREAD_LICH, EntityDreadLich.bakeAttributes().build());
        creationEvent.put(DREAD_BEAST, EntityDreadBeast.bakeAttributes().build());
        creationEvent.put(DREAD_HORSE, EntityDreadHorse.bakeAttributes().build());
        creationEvent.put(DREAD_GHOUL, EntityDreadGhoul.bakeAttributes().build());
        creationEvent.put(DREAD_KNIGHT, EntityDreadKnight.bakeAttributes().build());
        creationEvent.put(DREAD_SCUTTLER, EntityDreadScuttler.bakeAttributes().build());
        creationEvent.put(HYDRA, EntityHydra.bakeAttributes().build());
        creationEvent.put(GHOST, EntityGhost.bakeAttributes().build());
    }


    static {
        SpawnPlacements.register(HIPPOGRYPH, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityHippogryph::checkMobSpawnRules);
        SpawnPlacements.register(TROLL, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTroll::canTrollSpawnOn);
        SpawnPlacements.register(DREAD_LICH, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityDreadLich::canLichSpawnOn);
        SpawnPlacements.register(COCKATRICE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCockatrice::checkMobSpawnRules);
        SpawnPlacements.register(AMPHITHERE, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, EntityAmphithere::canAmphithereSpawnOn);
    }
        @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        try {
            for (Field f : IafEntityRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof EntityType) {
                    event.getRegistry().register((EntityType) obj);
                } else if (obj instanceof EntityType[]) {
                    for (EntityType type : (EntityType[]) obj) {
                        event.getRegistry().register(type);

                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //bakeAttributes();
    }

    public static HashMap<String, Boolean> LOADED_ENTITIES;
    static {
    	LOADED_ENTITIES = new HashMap<String, Boolean>();
    	LOADED_ENTITIES.put("HIPPOGRYPH", false);
    	LOADED_ENTITIES.put("DREAD_LICH", false);
    	LOADED_ENTITIES.put("COCKATRICE", false);
    	LOADED_ENTITIES.put("AMPHITHERE", false);
    	LOADED_ENTITIES.put("TROLL_F", false);
    	LOADED_ENTITIES.put("TROLL_S", false);
    	LOADED_ENTITIES.put("TROLL_M", false);
    }
    public static void onBiomesLoad(BiomeLoadingEvent event) {
    	Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());

    	if (IafConfig.spawnHippogryphs && BiomeConfig.test(BiomeConfig.hippogryphBiomes, biome)) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(IafEntityRegistry.HIPPOGRYPH, IafConfig.hippogryphSpawnRate, 1, 1));
            LOADED_ENTITIES.put("HIPPOGRYPH", true);
        }
        if (IafConfig.spawnLiches && BiomeConfig.test(BiomeConfig.mausoleumBiomes, biome)) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(IafEntityRegistry.DREAD_LICH, IafConfig.lichSpawnRate, 1, 1));
            LOADED_ENTITIES.put("DREAD_LICH", true);
        }
        if (IafConfig.spawnCockatrices && BiomeConfig.test(BiomeConfig.cockatriceBiomes, biome)) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(IafEntityRegistry.COCKATRICE, IafConfig.cockatriceSpawnRate, 1, 2));
            LOADED_ENTITIES.put("COCKATRICE", true);
        }
        if (IafConfig.spawnAmphitheres && BiomeConfig.test(BiomeConfig.amphithereBiomes, biome)) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(IafEntityRegistry.AMPHITHERE, IafConfig.amphithereSpawnRate, 1, 3));
            LOADED_ENTITIES.put("AMPHITHERE", true);
        }
        if (IafConfig.spawnTrolls && (
    		BiomeConfig.test(BiomeConfig.forestTrollBiomes, biome) ||
    		BiomeConfig.test(BiomeConfig.snowyTrollBiomes, biome) ||
    		BiomeConfig.test(BiomeConfig.mountainTrollBiomes, biome)
		)) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(IafEntityRegistry.TROLL, IafConfig.trollSpawnRate, 1, 3));
    		if (BiomeConfig.test(BiomeConfig.forestTrollBiomes, biome)) LOADED_ENTITIES.put("TROLL_F", true);
    		if (BiomeConfig.test(BiomeConfig.snowyTrollBiomes, biome)) LOADED_ENTITIES.put("TROLL_S", true); 
    		if (BiomeConfig.test(BiomeConfig.mountainTrollBiomes, biome)) LOADED_ENTITIES.put("TROLL_M", true);
        }
    }
}
