package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidVoider extends PylonBlock implements PylonFluidIoBlock {

    public static class Item extends PylonItem {

        public final double voidRate = getSettings().getOrThrow("fluid-voided-per-second", Double.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "void_rate", UnitFormat.MILLIBUCKETS_PER_SECOND.format(Math.round(voidRate))
            );
        }
    }

    public static final NamespacedKey FLUID_VOIDER_1_KEY = pylonKey("fluid_voider_1");
    public static final NamespacedKey FLUID_VOIDER_2_KEY= pylonKey("fluid_voider_2");
    public static final NamespacedKey FLUID_VOIDER_3_KEY= pylonKey("fluid_voider_3");

    private static final Material MAIN_MATERIAL = Material.BLACK_TERRACOTTA;

    public final double voidRate = getSettings().getOrThrow("fluid-voided-per-second", Double.class);
    public final double mainDisplaySize = getSettings().getOrThrow("main-display-size", Double.class);


    @SuppressWarnings("unused")
    public FluidVoider(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public FluidVoider(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.UP, (float) (mainDisplaySize / 2.0)));
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        Map<String, PylonEntity<?>> entities = PylonFluidIoBlock.super.createEntities(context);
        @NotNull Block block = getBlock();
        entities.put("main", new MainDisplay(block, mainDisplaySize));
        return entities;
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return PylonRegistry.FLUIDS.getValues()
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> voidRate * deltaSeconds * PylonConfig.getFluidIntervalTicks()));
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        // do nothing lol
    }

    public static class MainDisplay extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("fluid_voider_main_display");

        @SuppressWarnings("unused")
        public MainDisplay(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public MainDisplay(@NotNull Block block, double mainDisplaySize) {
            super(KEY, new ItemDisplayBuilder()
                    .material(MAIN_MATERIAL)
                    .transformation(new TransformBuilder()
                            .scale(mainDisplaySize)
                    )
                    .build(block.getLocation().toCenterLocation())
            );
        }
    }
}
