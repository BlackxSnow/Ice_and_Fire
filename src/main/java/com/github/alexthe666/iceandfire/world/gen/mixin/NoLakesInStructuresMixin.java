package com.github.alexthe666.iceandfire.world.gen.mixin;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

// Based on code from TelepathicGrunts RepurposedStructures
@Mixin(LakeFeature.class)
public class NoLakesInStructuresMixin {

    @Inject(
            method = "generate(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/BlockStateFeatureConfig;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/BlockPos;down(I)Lnet/minecraft/util/math/BlockPos;"),
            cancellable = true
    )
    private void iaf_noLakesInMausoleum(WorldGenLevel serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockStateConfiguration singleStateFeatureConfig, CallbackInfoReturnable<Boolean> cir) {
        SectionPos sectionPos = SectionPos.of(blockPos);
        StructureFeature<NoneFeatureConfiguration> structure = IafWorldRegistry.MAUSOLEUM;
        if (serverWorldAccess.startsForFeature(sectionPos, structure).findAny().isPresent()) {
             cir.setReturnValue(false);
        }
    }
}