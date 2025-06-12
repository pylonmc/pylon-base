package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.CastableFluid;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public final class SmelteryHopper extends SmelteryComponent implements PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_hopper");

    private static final Map<PylonFluid, CastableFluid> CASTABLE_FLUIDS = PylonRegistry.FLUIDS.getValues().stream()
            .filter(f -> f.hasTag(CastableFluid.class))
            .collect(Collectors.toMap(
                    Function.identity(),
                    f -> f.getTag(CastableFluid.class)
            ));

    @SuppressWarnings("unused")
    public SmelteryHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public SmelteryHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return 2;
    }

    @Override
    public void tick(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) return;
        Hopper hopper = (Hopper) getBlock().getState(false);
        for (ItemStack item : hopper.getInventory().getContents()) {
            if (item == null) continue;
            PylonFluid fluid = null;
            double temperature = Double.NaN;
            for (var entry : CASTABLE_FLUIDS.entrySet()) {
                CastableFluid castableFluid = entry.getValue();
                if (castableFluid.castResult().isSimilar(item)) {
                    fluid = entry.getKey();
                    temperature = castableFluid.castTemperature();
                    break;
                }
            }
            if (fluid == null) continue;
            if (controller.getTemperature() >= temperature) {
                controller.addFluid(fluid, 1000);
                item.subtract();
            }
        }
    }
}
