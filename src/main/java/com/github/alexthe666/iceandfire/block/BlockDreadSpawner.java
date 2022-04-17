package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawner;

import net.minecraft.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockDreadSpawner extends SpawnerBlock implements IDreadBlock {

    public BlockDreadSpawner() {
        super(
    		BlockBehaviour.Properties
    			.of(Material.STONE)
    			.strength(10.0F, 10000F)
    			.sound(SoundType.METAL)
    			.noOcclusion()
    			.dynamicShape()
		);

        this.setRegistryName(IceAndFire.MODID, "dread_spawner");
    }

    public BlockEntity newBlockEntity(BlockGetter worldIn) {
        return new TileEntityDreadSpawner();
    }

}
