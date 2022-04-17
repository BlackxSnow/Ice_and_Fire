package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityHippogryphEgg extends ThrownEgg {

    private ItemStack itemstack;

    public EntityHippogryphEgg(EntityType type, Level world) {
        super(type, world);
    }

    public EntityHippogryphEgg(EntityType type, Level worldIn, double x, double y, double z, ItemStack stack) {
        this(type, worldIn);
        this.setPos(x, y, z);
        this.itemstack = stack;
    }

    public EntityHippogryphEgg(EntityType type, Level worldIn, LivingEntity throwerIn, ItemStack stack) {
        this(type, worldIn);
        this.setPos(throwerIn.getX(), throwerIn.getEyeY() - (double) 0.1F, throwerIn.getZ());
        this.itemstack = stack;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    protected void onHit(HitResult result) {
        Entity thrower = getOwner();
        if (result.getType() == HitResult.Type.ENTITY) {
            ((EntityHitResult) result).getEntity().hurt(DamageSource.thrown(this, thrower), 0.0F);
        }

        if (!this.level.isClientSide) {
            EntityHippogryph hippogryph = new EntityHippogryph(IafEntityRegistry.HIPPOGRYPH, this.level);
            hippogryph.setAge(-24000);
            hippogryph.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
            if (itemstack != null) {
                int variant = 0;
                CompoundTag tag = itemstack.getTag();
                if (tag != null) {
                    variant = tag.getInt("EggOrdinal");
                }
                hippogryph.setVariant(variant);
            }
            this.level.addFreshEntity(hippogryph);
        }

        this.level.broadcastEntityEvent(this, (byte) 3);
        this.remove();
    }

    @Override
    protected Item getDefaultItem() {
      return IafItemRegistry.HIPPOGRYPH_EGG;
    }
}
