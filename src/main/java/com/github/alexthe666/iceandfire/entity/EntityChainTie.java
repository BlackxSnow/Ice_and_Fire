package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.entity.props.ChainProperties;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.entity.*;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class EntityChainTie extends HangingEntity {

    public EntityChainTie(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityChainTie(EntityType type, Level worldIn, BlockPos hangingPositionIn) {
        super(type, worldIn, hangingPositionIn);
        this.setPos((double) hangingPositionIn.getX() + 0.5D, hangingPositionIn.getY(), (double) hangingPositionIn.getZ() + 0.5D);
        this.forcedLoading = true;
    }

    public static EntityChainTie createTie(Level worldIn, BlockPos fence) {
        EntityChainTie entityChainTie = new EntityChainTie(IafEntityRegistry.CHAIN_TIE, worldIn, fence);
        worldIn.addFreshEntity(entityChainTie);
        entityChainTie.playPlacementSound();
        return entityChainTie;
    }

    @Nullable
    public static EntityChainTie getKnotForPosition(Level worldIn, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (EntityChainTie entityleashknot : worldIn.getEntitiesOfClass(EntityChainTie.class, new AABB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D))) {
            if (entityleashknot != null && entityleashknot.getPos() != null && entityleashknot.getPos().equals(pos)) {
                return entityleashknot;
            }
        }
        return null;
    }

    public void setPos(double x, double y, double z) {
        super.setPos((double) Mth.floor(x) + 0.5D, (double) Mth.floor(y) + 0.5D, (double) Mth.floor(z) + 0.5D);
    }

    protected void recalculateBoundingBox() {
        this.setPosRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D);
        double xSize = 0.3D;
        double ySize = 0.875D;
        double zSize = xSize;
        this.setBoundingBox(new AABB(this.getX() - xSize, this.getY() - 0.5, this.getZ() - zSize,
            this.getX() + xSize, this.getY() + ySize - 0.5, this.getZ() + zSize));
        if (this.isAddedToWorld() && this.level instanceof net.minecraft.server.level.ServerLevel)
            ((net.minecraft.server.level.ServerLevel) this.level).updateChunkPos(this); // Forge - Process chunk registration after moving.
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && source.getEntity() instanceof Player) {
            return super.hurt(source, amount);
        }
        return false;
    }

    public int getWidth() {
        return 9;
    }

    public int getHeight() {
        return 9;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        BlockPos blockpos = this.getPos();
        compound.putInt("TileX", blockpos.getX());
        compound.putInt("TileY", blockpos.getY());
        compound.putInt("TileZ", blockpos.getZ());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        this.pos = new BlockPos(compound.getInt("TileX"), compound.getInt("TileY"), compound.getInt("TileZ"));
    }

    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return -0.0625F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    public void dropItem(@Nullable Entity brokenEntity) {
        this.playSound(SoundEvents.ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }

    @Override
    public void remove() {
        this.remove(false);
    }

    @Override
    public void remove(boolean keepData) {
        super.remove(keepData);
        double d0 = 30D;
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - d0, this.getY() - d0, this.getZ() - d0, this.getX() + d0, this.getY() + d0, this.getZ() + d0));
        for (LivingEntity livingEntity : list) {
            if (ChainProperties.isChainedTo(livingEntity, this)) {
                ChainProperties.removeChain(livingEntity, this);
                ItemEntity entityitem = new ItemEntity(this.level, this.getX(), this.getY() + (double) 1, this.getZ(), new ItemStack(IafItemRegistry.CHAIN));
                entityitem.setDefaultPickUpDelay();
                this.level.addFreshEntity(entityitem);
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            boolean flag = false;
            double d0 = 30D;
            List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - d0, this.getY() - d0, this.getZ() - d0, this.getX() + d0, this.getY() + d0, this.getZ() + d0));

            for (LivingEntity livingEntity : list) {
                if (ChainProperties.isChainedTo(livingEntity, player)) {
                    ChainProperties.removeChain(livingEntity, player);
                    ChainProperties.attachChain(livingEntity, this);
                    flag = true;
                }
            }

            if (!flag) {
                this.remove();
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean survives() {
        return this.level.getBlockState(this.pos).getBlock() instanceof WallBlock;
    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }
}
