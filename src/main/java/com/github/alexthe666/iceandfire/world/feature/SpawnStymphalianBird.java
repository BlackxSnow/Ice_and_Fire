package com.github.alexthe666.iceandfire.world.feature;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpawnStymphalianBird extends Feature<NoneFeatureConfiguration> {

    public SpawnStymphalianBird(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoneFeatureConfiguration p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForMobs(worldIn)){
            return false;
        }
        position = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, position.offset(8, 0, 8));

        if (IafConfig.spawnStymphalianBirds && IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position) && rand.nextInt(IafConfig.stymphalianBirdSpawnChance + 1) == 0) {
            for (int i = 0; i < 4 + rand.nextInt(4); i++) {
                BlockPos pos = position.offset(rand.nextInt(10) - 5, 0, rand.nextInt(10) - 5);
                pos = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
                if (worldIn.getBlockState(pos.below()).canOcclude()) {
                    EntityStymphalianBird bird = IafEntityRegistry.STYMPHALIAN_BIRD.create(worldIn.getLevel());
                    bird.moveTo(pos.getX() + 0.5F, pos.getY() + 1.5F, pos.getZ() + 0.5F, 0, 0);
                    worldIn.addFreshEntity(bird);
                    
                }
            }
        }

        return false;
    }
}
