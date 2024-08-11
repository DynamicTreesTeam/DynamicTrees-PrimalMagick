package maxhyper.dtprimalmagick.blocks;

import com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.verdantartifice.primalmagick.common.blocks.trees.AbstractPhasingLeavesBlock;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import maxhyper.dtprimalmagick.trees.PhasingLeavesProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class PhasingLeavesBlock extends DynamicLeavesBlock {

    public static final EnumProperty<TimePhase> PHASE = AbstractPhasingLeavesBlock.PHASE;

    public PhasingLeavesBlock(LeavesProperties leavesProperties, Properties properties) {
        super(leavesProperties, properties.randomTicks());
        registerDefaultState(defaultBlockState().setValue(PHASE, TimePhase.FULL));
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(PHASE));
    }

    @Override
    public PhasingLeavesProperties getProperties(BlockState blockState) {
        return (PhasingLeavesProperties) super.getProperties(blockState);
    }

    @Override
    public BlockState getLeavesBlockStateForPlacement(LevelAccessor level, BlockPos pos, BlockState leavesStateWithHydro, int oldHydro, boolean worldGen) {
        BlockState state = super.getLeavesBlockStateForPlacement(level, pos, leavesStateWithHydro, oldHydro, worldGen);
        return state.setValue(PHASE, getProperties(state).getCurrentPhase(level));
    }

    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        super.randomTick(state, worldIn, pos, random);
        TimePhase newPhase = getProperties(state).getCurrentPhase(worldIn);
        if (newPhase != state.getValue(PHASE)) {
            worldIn.setBlock(pos, state.setValue(PHASE, newPhase), 3);
        }
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState state = super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        TimePhase newPhase = getProperties(state).getCurrentPhase(worldIn);
        if (newPhase != state.getValue(PHASE)) {
            state = state.setValue(PHASE, newPhase);
        }
        return state;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(PHASE).getLightLevel();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return switch (state.getValue(PHASE)){
            case FULL -> 1F;
            case WAXING -> 0.75F;
            case WANING -> 0.5F;
            case FADED -> 0.25F;
        };
    }

}
