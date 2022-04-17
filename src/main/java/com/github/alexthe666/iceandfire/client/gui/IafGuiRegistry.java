package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.inventory.IafContainerRegistry;

import net.minecraft.client.gui.screens.MenuScreens;

public class IafGuiRegistry {

    public static void register() {
        MenuScreens.register(IafContainerRegistry.IAF_LECTERN_CONTAINER, GuiLectern::new);
        MenuScreens.register(IafContainerRegistry.PODIUM_CONTAINER, GuiPodium::new);
        MenuScreens.register(IafContainerRegistry.DRAGON_CONTAINER, GuiDragon::new);
        MenuScreens.register(IafContainerRegistry.HIPPOGRYPH_CONTAINER, GuiHippogryph::new);
        MenuScreens.register(IafContainerRegistry.HIPPOCAMPUS_CONTAINER, GuiHippocampus::new);
        MenuScreens.register(IafContainerRegistry.DRAGON_FORGE_CONTAINER, GuiDragonForge::new);
    }
}
