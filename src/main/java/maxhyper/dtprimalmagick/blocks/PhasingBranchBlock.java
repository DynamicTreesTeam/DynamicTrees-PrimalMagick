package maxhyper.dtprimalmagick.blocks;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.common.blocks.trees.AbstractPhasingLogBlock;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import maxhyper.dtprimalmagick.trees.PhasingTreeFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class PhasingBranchBlock extends ThickBranchBlock {

    public static final EnumProperty<TimePhase> PHASE = AbstractPhasingLogBlock.PHASE;
    public static final BooleanProperty PULSING = AbstractPhasingLogBlock.PULSING;

    public PhasingBranchBlock(ResourceLocation name, Properties properties) {
        super(name, properties.randomTicks());
    }

    @Override
    public BlockState[] createBranchStates(IntegerProperty radiusProperty, int maxRadius) {
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(radiusProperty, 1)
                .setValue(WATERLOGGED, false)
                .setValue(PHASE, TimePhase.FULL)
                .setValue(PULSING, false));
        BlockState[] branchStates = new BlockState[maxRadius + 1];
        branchStates[0] = Blocks.AIR.defaultBlockState();
        for(int radius = 1; radius <= maxRadius; ++radius) {
            branchStates[radius] = this.defaultBlockState().setValue(radiusProperty, radius);
        }
        return branchStates;
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(PHASE, PULSING));
    }

    @Override
    public int setRadius(LevelAccessor level, BlockPos pos, int radius, @Nullable Direction originDir, int flags) {
        destroyMode = DynamicTrees.DestroyMode.SET_RADIUS;
        BlockState branchState = level.getBlockState(pos);
        boolean replacingWater = branchState.getFluidState() == Fluids.WATER.getSource(false);
        boolean setWaterlogged = replacingWater && radius <= 7;
        boolean isPulsing = branchState.getBlock() instanceof PhasingBranchBlock && branchState.getValue(PULSING);
        TimePhase phase = getFamily().getCurrentPhase(level);
        level.setBlock(pos, this.getStateForRadius(radius)
                        .setValue(WATERLOGGED, setWaterlogged)
                        .setValue(PULSING, isPulsing)
                        .setValue(PHASE, phase), flags);
        destroyMode = DynamicTrees.DestroyMode.SLOPPY;
        return radius;
    }

    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        super.randomTick(state, worldIn, pos, random);
        TimePhase newPhase = getFamily().getCurrentPhase(worldIn);
        if (newPhase != state.getValue(PHASE)) {
            worldIn.setBlock(pos, state.setValue(PHASE, newPhase), 3);
        }
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState state = super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        TimePhase newPhase = getFamily().getCurrentPhase(worldIn);
        if (newPhase != state.getValue(PHASE)) {
            state = state.setValue(PHASE, newPhase);
        }
        return state;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        int branchRad = TreeHelper.getRadius(level, pos);
        if (state.getValue(PULSING) && random.nextInt(32/branchRad) == 0) {
            int radius = branchRad < 4 ? 1 : 2;
            FxDispatcher.INSTANCE.spellImpact((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, radius, getFamily().getPulseColor());
        }
    }

    @Override
    public PhasingTreeFamily getFamily() {
        return (PhasingTreeFamily)super.getFamily();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (pState.getValue(PHASE) == TimePhase.FULL)
            return super.getOcclusionShape(pState, pLevel, pPos);
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(PHASE) == TimePhase.FULL)
            return super.getVisualShape(pState, pLevel, pPos, pContext);
        return Shapes.empty();
    }

}
