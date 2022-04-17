package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.DragonFireDamageWorldEvent;
import com.github.alexthe666.iceandfire.block.BlockCharedPath;
import com.github.alexthe666.iceandfire.block.BlockFallingReturningState;
import com.github.alexthe666.iceandfire.block.BlockReturningState;
import com.github.alexthe666.iceandfire.block.IDragonProof;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeInput;
import com.github.alexthe666.iceandfire.entity.util.BlockLaunchExplosion;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class IafDragonDestructionManager {

    public static void destroyAreaFire(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;

        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageFire;

        if (stage <= 3) {
        	BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                }
                if (IafConfig.dragonGriefing != 2 && world.random.nextBoolean()) {
                    if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockFire(world.getBlockState(pos));
                        if(transformState.getBlock() != world.getBlockState(pos).getBlock()){
                            world.setBlockAndUpdate(pos, transformState);
                        }
                        if (world.random.nextBoolean() && transformState.getMaterial().isSolid() && world.getFluidState(pos.above()).isEmpty() && !world.getBlockState(pos.above()).canOcclude()) {
                            world.setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
                        }
                    }
                }
        	});
        } else {
            final int radius = stage == 4 ? 2 : 3;
            final int j = radius + world.random.nextInt(1);
            final int k = radius + world.random.nextInt(1);
            final int l = radius + world.random.nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = (double) ff;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                if (world.getBlockEntity(blockpos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(blockpos)).onHitWithFlame();
                }
                if (blockpos.distSqr(center) <= ffDouble) {
                    if (IafConfig.dragonGriefing != 2 && world.random.nextFloat() > (float) blockpos.distSqr(center) / ff) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockFire(world.getBlockState(blockpos));
                            world.setBlockAndUpdate(blockpos, transformState);
                            if (world.random.nextBoolean() && transformState.getMaterial().isSolid() && world.getFluidState(blockpos.above()).isEmpty() && !world.getBlockState(blockpos.above()).canOcclude()) {
                                world.setBlockAndUpdate(blockpos.above(), Blocks.FIRE.defaultBlockState());
                            }
                        }
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        final int statusDuration = 5 + stage * 5;
        world.getEntitiesOfClass(
    		LivingEntity.class,
    		new AABB(
				(double) center.getX() - damageRadius,
				(double) center.getY() - damageRadius,
				(double) center.getZ() - damageRadius,
				(double) center.getX() + damageRadius,
				(double) center.getY() + damageRadius,
				(double) center.getZ() + damageRadius
			)
		).stream().forEach(livingEntity -> {
            if (!DragonUtils.onSameTeam(destroyer, livingEntity) && !destroyer.is(livingEntity) && destroyer.canSee(livingEntity)) {
                livingEntity.hurt(IafDamageRegistry.DRAGON_FIRE, stageDmg);
                livingEntity.setSecondsOnFire(statusDuration);
            }
		});
    }

    public static void destroyAreaIce(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;

        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageIce;

        if (stage <= 3) {
        	BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                }
                if (IafConfig.dragonGriefing != 2 && world.random.nextBoolean()) {
                    if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockIce(world.getBlockState(pos));
                        if(transformState.getBlock() != world.getBlockState(pos).getBlock()){
                            world.setBlockAndUpdate(pos, transformState);
                        }
                        if (world.random.nextInt(9) == 0 && transformState.getMaterial().isSolid() && world.getFluidState(pos.above()).isEmpty() && !world.getBlockState(pos.above()).canOcclude() && world.getBlockState(pos).canOcclude()) {
                            world.setBlockAndUpdate(pos.above(), IafBlockRegistry.DRAGON_ICE_SPIKES.defaultBlockState());
                        }
                    }
                }
        	});
        } else {
        	final int radius = stage == 4 ? 2 : 3;
            final int j = radius + world.random.nextInt(1);
            final int k = radius + world.random.nextInt(1);
            final int l = radius + world.random.nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = (double) ff;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                if (world.getBlockEntity(blockpos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(blockpos)).onHitWithFlame();
                }
                if (blockpos.distSqr(center) <= ffDouble) {
                    if (IafConfig.dragonGriefing != 2 && world.random.nextFloat() > (float) blockpos.distSqr(center) / ff) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockIce(world.getBlockState(blockpos));
                            world.setBlockAndUpdate(blockpos, transformState);
                            if (world.random.nextInt(9) == 0 && transformState.getMaterial().isSolid() && world.getFluidState(blockpos.above()).isEmpty() && !world.getBlockState(blockpos.above()).canOcclude() && world.getBlockState(blockpos).canOcclude()) {
                                world.setBlockAndUpdate(blockpos.above(), IafBlockRegistry.DRAGON_ICE_SPIKES.defaultBlockState());
                            }
                        }
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        final int statusDuration = 50 * stage;
        world.getEntitiesOfClass(
    		LivingEntity.class,
    		new AABB(
				(double) center.getX() - damageRadius,
				(double) center.getY() - damageRadius,
				(double) center.getZ() - damageRadius,
				(double) center.getX() + damageRadius,
				(double) center.getY() + damageRadius,
				(double) center.getZ() + damageRadius
			)
		).stream().forEach(livingEntity -> {
            if (!DragonUtils.onSameTeam(destroyer, livingEntity) && !destroyer.is(livingEntity) && destroyer.canSee(livingEntity)) {
                livingEntity.hurt(IafDamageRegistry.DRAGON_ICE, stageDmg);
                FrozenProperties.setFrozenFor(livingEntity, statusDuration);
            }
		});
    }

    public static void destroyAreaFireCharge(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;

            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
            	BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.random.nextFloat() * 3 > pos.distSqr(center) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                    if (world.random.nextBoolean()) {
                        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                            BlockState transformState = transformBlockFire(world.getBlockState(pos));
                            world.setBlockAndUpdate(pos, transformState);
                            if (world.random.nextBoolean() && transformState.getMaterial().isSolid() && world.getFluidState(pos.above()).isEmpty() && !world.getBlockState(pos.above()).canOcclude()) {
                                world.setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
                            }
                        }
                    }
            	});
            } else {
                final int radius = stage == 4 ? 2 : 3;
                j = radius + world.random.nextInt(2);
                k = radius + world.random.nextInt(2);
                l = radius + world.random.nextInt(2);
                final float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;
                final double ffDouble = (double) ff;

                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (world.random.nextFloat() * 3 > (float) blockpos.distSqr(center) / ff && !(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                        }
                    }
                });

                j++;
                k++;
                l++;
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockFire(world.getBlockState(blockpos));
                            world.setBlockAndUpdate(blockpos, transformState);
                            if (world.random.nextBoolean() && transformState.getMaterial().isSolid() && world.getFluidState(blockpos.above()).isEmpty() && !world.getBlockState(blockpos.above()).canOcclude()) {
                                world.setBlockAndUpdate(blockpos.above(), Blocks.FIRE.defaultBlockState());
                            }
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            final int statusDuration = 15;
            world.getEntitiesOfClass(
        		LivingEntity.class,
        		new AABB(
    				(double) center.getX() - j,
    				(double) center.getY() - k,
    				(double) center.getZ() - l,
    				(double) center.getX() + j,
    				(double) center.getY() + k,
    				(double) center.getZ() + l
				)
    		).stream().forEach(livingEntity -> {
                if (!destroyer.isAlliedTo(livingEntity) && !destroyer.is(livingEntity) && destroyer.canSee(livingEntity)) {
                    livingEntity.hurt(IafDamageRegistry.DRAGON_FIRE, stageDmg);
                    livingEntity.setSecondsOnFire(statusDuration);
                }
            });

            if (IafConfig.explosiveDragonBreath) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2));
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        }
    }

    public static void destroyAreaIceCharge(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;

            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
            	BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.random.nextFloat() * 3 > pos.distSqr(center) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
            	});
            	BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.random.nextBoolean()) {
                        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                            BlockState transformState = transformBlockIce(world.getBlockState(pos));
                            world.setBlockAndUpdate(pos, transformState);
                            if (world.random.nextBoolean() && transformState.getMaterial().isSolid() && world.getFluidState(pos.above()).isEmpty() && !world.getBlockState(pos.above()).canOcclude() && world.getBlockState(pos).canOcclude()) {
                                world.setBlockAndUpdate(pos.above(), IafBlockRegistry.DRAGON_ICE_SPIKES.defaultBlockState());
                            }
                        }
                    }
            	});
            } else {
                int radius = stage == 4 ? 2 : 3;
                j = radius + world.random.nextInt(2);
                k = radius + world.random.nextInt(2);
                l = radius + world.random.nextInt(2);
                final float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;
                final double ffDouble = (double) ff;

                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (world.random.nextFloat() * 3 > (float) blockpos.distSqr(center) / ff && !(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                        }
                    }
                });

                j++;
                k++;
                l++;
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockIce(world.getBlockState(blockpos));
                            world.setBlockAndUpdate(blockpos, transformState);
                            if (world.random.nextBoolean() && transformState.getMaterial().isSolid() && world.getFluidState(blockpos.above()).isEmpty() && !world.getBlockState(blockpos.above()).canOcclude() && world.getBlockState(blockpos).canOcclude()) {
                                world.setBlockAndUpdate(blockpos.above(), IafBlockRegistry.DRAGON_ICE_SPIKES.defaultBlockState());
                            }
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            final int statusDuration = 400;
            world.getEntitiesOfClass(
        		LivingEntity.class,
        		new AABB(
    				(double) center.getX() - j,
    				(double) center.getY() - k,
    				(double) center.getZ() - l,
    				(double) center.getX() + j,
    				(double) center.getY() + k,
    				(double) center.getZ() + l
				)
    		).stream().forEach(livingEntity -> {
                if (!destroyer.isAlliedTo(livingEntity) && !destroyer.is(livingEntity) && destroyer.canSee(livingEntity)) {
                    livingEntity.hurt(IafDamageRegistry.DRAGON_ICE, stageDmg);
                    FrozenProperties.setFrozenFor(livingEntity, statusDuration);
                }
    		});

            if (IafConfig.explosiveDragonBreath) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2));
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        }
    }

    public static void destroyAreaLightning(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;

        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageLightning;

        if (stage <= 3) {
        	BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                }
                if (IafConfig.dragonGriefing != 2 && world.random.nextBoolean()) {
                    if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockLightning(world.getBlockState(pos));
                        if(transformState.getBlock() != world.getBlockState(pos).getBlock()){
                            world.setBlockAndUpdate(pos, transformState);
                        }
                    }
                }
        	});
        } else {
            int radius = stage == 4 ? 2 : 3;
            int j = radius + world.random.nextInt(1);
            int k = radius + world.random.nextInt(1);
            int l = radius + world.random.nextInt(1);
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = (double) ff;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                if (world.getBlockEntity(blockpos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(blockpos)).onHitWithFlame();
                }
                if (blockpos.distSqr(center) <= ffDouble) {
                    if (IafConfig.dragonGriefing != 2 && world.random.nextFloat() > (float) blockpos.distSqr(center) / ff) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockLightning(world.getBlockState(blockpos));
                            world.setBlockAndUpdate(blockpos, transformState);
                        }
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        world.getEntitiesOfClass(
    		LivingEntity.class,
    		new AABB(
				(double) center.getX() - damageRadius,
				(double) center.getY() - damageRadius,
				(double) center.getZ() - damageRadius,
				(double) center.getX() + damageRadius,
				(double) center.getY() + damageRadius,
				(double) center.getZ() + damageRadius
			)
		).stream().forEach(livingEntity -> {
            if (!DragonUtils.onSameTeam(destroyer, livingEntity) && !destroyer.is(livingEntity) && destroyer.canSee(livingEntity)) {
                livingEntity.hurt(IafDamageRegistry.DRAGON_LIGHTNING, stageDmg);
                double d1 = destroyer.getX() - livingEntity.getX();
                double d0 = destroyer.getZ() - livingEntity.getZ();
                livingEntity.knockback(0.3F, d1, d0);
            }
		});
    }

    public static void destroyAreaLightningCharge(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;

            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
            	BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.random.nextFloat() * 7F > Math.sqrt(center.distSqr(pos)) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
            	});
            	BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.random.nextFloat() * 7F > Math.sqrt(center.distSqr(pos)) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockLightning(world.getBlockState(pos));
                        world.setBlockAndUpdate(pos, transformState);
                    }
            	});
            } else {
                int radius = stage == 4 ? 2 : 3;
                j = radius + world.random.nextInt(2);
                k = radius + world.random.nextInt(2);
                l = radius + world.random.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;
                final double ffDouble = (double) ff;

                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (world.random.nextFloat() * 3 > (float) blockpos.distSqr(center) / ff && !(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                        }
                    }
                });

                j++;
                k++;
                l++;
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockLightning(world.getBlockState(blockpos));
                            world.setBlockAndUpdate(blockpos, transformState);
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            world.getEntitiesOfClass(
        		LivingEntity.class,
        		new AABB(
    				(double) center.getX() - j,
    				(double) center.getY() - k,
    				(double) center.getZ() - l,
    				(double) center.getX() + j,
    				(double) center.getY() + k,
    				(double) center.getZ() + l
				)
    		).stream().forEach(livingEntity -> {
                if (!destroyer.isAlliedTo(livingEntity) && !destroyer.is(livingEntity) && destroyer.canSee(livingEntity)) {
                    livingEntity.hurt(IafDamageRegistry.DRAGON_LIGHTNING, stageDmg);
                    double d1 = destroyer.getX() - livingEntity.getX();
                    double d0 = destroyer.getZ() - livingEntity.getZ();
                    livingEntity.knockback(0.9F, d1, d0);
                }
    		});

            if (IafConfig.explosiveDragonBreath) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2));
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        }
    }


    public static BlockState transformBlockFire(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyDirtBlock) {
            return IafBlockRegistry.CHARRED_GRASS.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.DIRT && in.getBlock() == Blocks.DIRT) {
            return IafBlockRegistry.CHARRED_DIRT.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.CHARRED_GRAVEL.defaultBlockState().setValue(BlockFallingReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.STONE && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone"))) {
            return IafBlockRegistry.CHARRED_COBBLESTONE.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.STONE && in.getBlock() != IafBlockRegistry.CHARRED_COBBLESTONE) {
            return IafBlockRegistry.CHARRED_STONE.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.GRASS_PATH) {
            return IafBlockRegistry.CHARRED_GRASS_PATH.defaultBlockState().setValue(BlockCharedPath.REVERTS, true);
        } else if (in.getMaterial() == Material.WOOD) {
            return IafBlockRegistry.ASH.defaultBlockState();
        } else if (in.getMaterial() == Material.LEAVES || in.getMaterial() == Material.PLANT || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.defaultBlockState();
        }
        return in;
    }

    public static BlockState transformBlockIce(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyDirtBlock) {
            return IafBlockRegistry.FROZEN_GRASS.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.DIRT && in.getBlock() == Blocks.DIRT || in.getMaterial() == Material.SNOW) {
            return IafBlockRegistry.FROZEN_DIRT.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.FROZEN_GRAVEL.defaultBlockState().setValue(BlockFallingReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() != Blocks.GRAVEL) {
            return in;
        } else if (in.getMaterial() == Material.STONE && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone"))) {
            return IafBlockRegistry.FROZEN_COBBLESTONE.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.STONE && in.getBlock() != IafBlockRegistry.FROZEN_COBBLESTONE) {
            return IafBlockRegistry.FROZEN_STONE.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.GRASS_PATH) {
            return IafBlockRegistry.FROZEN_GRASS_PATH.defaultBlockState().setValue(BlockCharedPath.REVERTS, true);
        } else if (in.getMaterial() == Material.WOOD) {
            return IafBlockRegistry.FROZEN_SPLINTERS.defaultBlockState();
        } else if (in.getMaterial() == Material.WATER) {
            return Blocks.ICE.defaultBlockState();
        } else if (in.getMaterial() == Material.LEAVES || in.getMaterial() == Material.PLANT || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.defaultBlockState();
        }
        return in;
    }

    public static BlockState transformBlockLightning(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyDirtBlock) {
            return IafBlockRegistry.CRACKLED_GRASS.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.DIRT && in.getBlock() == Blocks.DIRT) {
            return IafBlockRegistry.CRACKLED_DIRT.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.CRACKLED_GRAVEL.defaultBlockState().setValue(BlockFallingReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.STONE && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone"))) {
            return IafBlockRegistry.CRACKLED_COBBLESTONE.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.STONE && in.getBlock() != IafBlockRegistry.CRACKLED_COBBLESTONE) {
            return IafBlockRegistry.CRACKLED_STONE.defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.GRASS_PATH) {
            return IafBlockRegistry.CRACKLED_GRASS_PATH.defaultBlockState().setValue(BlockCharedPath.REVERTS, true);
        } else if (in.getMaterial() == Material.WOOD) {
            return IafBlockRegistry.ASH.defaultBlockState();
        } else if (in.getMaterial() == Material.LEAVES || in.getMaterial() == Material.PLANT || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.defaultBlockState();
        }
        return in;
    }

}
