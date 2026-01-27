package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.content.machines.fluid.gui.FluidSelector;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.PylonDirectionalBlock;
import io.github.pylonmc.rebar.block.base.PylonFluidBlock;
import io.github.pylonmc.rebar.block.base.PylonGuiBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.datatypes.PylonSerializers;
import io.github.pylonmc.rebar.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.rebar.entity.display.transform.TransformBuilder;
import io.github.pylonmc.rebar.fluid.FluidPointType;
import io.github.pylonmc.rebar.fluid.PylonFluid;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.util.PylonUtils;
import io.github.pylonmc.rebar.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class CreativeFluidSource extends PylonBlock implements
        PylonFluidBlock,
        PylonDirectionalBlock,
        PylonGuiBlock {

    public static final NamespacedKey FLUID_KEY = baseKey("fluid");

    @Nullable public PylonFluid fluid;

    @SuppressWarnings("unused")
    public CreativeFluidSource(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setFacing(context.getFacing());
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.NORTH, context, true, 0.55F);
        addEntity("fluid-1", new ItemDisplayBuilder()
                .material(Material.RED_TERRACOTTA)
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(1.1, 0.8, 0.8)
                )
                .build(getBlock().getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("fluid-2", new ItemDisplayBuilder()
                .material(Material.RED_TERRACOTTA)
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(0.8, 1.1, 0.8)
                )
                .build(getBlock().getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("fluid-3", new ItemDisplayBuilder()
                .material(Material.RED_TERRACOTTA)
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(0.8, 0.8, 1.1)
                )
                .build(getBlock().getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        fluid = null;
    }

    @SuppressWarnings("unused")
    public CreativeFluidSource(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        fluid = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluid);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids() {
        return fluid == null ? Map.of() : Map.of(fluid, 1.0e9);
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {}

    @Override
    public @NotNull Gui createGui() {
        return (FluidSelector.make(() -> fluid, fluid -> {
            this.fluid = fluid;
            ItemStack stack = fluid == null ? new ItemStack(Material.RED_TERRACOTTA) : fluid.getItem();
            getHeldEntityOrThrow(ItemDisplay.class, "fluid-1").setItemStack(stack);
            getHeldEntityOrThrow(ItemDisplay.class, "fluid-2").setItemStack(stack);
            getHeldEntityOrThrow(ItemDisplay.class, "fluid-3").setItemStack(stack);
        }));
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("fluid", fluid == null
                        ? Component.translatable("pylon.pylonbase.fluid.none")
                        : fluid.getName()
                )
        ));
    }
}
