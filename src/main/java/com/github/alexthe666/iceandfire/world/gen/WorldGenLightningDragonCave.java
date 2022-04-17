package com.github.alexthe666.iceandfire.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WorldGenLightningDragonCave extends Feature<NoneFeatureConfiguration> {
    public static final ResourceLocation LIGHTNINGDRAGON_CHEST = new ResourceLocation("iceandfire", "chest/lightning_dragon_female_cave");
    public static final ResourceLocation LIGHTNINGDRAGON_MALE_CHEST = new ResourceLocation("iceandfire", "chest/lightning_dragon_male_cave");
    private static final WorldGenCaveStalactites CEILING_DECO = new WorldGenCaveStalactites(IafBlockRegistry.CRACKLED_STONE, 9);
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static boolean isMale;

    public WorldGenLightningDragonCave(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    public static void setGoldPile(LevelAccessor world, BlockPos pos, Random rand) {
        int chance = rand.nextInt(99) + 1;
        if (!(world.getBlockState(pos).getBlock() instanceof BaseEntityBlock)) {
            if (chance < 60) {
                int goldRand = Math.max(1, IafConfig.dragonDenGoldAmount) * (isMale ? 1 : 2);
                boolean generateGold = rand.nextInt(goldRand) == 0;
                world.setBlock(pos, generateGold ? IafBlockRegistry.COPPER_PILE.defaultBlockState().setValue(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)) : Blocks.AIR.defaultBlockState(), 3);
            } else if (chance == 61) {
                world.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, HORIZONTALS[rand.nextInt(3)]), 2);
                if (world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                    BlockEntity tileentity1 = world.getBlockEntity(pos);
                    if (tileentity1 instanceof ChestBlockEntity) {
                        ((ChestBlockEntity) tileentity1).setLootTable(isMale ? LIGHTNINGDRAGON_MALE_CHEST : LIGHTNINGDRAGON_CHEST, rand.nextLong());
                    }
                }
            }
        }
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoneFeatureConfiguration p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForDragons(worldIn)){
            return false;
        }
        if(!IafConfig.generateDragonDens || rand.nextInt(IafConfig.generateDragonDenChance) != 0 || !IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position) || !IafWorldRegistry.isFarEnoughFromDangerousGen(worldIn, position)){
            return false;
        }
        List<SphereInfo> sphereList = new ArrayList<SphereInfo>();
        position = new BlockPos(position.getX(), 20 + rand.nextInt(20), position.getZ());
        isMale = new Random().nextBoolean();
        int dragonAge = 75 + rand.nextInt(50);
        int radius = (int) (dragonAge * 0.2F) + rand.nextInt(8);
        createShell(worldIn, rand, position, radius, sphereList);
        for (int i = 0; i < 3 + rand.nextInt(2); i++) {
            Direction direction = HORIZONTALS[rand.nextInt(HORIZONTALS.length - 1)];
            createShell(worldIn, rand, position.relative(direction, radius - 2), 2 * (int) (radius / 3F) + rand.nextInt(8), sphereList);
        }
        for (SphereInfo info : sphereList) {
            hollowOut(worldIn, rand, info.pos, info.radius - 2);
            decorateCave(worldIn, rand, info.pos, info.radius + 2);
        }
        sphereList.clear();
        EntityLightningDragon dragon = new EntityLightningDragon(IafEntityRegistry.LIGHTNING_DRAGON, worldIn.getLevel());
        dragon.setGender(isMale);
        dragon.growDragon(dragonAge);
        dragon.setAgingDisabled(true);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.setVariant(rand.nextInt(4));
        dragon.absMoveTo(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
        dragon.setInSittingPose(true);
        dragon.homePos = position;
        dragon.setHunger(50);
        worldIn.addFreshEntity(dragon);
        return false;
    }

    private void decorateCave(LevelAccessor worldIn, Random rand, BlockPos pos, int radius) {
        for (int i = 0; i < 15 + rand.nextInt(10); i++) {
            CEILING_DECO.generate(worldIn, rand, offsetRandomlyByXZ(pos.above(radius / 2 - 1), rand, rand.nextInt(radius) - radius / 2, rand.nextInt(radius) - radius / 2));
        }
        int j = radius;
        int k = radius / 2;
        int l = radius;
        float f = (float) (j + k + l) * 0.333F + 0.5F;
        BlockPos.betweenClosedStream(pos.offset(-j, -k, -l), pos.offset(j, k / 2, l)).map(BlockPos::immutable).forEach(blockPos -> {
            if (blockPos.distSqr(pos) <= (double) (f * f) && worldIn.getBlockState(blockPos.below()).getMaterial() == Material.STONE && worldIn.getBlockState(blockPos).getMaterial() != Material.STONE) {
                setGoldPile(worldIn, blockPos, rand);
            }
        });

    }

    private BlockPos offsetRandomlyBy(BlockPos in, Random rand, int offset1, int offset2) {
        return in.relative(Direction.values()[rand.nextInt(Direction.values().length - 1)], offset1).relative(Direction.values()[rand.nextInt(Direction.values().length - 1)], offset2);
    }

    private BlockPos offsetRandomlyByXZ(BlockPos in, Random rand, int offset1, int offset2) {
        return in.offset(offset1, 0, offset2);
    }

    private void createShell(LevelAccessor worldIn, Random rand, BlockPos position, int radius, List<SphereInfo> sphereList) {
        int j = radius;
        int k = radius / 2;
        int l = radius;
        float f = (float) (j + k + l) * 0.333F + 0.5F;
        BlockPos.betweenClosedStream(position.offset(-j, -k, -l), position.offset(j, k, l)).map(BlockPos::immutable).forEach(blockPos ->  {
            if (blockPos.distSqr(position) <= (double) (f * f)) {
                if (!(worldIn.getBlockState(position).getBlock() instanceof BaseEntityBlock) && worldIn.getBlockState(position).getDestroySpeed(worldIn, position) >= 0) {
                    boolean doOres = rand.nextInt(IafConfig.oreToStoneRatioForDragonCaves + 1) == 0;
                    if (doOres) {
                        int chance = rand.nextInt(199) + 1;
                        if (chance < 30) {
                            worldIn.setBlock(blockPos, Blocks.IRON_ORE.defaultBlockState(), 3);
                        }
                        else if (chance > 30 && chance < 40) {
                            worldIn.setBlock(blockPos, Blocks.GOLD_ORE.defaultBlockState(), 3);
                        }
                        else if (chance > 40 && chance < 45) {
                            worldIn.setBlock(blockPos, IafConfig.generateCopperOre ? IafBlockRegistry.COPPER_ORE.defaultBlockState() : IafBlockRegistry.CRACKLED_STONE.defaultBlockState(), 3);
                        }
                        else if (chance > 45 && chance < 50) {
                            worldIn.setBlock(blockPos, IafConfig.generateSilverOre ? IafBlockRegistry.SILVER_ORE.defaultBlockState() : IafBlockRegistry.CRACKLED_STONE.defaultBlockState(), 3);
                        }
                        else if (chance > 50 && chance < 60) {
                            worldIn.setBlock(blockPos, Blocks.COAL_ORE.defaultBlockState(), 3);
                        }
                        else if (chance > 60 && chance < 70) {
                            worldIn.setBlock(blockPos, Blocks.REDSTONE_ORE.defaultBlockState(), 3);
                        }
                        else if (chance > 70 && chance < 80) {
                            worldIn.setBlock(blockPos, Blocks.LAPIS_ORE.defaultBlockState(), 3);
                        }
                        else if (chance > 80 && chance < 90) {
                            worldIn.setBlock(blockPos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                        }
                        else if (chance > 90 && chance < 1000) {
                            worldIn.setBlock(blockPos, IafConfig.generateAmythestOre ? IafBlockRegistry.AMYTHEST_ORE.defaultBlockState() : Blocks.EMERALD_ORE.defaultBlockState(), 3);
                        }
                    } else {
                        worldIn.setBlock(blockPos, rand.nextBoolean() ? IafBlockRegistry.CRACKLED_COBBLESTONE.defaultBlockState() : IafBlockRegistry.CRACKLED_STONE.defaultBlockState(), 2);
                    }
                }
            }
        });
        sphereList.add(new SphereInfo(radius, position));
    }

    private void hollowOut(LevelAccessor worldIn, Random rand, BlockPos position, int radius) {
        int j = radius;
        int k = radius / 2;
        int l = radius;
        float f = (float) (j + k + l) * 0.333F + 0.5F;
        BlockPos.betweenClosedStream(position.offset(-j, -k, -l), position.offset(j, k, l)).map(BlockPos::immutable).forEach(blockPos ->  {
            if (blockPos.distSqr(position) <= (double) (f * f * Mth.clamp(rand.nextFloat(), 0.75F, 1.0F))) {
                if (!(worldIn.getBlockState(position).getBlock() instanceof BaseEntityBlock)) {
                    worldIn.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        });
    }


    private class SphereInfo {
        int radius;
        BlockPos pos;

        private SphereInfo(int radius, BlockPos pos) {
            this.radius = radius;
            this.pos = pos;
        }
    }
}
