package maxhyper.dtarsnouveau.blocks;

import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.mojang.datafixers.types.Func;
import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.common.blocks.trees.AbstractPhasingLogBlock;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import com.verdantartifice.primalmagick.common.sources.Source;
import maxhyper.dtarsnouveau.trees.PhasingTreeFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class PhasingBranchBlock extends ThickBranchBlock {

    public static final EnumProperty<TimePhase> PHASE = AbstractPhasingLogBlock.PHASE;
    public static final BooleanProperty PULSING = AbstractPhasingLogBlock.PULSING;

    public PhasingBranchBlock(ResourceLocation name, Properties properties) {
        super(name, properties.randomTicks());
        registerDefaultState(defaultBlockState().setValue(PHASE, TimePhase.FULL).setValue(PULSING, false));
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(PHASE, PULSING));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(PHASE).getLightLevel();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(PULSING) && random.nextInt(4) == 0) {
            FxDispatcher.INSTANCE.spellImpact((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 2, getFamily().getPulseColor());
        }
    }

    @Override
    public PhasingTreeFamily getFamily() {
        return (PhasingTreeFamily)super.getFamily();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//        if (pState.getValue(PHASE) == TimePhase.FULL)
//            return super.getVisualShape(pState, pLevel, pPos, pContext);
//        return Shapes.empty();
        return Shapes.empty();
    }

}
