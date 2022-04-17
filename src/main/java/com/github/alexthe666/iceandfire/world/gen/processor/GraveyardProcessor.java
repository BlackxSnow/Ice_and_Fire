package com.github.alexthe666.iceandfire.world.gen.processor;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.block.BlockGhostChest;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class GraveyardProcessor extends StructureProcessor {

    private float integrity = 1.0F;
    public static final GraveyardProcessor INSTANCE = new GraveyardProcessor();
    public static final Codec<GraveyardProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public GraveyardProcessor() {
    }

    public static BlockState getRandomCobblestone(@Nullable BlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.5) {
            return Blocks.COBBLESTONE.defaultBlockState();
        } else if (rand < 0.9) {
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        } else {
            return Blocks.INFESTED_COBBLESTONE.defaultBlockState();
        }
    }

    public static BlockState getRandomCrackedBlock(@Nullable BlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.5) {
            return Blocks.STONE_BRICKS.defaultBlockState();
        } else if (rand < 0.9) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        } else {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        }
    }

    public StructureTemplate.StructureBlockInfo process(LevelReader worldReader, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, StructurePlaceSettings settings,@Nullable StructureTemplate template) {
        Random random = settings.getRandom(infoIn2.pos);
        if (infoIn2.state.getBlock() == Blocks.STONE_BRICKS) {
            BlockState state = getRandomCrackedBlock(null, random);
            return new StructureTemplate.StructureBlockInfo(infoIn2.pos, state, null);
        }
        if (infoIn2.state.getBlock() == Blocks.COBBLESTONE) {
            BlockState state = getRandomCobblestone(null, random);
            return new StructureTemplate.StructureBlockInfo(infoIn2.pos, state, null);
        }
        return infoIn2;
    }


    @Override
    protected StructureProcessorType getType() {
        return IafProcessors.GRAVEYARDPROCESSOR;
    }

}
