package com.github.alexthe666.iceandfire.enums;

import java.util.Map;

import com.github.alexthe666.iceandfire.entity.DragonType;
import com.google.common.collect.Maps;

import net.minecraft.ChatFormatting;

public enum EnumDragonEgg {
    RED(0, ChatFormatting.DARK_RED, DragonType.FIRE), GREEN(1, ChatFormatting.DARK_GREEN, DragonType.FIRE), BRONZE(2, ChatFormatting.GOLD, DragonType.FIRE), GRAY(3, ChatFormatting.GRAY, DragonType.FIRE),
    BLUE(4, ChatFormatting.AQUA, DragonType.ICE), WHITE(5, ChatFormatting.WHITE, DragonType.ICE), SAPPHIRE(6, ChatFormatting.BLUE, DragonType.ICE), SILVER(7, ChatFormatting.DARK_GRAY, DragonType.ICE),
    ELECTRIC(8, ChatFormatting.DARK_BLUE, DragonType.LIGHTNING), AMYTHEST(9, ChatFormatting.LIGHT_PURPLE, DragonType.LIGHTNING), COPPER(10, ChatFormatting.GOLD, DragonType.LIGHTNING), BLACK(11, ChatFormatting.DARK_GRAY, DragonType.LIGHTNING);

    private static final Map<Integer, EnumDragonEgg> META_LOOKUP = Maps.newHashMap();

    static {
        EnumDragonEgg[] var0 = values();
        int var1 = var0.length;

        for (EnumDragonEgg var3 : var0) {
            META_LOOKUP.put(var3.meta, var3);
        }
    }

    public int meta;
    public ChatFormatting color;
    public DragonType dragonType;

    EnumDragonEgg(int meta, ChatFormatting color, DragonType dragonType) {
        this.meta = meta;
        this.color = color;
        this.dragonType = dragonType;
    }

    public static EnumDragonEgg byMetadata(int meta) {
        EnumDragonEgg i = META_LOOKUP.get(meta);
        return i == null ? RED : i;
    }
}
