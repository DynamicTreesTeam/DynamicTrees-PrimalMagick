package maxhyper.dtprimalmagick.trees;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.branch.BasicBranchBlock;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import com.verdantartifice.primalmagick.common.sources.Source;
import maxhyper.dtprimalmagick.blocks.PhasingBranchBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;

import java.util.Locale;
import java.util.function.Function;

public class PhasingTreeFamily extends Family {

    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(PhasingTreeFamily::new);
    private int pulseColor = Source.SUN.getColor();
    private PhaseSync phaseSync = PhaseSync.SUN;

    public enum PhaseSync {
        SUN (TimePhase::getSunPhase),
        MOON (TimePhase::getMoonPhase);
        private final Function<LevelAccessor, TimePhase> syncGetter;
        PhaseSync (Function<LevelAccessor, TimePhase> syncGetter){
            this.syncGetter = syncGetter;
        }
        public TimePhase getPhase (LevelAccessor accessor){
            return syncGetter.apply(accessor);
        }
    }
    public PhasingTreeFamily(ResourceLocation name) {
        super(name);
    }

    @Override
    protected BranchBlock createBranchBlock(ResourceLocation name) {
        BasicBranchBlock branch = new PhasingBranchBlock(name, this.getProperties());
        if (this.isFireProof()) branch.setFireSpreadSpeed(0).setFlammability(0);
        return branch;
    }

    public int getPulseColor() {
        return pulseColor;
    }

    public void setPulseColor (int color){
        this.pulseColor = color;
    }

    public TimePhase getCurrentPhase(LevelAccessor level){
        return phaseSync.getPhase(level);
    }

    public void setPhaseSync (String phase){
        this.phaseSync = PhasingTreeFamily.PhaseSync.valueOf(phase.toUpperCase(Locale.ENGLISH));
    }
}
