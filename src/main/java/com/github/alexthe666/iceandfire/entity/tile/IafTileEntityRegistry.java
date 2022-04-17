package com.github.alexthe666.iceandfire.entity.tile;

import java.lang.reflect.Field;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafTileEntityRegistry {
    public static final BlockEntityType<TileEntityLectern> IAF_LECTERN = registerTileEntity(BlockEntityType.Builder.of(TileEntityLectern::new, IafBlockRegistry.LECTERN), "iaf_lectern");
    public static final BlockEntityType<TileEntityPodium> PODIUM = registerTileEntity(BlockEntityType.Builder.of(TileEntityPodium::new, IafBlockRegistry.PODIUM_OAK, IafBlockRegistry.PODIUM_BIRCH, IafBlockRegistry.PODIUM_SPRUCE, IafBlockRegistry.PODIUM_JUNGLE, IafBlockRegistry.PODIUM_DARK_OAK, IafBlockRegistry.PODIUM_ACACIA), "podium");
    public static final BlockEntityType<TileEntityEggInIce> EGG_IN_ICE = registerTileEntity(BlockEntityType.Builder.of(TileEntityEggInIce::new, IafBlockRegistry.EGG_IN_ICE), "egg_in_ice");
    public static final BlockEntityType<TileEntityPixieHouse> PIXIE_HOUSE = registerTileEntity(BlockEntityType.Builder.of(TileEntityPixieHouse::new, IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_RED, IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_BROWN, IafBlockRegistry.PIXIE_HOUSE_OAK, IafBlockRegistry.PIXIE_HOUSE_BIRCH, IafBlockRegistry.PIXIE_HOUSE_BIRCH, IafBlockRegistry.PIXIE_HOUSE_SPRUCE, IafBlockRegistry.PIXIE_HOUSE_DARK_OAK), "pixie_house");
    public static final BlockEntityType<TileEntityJar> PIXIE_JAR = registerTileEntity(BlockEntityType.Builder.of(TileEntityJar::new, IafBlockRegistry.JAR_EMPTY, IafBlockRegistry.JAR_PIXIE_0, IafBlockRegistry.JAR_PIXIE_1, IafBlockRegistry.JAR_PIXIE_2, IafBlockRegistry.JAR_PIXIE_3, IafBlockRegistry.JAR_PIXIE_4), "pixie_jar");
    public static final BlockEntityType<TileEntityMyrmexCocoon> MYRMEX_COCOON = registerTileEntity(BlockEntityType.Builder.of(TileEntityMyrmexCocoon::new, IafBlockRegistry.DESERT_MYRMEX_COCOON, IafBlockRegistry.JUNGLE_MYRMEX_COCOON), "myrmex_cocoon");
    public static final BlockEntityType<TileEntityDragonforge> DRAGONFORGE_CORE = registerTileEntity(BlockEntityType.Builder.of(TileEntityDragonforge::new, IafBlockRegistry.DRAGONFORGE_FIRE_CORE, IafBlockRegistry.DRAGONFORGE_ICE_CORE, IafBlockRegistry.DRAGONFORGE_FIRE_CORE_DISABLED, IafBlockRegistry.DRAGONFORGE_ICE_CORE_DISABLED, IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE, IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE_DISABLED), "dragonforge_core");
    public static final BlockEntityType<TileEntityDragonforgeBrick> DRAGONFORGE_BRICK = registerTileEntity(BlockEntityType.Builder.of(TileEntityDragonforgeBrick::new, IafBlockRegistry.DRAGONFORGE_FIRE_BRICK, IafBlockRegistry.DRAGONFORGE_ICE_BRICK, IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK), "dragonforge_brick");
    public static final BlockEntityType<TileEntityDragonforgeInput> DRAGONFORGE_INPUT = registerTileEntity(BlockEntityType.Builder.of(TileEntityDragonforgeInput::new, IafBlockRegistry.DRAGONFORGE_FIRE_INPUT, IafBlockRegistry.DRAGONFORGE_ICE_INPUT, IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT), "dragonforge_input");
    public static final BlockEntityType<TileEntityDreadPortal> DREAD_PORTAL = registerTileEntity(BlockEntityType.Builder.of(TileEntityDreadPortal::new, IafBlockRegistry.DREAD_PORTAL), "dread_portal");
    public static final BlockEntityType<TileEntityDreadSpawner> DREAD_SPAWNER = registerTileEntity(BlockEntityType.Builder.of(TileEntityDreadSpawner::new, IafBlockRegistry.DREAD_SPAWNER), "dread_spawner");
    public static final BlockEntityType<TileEntityGhostChest> GHOST_CHEST = registerTileEntity(BlockEntityType.Builder.of(TileEntityGhostChest::new, IafBlockRegistry.GHOST_CHEST), "ghost_chest");


    public static BlockEntityType registerTileEntity(BlockEntityType.Builder builder, String entityName) {
        ResourceLocation nameLoc = new ResourceLocation(IceAndFire.MODID, entityName);
        return (BlockEntityType) builder.build(null).setRegistryName(nameLoc);
    }

    @SubscribeEvent
    public static void registerTileEntities(final RegistryEvent.Register<BlockEntityType<?>> event) {
        try {
            for (Field f : IafTileEntityRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof BlockEntityType) {
                    event.getRegistry().register((BlockEntityType) obj);
                } else if (obj instanceof BlockEntityType[]) {
                    for (BlockEntityType te : (BlockEntityType[]) obj) {
                        event.getRegistry().register(te);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
