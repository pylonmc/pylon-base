package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FluidVoider extends PylonBlock implements PylonFluidBlock, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        public final double voidRate = getSettings().getOrThrow("fluid-voided-per-second", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of(
                    "void_rate", UnitFormat.MILLIBUCKETS_PER_SECOND.format(voidRate).decimalPlaces(0)
            ));
        }
    }

    public final Material mainMaterial = getSettings().getOrThrow("main-material", ConfigAdapter.MATERIAL);
    public final double voidRate = getSettings().getOrThrow("fluid-voided-per-second", ConfigAdapter.DOUBLE);
    public final double mainDisplaySize = getSettings().getOrThrow("main-display-size", ConfigAdapter.DOUBLE);

    @SuppressWarnings("unused")
    public FluidVoider(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.UP, (float) (mainDisplaySize / 2.0)));
        addEntity("main", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(mainMaterial)
                        .addCustomModelDataString(getKey() + ":main")
                        .build()
                )
                .transformation(new TransformBuilder()
                        .scale(mainDisplaySize)
                )
                .build(getBlock().getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public FluidVoider(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid, double deltaSeconds) {
        return voidRate * deltaSeconds;
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        // do nothing lol
    }
}
