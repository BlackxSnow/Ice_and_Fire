package com.github.alexthe666.iceandfire.world.feature;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpawnDragonSkeleton extends Feature<NoneFeatureConfiguration> {
	protected EntityType<EntityDragonBase> dragonType;

	public SpawnDragonSkeleton(EntityType<EntityDragonBase> dt, Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
        dragonType = dt;
    }
	
    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoneFeatureConfiguration p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForMobs(worldIn)){
            return false;
        }
        position = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, position.offset(8, 0, 8));

        if (IafConfig.generateDragonSkeletons) {
            if (rand.nextInt(IafConfig.generateDragonSkeletonChance + 1) == 0) {
		        EntityDragonBase dragon = dragonType.create(worldIn.getLevel());
		        dragon.setPos(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
		        int dragonage = 10 + rand.nextInt(100);
		        dragon.growDragon(dragonage);
		        dragon.modelDeadProgress = 20;
		        dragon.setModelDead(true);
		        dragon.setDeathStage((dragonage / 5) / 2);
		        dragon.yRot = rand.nextInt(360);
		        worldIn.addFreshEntity(dragon);
            }
        }

        return false;
    }
}
