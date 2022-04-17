package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockGraveyardSoil extends Block {

    public BlockGraveyardSoil() {
        super(
    		Properties
    			.of(Material.DIRT)
    			.sound(SoundType.GRAVEL)
    			.strength(5, 15)
    			.harvestTool(ToolType.SHOVEL)
    			.harvestLevel(0)
    			.randomTicks()
		);

        setRegistryName(IceAndFire.MODID, "graveyard_soil");
    }


    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isClientSide) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (!worldIn.isDay() && !worldIn.getBlockState(pos.above()).canOcclude() && rand.nextInt(9) == 0 && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
                int checkRange = 32;
                int k = worldIn.getEntitiesOfClass(EntityGhost.class, (new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).inflate(checkRange)).size();
                if(k < 10){
                    EntityGhost ghost = IafEntityRegistry.GHOST.create(worldIn);
                    Random random = new Random();
                    ghost.absMoveTo(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, random.nextFloat() * 360F, 0);
                    if (!worldIn.isClientSide) {
                        ghost.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, null);
                        worldIn.addFreshEntity(ghost);
                    }
                    ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
                    ghost.restrictTo(pos, 16);
                }
            }
        }
    }
}
