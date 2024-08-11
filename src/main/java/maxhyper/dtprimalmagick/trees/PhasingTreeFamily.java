package maxhyper.dtprimalmagick.trees;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.branch.BasicBranchBlock;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;
import com.verdantartifice.primalmagick.common.sources.Source;
import maxhyper.dtprimalmagick.blocks.PhasingBranchBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.Locale;
import java.util.function.Function;

public class PhasingTreeFamily extends Family {

    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(PhasingTreeFamily::new);

    private int pulseColor = Source.SUN.getColor();
    private int chanceToPulse = 40;
    private int minimumRadiusToDropPulsing = 4;
    private PhaseSync phaseSync = PhaseSync.SUN;
    private ItemStack pulsingDrops = ItemStack.EMPTY;

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

    public void setPulseColor (int color){
        this.pulseColor = color;
    }

    public int getPulseColor() {
        return pulseColor;
    }

    public void setChanceToPulse (int chance){
        this.chanceToPulse = chance;
    }

    public int getChanceToPulse() {
        return chanceToPulse;
    }

    public void setPulsingDrops(ItemStack pulsingDrops) {
        this.pulsingDrops = pulsingDrops;
    }

    public ItemStack getPulsingDrops() {
        return pulsingDrops;
    }

    public void setMinimumRadiusToDropPulsing(int minimumRad) {
        this.minimumRadiusToDropPulsing = minimumRad;
    }

    public int getMinimumRadiusToDropPulsing() {
        return minimumRadiusToDropPulsing;
    }

    public TimePhase getCurrentPhase(LevelAccessor level){
        return phaseSync.getPhase(level);
    }

    public void setPhaseSync (String phase){
        this.phaseSync = PhasingTreeFamily.PhaseSync.valueOf(phase.toUpperCase(Locale.ENGLISH));
    }

}
