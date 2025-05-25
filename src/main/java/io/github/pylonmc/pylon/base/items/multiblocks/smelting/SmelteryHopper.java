package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.CastableFluid;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

@NullMarked
public final class SmelteryHopper extends SmelteryComponent implements PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_hopper");

    private static final Map<ItemStack, PylonFluid> CASTABLE_FLUIDS = PylonRegistry.FLUIDS.getValues().stream()
            .filter(f -> f.hasTag(CastableFluid.class))
            .collect(Collectors.toMap(
                    f -> f.getTag(CastableFluid.class).castResult(),
                    Function.identity()
            ));

    public SmelteryHopper(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public SmelteryHopper(Block block, PersistentDataContainer pdc) {
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
            PylonFluid fluid = CASTABLE_FLUIDS.get(item);
            if (fluid == null) continue;
            double temperature = fluid.getTag(FluidTemperature.class).getTemperature();
            if (controller.getTemperature() >= temperature) {
                controller.addFluid(fluid, 1000);
                item.subtract();
            }
        }
    }
}
