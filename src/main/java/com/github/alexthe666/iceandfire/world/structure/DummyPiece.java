package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ShipwreckPieces;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.Random;

public class DummyPiece extends TemplateStructurePiece {
    // Register to the registry name of the old structure piece removed to prevent logspam for players in existing worlds.

    public DummyPiece(StructureManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_, Random random) {
        super(IafWorldRegistry.DUMMY_PIECE, 0);
        loadTemplate(p_i48904_1_);
    }

    public DummyPiece(StructureManager p_i50445_1_, CompoundTag p_i50445_2_) {
        super(IafWorldRegistry.DUMMY_PIECE, p_i50445_2_);
        loadTemplate(p_i50445_1_);
    }

    // Sets up various templateStructurePiece variables these aren't necessarily needed
    // but are included as a backup to avoid crashes
    // Code stems from ShipwreckPieces.class
    private void loadTemplate(StructureManager p_204754_1_) {
        StructureTemplate lvt_2_1_ = p_204754_1_.getOrCreate(new ResourceLocation("minecraft:empty"));
        StructurePlaceSettings lvt_3_1_ = (new StructurePlaceSettings()).setRotation(Rotation.CLOCKWISE_90).setMirror(Mirror.NONE).setRotationPivot(new BlockPos(0,0,0)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
    }
    //Override post processing function since we don't have to do any for this dummy piece
    @Override
    public boolean postProcess(WorldGenLevel p_230383_1_, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, BoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        return true;
    }

    protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb) {
    }
}
