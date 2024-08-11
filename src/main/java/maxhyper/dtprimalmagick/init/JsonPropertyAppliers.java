package maxhyper.dtprimalmagick.init;

import com.ferreusveritas.dynamictrees.api.applier.ApplierRegistryEvent;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.deserialisation.PropertyAppliers;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.google.gson.JsonElement;
import maxhyper.dtprimalmagick.DynamicTreesPrimalMagick;
import maxhyper.dtprimalmagick.trees.PhasingLeavesProperties;
import maxhyper.dtprimalmagick.trees.PhasingTreeFamily;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DynamicTreesPrimalMagick.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JsonPropertyAppliers {

    //FAMILY
    @SubscribeEvent
    public static void registerReloadAppliersFamily(final ApplierRegistryEvent.Reload<Family, JsonElement> event) {
        registerFamilyAppliers(event.getAppliers());
    }
    public static void registerFamilyAppliers(PropertyAppliers<Family, JsonElement> appliers) {
        appliers.register("phase_sync", PhasingTreeFamily.class, String.class, PhasingTreeFamily::setPhaseSync)
                .register("pulse_color", PhasingTreeFamily.class, Integer.class, PhasingTreeFamily::setPulseColor)
                .register("chance_to_pulse", PhasingTreeFamily.class, Integer.class, PhasingTreeFamily::setPulseColor)
                .register("pulsing_branch_item", PhasingTreeFamily.class, Item.class, (family, item) -> family.setPulsingDrops(new ItemStack(item)));
    }

    //LEAVES
    @SubscribeEvent
    public static void registerReloadAppliersLeavesProperties(final ApplierRegistryEvent.Reload<LeavesProperties, JsonElement> event) {
        registerLeavesPropertiesAppliers(event.getAppliers());
    }

    public static void registerLeavesPropertiesAppliers(PropertyAppliers<LeavesProperties, JsonElement> appliers) {
        appliers.register("phase_sync", PhasingLeavesProperties.class, String.class, PhasingLeavesProperties::setPhaseSync);
    }

}
