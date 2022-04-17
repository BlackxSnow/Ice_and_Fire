package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.BlockGhostChest;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;

public class GorgonTempleProcessor extends StructureProcessor {

    private float integrity = 1.0F;
    public static final GorgonTempleProcessor INSTANCE = new GorgonTempleProcessor();
    public static final Codec<GorgonTempleProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public GorgonTempleProcessor() {
    }


    public StructureTemplate.StructureBlockInfo process(LevelReader worldReader, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, StructurePlaceSettings settings, @Nullable StructureTemplate template) {

        // Workaround for https://bugs.mojang.com/browse/MC-130584
        // Due to a hardcoded field in Templates, any waterloggable blocks in structures replacing water in the world will become waterlogged.
        // Idea of workaround is detect if we are placing a waterloggable block and if so, remove the water in the world instead.
        ChunkPos currentChunk = new ChunkPos(infoIn2.pos);
        if(infoIn2.state.getBlock() instanceof SimpleWaterloggedBlock){
            if(worldReader.getFluidState(infoIn2.pos).is(FluidTags.WATER)){
               worldReader.getChunk(currentChunk.x, currentChunk.z).setBlockState(infoIn2.pos, Blocks.AIR.defaultBlockState(), false);
            }
        }

        // Needed as waterloggable blocks will get waterlogged from neighboring chunk's water too.
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for(Direction direction : Direction.Plane.HORIZONTAL){
            mutable.set(infoIn2.pos).move(direction);
            if(currentChunk.x != mutable.getX() >> 4 || currentChunk.z != mutable.getZ() >> 4){
                ChunkAccess sideChunk = worldReader.getChunk(mutable);
                if(sideChunk.getFluidState(mutable).is(FluidTags.WATER)) {
                    sideChunk.setBlockState(mutable, Blocks.STONE_BRICKS.defaultBlockState(), false);
                }
            }
        }

        return infoIn2;
    }


    @Override
    protected StructureProcessorType getType() {
        return IafProcessors.GORGONTEMPLEPROCESSOR;
    }
}
