package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class FluidVoider extends PylonBlock implements PylonFluidBlock, PylonEntityHolderBlock {

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
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.UP, (float) (mainDisplaySize / 2.0)),
                "main", new SimpleItemDisplay(new ItemDisplayBuilder()
                        .material(MAIN_MATERIAL)
                        .transformation(new TransformBuilder()
                                .scale(mainDisplaySize)
                        )
                        .build(getBlock().getLocation().toCenterLocation())
                )
        );
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(double deltaSeconds) {
        return PylonRegistry.FLUIDS.getValues()
                .stream()
                .collect(Collectors.toMap(Function.identity(), key -> voidRate * deltaSeconds * PylonConfig.getFluidIntervalTicks()));
    }

    @Override
    public void addFluid(@NotNull PylonFluid fluid, double amount) {
        // do nothing lol
    }
}
