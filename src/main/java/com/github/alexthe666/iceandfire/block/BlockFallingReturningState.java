package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockFallingReturningState extends FallingBlock {
    public static final BooleanProperty REVERTS = BooleanProperty.create("revert");
    public Item itemBlock;
    private BlockState returnState;

    public BlockFallingReturningState(Material materialIn, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, BlockState revertState) {
        super(
    		BlockBehaviour.Properties
    			.of(materialIn)
    			.sound(sound)
    			.strength(hardness, resistance)
    			.harvestTool(ToolType.get(toolUsed))
    			.harvestLevel(toolStrength)
    			.randomTicks()
		);

        setRegistryName(IceAndFire.MODID, name);
        this.returnState = revertState;
        this.registerDefaultState(this.stateDefinition.any().setValue(REVERTS, Boolean.valueOf(false)));
    }

    @SuppressWarnings("deprecation")
    public BlockFallingReturningState(Material materialIn, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean slippery, BlockState revertState) {
        super(
    		BlockBehaviour.Properties
    			.of(materialIn)
    			.sound(sound)
    			.strength(hardness, resistance)
    			.harvestTool(ToolType.get(toolUsed))
    			.harvestLevel(toolStrength)
    			.randomTicks()
		);

        setRegistryName(IceAndFire.MODID, name);
        this.returnState = revertState;
        this.registerDefaultState(this.stateDefinition.any().setValue(REVERTS, Boolean.valueOf(false)));
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        if (!worldIn.isClientSide) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (state.getValue(REVERTS) && rand.nextInt(3) == 0) {
                worldIn.setBlockAndUpdate(pos, returnState);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getDustColor(BlockState blkst) {
        return -8356741;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REVERTS);
    }
}
