package maxhyper.dtarsnouveau.init;

import com.ferreusveritas.dynamictrees.api.registry.TypeRegistryEvent;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.block.rooty.SoilProperties;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import maxhyper.dtarsnouveau.DynamicTreesPrimalMagick;
import maxhyper.dtarsnouveau.trees.PhasingLeavesProperties;
import maxhyper.dtarsnouveau.trees.PhasingTreeFamily;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTPrimalMagickRegistries {

    @SubscribeEvent
    public static void registerFamilyTypes (final TypeRegistryEvent<Family> event) {
        event.registerType(DynamicTreesPrimalMagick.location("phasing"), PhasingTreeFamily.TYPE);
    }

    @SubscribeEvent
    public static void registerLeavesPropertiesTypes (final TypeRegistryEvent<LeavesProperties> event) {
        event.registerType(DynamicTreesPrimalMagick.location("phasing"), PhasingLeavesProperties.TYPE);
    }

}
