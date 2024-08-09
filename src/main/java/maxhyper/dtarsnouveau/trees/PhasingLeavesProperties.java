package maxhyper.dtarsnouveau.trees;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.leaves.DynamicLeavesBlock;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import maxhyper.dtarsnouveau.blocks.PhasingBranchBlock;
import maxhyper.dtarsnouveau.blocks.PhasingLeavesBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Locale;

public class PhasingLeavesProperties extends LeavesProperties {

    public static final TypedRegistry.EntryType<LeavesProperties> TYPE = TypedRegistry.newType(PhasingLeavesProperties::new);

    private PhasingTreeFamily.PhaseSync phaseSync = PhasingTreeFamily.PhaseSync.SUN;

    public PhasingLeavesProperties(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected DynamicLeavesBlock createDynamicLeaves(BlockBehaviour.Properties properties) {
        return new PhasingLeavesBlock(this, properties);
    }

    public TimePhase getCurrentPhase(LevelAccessor level){
        return phaseSync.getPhase(level);
    }

    public void setPhaseSync (String phase){
        this.phaseSync = PhasingTreeFamily.PhaseSync.valueOf(phase.toUpperCase(Locale.ENGLISH));
    }
}
