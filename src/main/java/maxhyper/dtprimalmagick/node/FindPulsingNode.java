package maxhyper.dtprimalmagick.node;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.NodeInspector;
import maxhyper.dtprimalmagick.blocks.PhasingBranchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class FindPulsingNode implements NodeInspector {
    private final Set<BlockPos> pulsingBranches;
    private final int minRadius;

    public FindPulsingNode(int minRadiusToConsider) {
        this.pulsingBranches = new HashSet<>();
        this.minRadius = minRadiusToConsider;
    }

    public boolean run(BlockState state, LevelAccessor level, BlockPos pos, Direction fromDir) {
        if (state.hasProperty(PhasingBranchBlock.PULSING) && state.getValue(PhasingBranchBlock.PULSING)){
            int rad = TreeHelper.getRadius(level, pos);
            if (rad >= minRadius || rad >= 8 || level.getRandom().nextInt(8 - rad) == 0){
                pulsingBranches.add(pos);
            }
        }
        return true;
    }

    public boolean returnRun(BlockState state, LevelAccessor level, BlockPos pos, Direction fromDir) {
        return false;
    }

    public Set<BlockPos> getPulsingBranches() {
        return pulsingBranches;
    }
}
