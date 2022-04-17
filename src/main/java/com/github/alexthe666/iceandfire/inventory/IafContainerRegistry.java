package com.github.alexthe666.iceandfire.inventory;

import java.lang.reflect.Field;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafContainerRegistry {

    public static final MenuType IAF_LECTERN_CONTAINER = register(new MenuType(ContainerLectern::new), "iaf_lectern");
    public static final MenuType PODIUM_CONTAINER = register(new MenuType(ContainerPodium::new), "podium");
    public static final MenuType DRAGON_CONTAINER = register(new MenuType(ContainerDragon::new), "dragon");
    public static final MenuType HIPPOGRYPH_CONTAINER = register(new MenuType(ContainerHippogryph::new), "hippogryph");
    public static final MenuType HIPPOCAMPUS_CONTAINER = register(new MenuType(ContainerHippocampus::new), "hippocampus");
    public static final MenuType DRAGON_FORGE_CONTAINER = register(new MenuType(ContainerDragonForge::new), "dragon_forge");

    public static MenuType register(MenuType type, String name) {
        type.setRegistryName(name);
        return type;
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
        try {
            for (Field f : IafContainerRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof MenuType) {
                    event.getRegistry().register((MenuType) obj);
                } else if (obj instanceof MenuType[]) {
                    for (MenuType container : (MenuType[]) obj) {
                        event.getRegistry().register(container);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
