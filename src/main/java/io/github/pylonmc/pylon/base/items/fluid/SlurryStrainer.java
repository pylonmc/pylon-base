package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.fluid.Slurry;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public class SlurryStrainer extends PylonBlock implements PylonFluidIoBlock, PylonTickingBlock, PylonGuiBlock {

    public static final NamespacedKey KEY = pylonKey("slurry_strainer");

    public static final double BUFFER_SIZE = Settings.get(KEY).getOrThrow("buffer-size", Double.class);

    private static final NamespacedKey CURRENT_FLUID_KEY = pylonKey("current_fluid");
    private static final NamespacedKey BUFFER_KEY = pylonKey("buffer");
    private static final NamespacedKey PASSED_FLUID_KEY = pylonKey("passed_fluid");

    private @Nullable Slurry slurry;
    private double buffer;
    private double passedFluid;

    @SuppressWarnings("unused")
    public SlurryStrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        slurry = null;
        buffer = 0;
        passedFluid = 0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public SlurryStrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        slurry = (Slurry) pdc.get(CURRENT_FLUID_KEY, PylonSerializers.PYLON_FLUID);
        buffer = pdc.get(BUFFER_KEY, PylonSerializers.DOUBLE);
        passedFluid = pdc.get(PASSED_FLUID_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, CURRENT_FLUID_KEY, PylonSerializers.PYLON_FLUID, slurry);
        pdc.set(BUFFER_KEY, PylonSerializers.DOUBLE, buffer);
        pdc.set(PASSED_FLUID_KEY, PylonSerializers.DOUBLE, passedFluid);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH),
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH)
        );
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.SLURRY, buffer);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return PylonRegistry.FLUIDS.stream()
                .filter(fluid -> fluid instanceof Slurry)
                .collect(Collectors.toMap(Function.identity(), f -> BUFFER_SIZE - buffer));
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        if (!fluid.equals(slurry)) {
            passedFluid = 0;
            slurry = (Slurry) fluid;
        }
        buffer += amount;
        passedFluid += amount;
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        buffer -= amount;
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(
                getName(),
                Map.of("info", slurry == null ?
                        Component.empty() :
                        Component.translatable("pylon.pylonbase.waila.slurry_strainer.straining",
                                PylonArgument.of("item", slurry.getSlurryMaterial().effectiveName()),
                                PylonArgument.of("progress", UnitFormat.PERCENT.format(passedFluid / 10)
                                        .decimalPlaces(0))
                        )
                )
        );
    }

    private final VirtualInventory inventory = new VirtualInventory(9 * 3);

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . ."
                )
                .addIngredient('.', inventory)
                .build();
    }

    @Override
    public void tick(double deltaSeconds) {
        if (slurry != null && passedFluid >= 1000) {
            inventory.addItem(null, slurry.getSlurryMaterial().clone());
            passedFluid -= 1000;
            if (passedFluid == 0) {
                slurry = null;
            }
        }
    }
}
