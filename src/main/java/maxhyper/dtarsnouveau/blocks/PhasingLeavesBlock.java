package maxhyper.dtarsnouveau.blocks;

import com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.verdantartifice.primalmagick.common.blocks.trees.AbstractPhasingLeavesBlock;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import net.minecraft.core.BlockPos;
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
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(PHASE).getLightLevel();
    }
}
