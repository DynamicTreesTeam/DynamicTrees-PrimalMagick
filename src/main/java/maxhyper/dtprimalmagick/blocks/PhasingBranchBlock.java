package maxhyper.dtprimalmagick.blocks;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.network.NodeInspector;
import com.ferreusveritas.dynamictrees.block.branch.ThickBranchBlock;
import com.ferreusveritas.dynamictrees.systems.nodemapper.SpeciesNode;
import com.ferreusveritas.dynamictrees.util.BranchDestructionData;
import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.client.fx.particles.ParticleTypesPM;
import com.verdantartifice.primalmagick.common.blocks.trees.AbstractPhasingLogBlock;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import maxhyper.dtprimalmagick.node.FindPulsingNode;
import maxhyper.dtprimalmagick.trees.PhasingTreeFamily;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.awt.*;

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
        boolean isPulsing = branchState.getBlock() instanceof PhasingBranchBlock ?
                branchState.getValue(PULSING) : (level.getRandom().nextInt(getFamily().getChanceToPulse()) == 0);
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

        int branchRad = TreeHelper.getRadius(level, pos);
        int chance = 32/branchRad;
        if (state.getValue(PULSING) && random.nextInt(chance) == 0) {
            float radius = branchRad/4f;
            spellImpact(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, getFamily().getPulseColor(), level);
        }
        super.animateTick(state, level, pos, random);
    }

    private void spellImpact(double x, double y, double z, float radius, int color, Level level) {
        Color c = new Color(color);
        float r = (float)c.getRed() / 255.0F;
        float g = (float)c.getGreen() / 255.0F;
        float b = (float)c.getBlue() / 255.0F;
        this.spellImpact(x, y, z, radius, r, g, b, level);
    }

    private void spellImpact(double x, double y, double z, float radius, float r, float g, float b, Level world) {
        Minecraft mc = Minecraft.getInstance();
        RandomSource rng = world.random;
        int count = (int)((15 + rng.nextInt(11)) * radius);

        for(int index = 0; index < count; ++index) {
            double dx = (double)rng.nextFloat() * 0.035 * (double)radius * (double)(rng.nextBoolean() ? 1 : -1);
            double dy = (double)rng.nextFloat() * 0.035 * (double)radius * (double)(rng.nextBoolean() ? 1 : -1);
            double dz = (double)rng.nextFloat() * 0.035 * (double)radius * (double)(rng.nextBoolean() ? 1 : -1);
            Vector3d dir = new Vector3d(dx, dy, dz).normalize();
            Particle p = mc.particleEngine.createParticle(ParticleTypesPM.SPELL_SPARKLE.get(),
                    x+(dir.x*radius/4), y+(dir.y*radius/4), z+(dir.z*radius/4),
                    dx/2, dy/2, dz/2);
            if (p != null) {
                p.setColor(r, g, b);
            }
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

    @Override
    public BranchDestructionData destroyBranchFromNode(Level level, BlockPos cutPos, Direction toolDir, boolean wholeTree, @org.jetbrains.annotations.Nullable LivingEntity entity) {
        //Drop heartwood from pulsing branches
        BlockState blockState = level.getBlockState(cutPos);
        FindPulsingNode findPulsing = new FindPulsingNode(getFamily().getMinimumRadiusToDropPulsing());
        this.analyse(blockState, level, cutPos, null, new MapSignal(findPulsing));
        for (BlockPos pulsingPos : findPulsing.getPulsingBranches()){
            ItemStack drops = getFamily().getPulsingDrops();
            Entity itemDrops = new ItemEntity(level, pulsingPos.getX()+0.5f, pulsingPos.getY()+0.5f, pulsingPos.getZ()+0.5f, drops);
            level.addFreshEntity(itemDrops);
        }
        return super.destroyBranchFromNode(level, cutPos, toolDir, wholeTree, entity);
    }
}
