package io.github.pylonmc.pylon.base.content.components;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.fluid.FluidTankCasing;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.waila.Waila;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public abstract class FluidHatch extends PylonBlock implements
        PylonFluidBufferBlock,
        PylonSimpleMultiblock,
        PylonDirectionalBlock {

    private static final NamespacedKey FLUID_KEY = baseKey("fluid");

    private static MixedMultiblockComponent component = null;

    public @Nullable PylonFluid fluid;

    static {
        // run on first tick after all addons registered
        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
            List<PylonMultiblockComponent> components = new ArrayList<>();
            for (PylonItemSchema schema : PylonRegistry.ITEMS) {
                if (PylonItem.fromStack(schema.getItemStack()) instanceof FluidTankCasing.Item) {
                    components.add(new PylonMultiblockComponent(schema.getKey()));
                }
            }
            component = new MixedMultiblockComponent(components);
        }, 0);
    }

    public FluidHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        fluid = null;
        setFacing(context.getFacing());
        addEntity("fluid", new ItemDisplayBuilder()
                .transformation(new TransformBuilder().scale(0))
                .build(getBlock().getLocation().toCenterLocation().add(0, 1, 0))
        );
    }

    public FluidHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        fluid = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluid);
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();
        components.put(new Vector3i(0, 1, 0), component);
        return components;
    }

    @Override
    public boolean checkFormed() {
        boolean formed = PylonSimpleMultiblock.super.checkFormed();
        if (formed) {
            FluidTankCasing casing = BlockStorage.getAs(FluidTankCasing.class, getBlock().getRelative(BlockFace.UP));
            Preconditions.checkState(casing != null);
            Waila.addWailaOverride(casing.getBlock(), this::getWaila);
            if (fluid != null) {
                setFluidCapacity(fluid, casing.capacity);
            }
        }
        return formed;
    }

    @Override
    public void onMultiblockUnformed(boolean partUnloaded) {
        PylonSimpleMultiblock.super.onMultiblockUnformed(partUnloaded);
        Waila.removeWailaOverride(getBlock().getRelative(BlockFace.UP));
        if (fluid != null) {
            setFluidCapacity(fluid, 0);
            getFluidDisplay().setTransformationMatrix(new TransformBuilder()
                    .scale(0, 0, 0)
                    .buildForItemDisplay()
            );
        }
    }

    @Override
    public boolean setFluid(@NotNull PylonFluid fluid, double amount) {
        boolean result = PylonFluidBufferBlock.super.setFluid(fluid, amount);
        float scale = (float) (0.9 * fluidAmount(fluid) / fluidCapacity(fluid));
        if (scale < 1.0e-9) {
            scale = 0.0F;
        }
        getFluidDisplay().setItemStack(fluid.getItem());
        getFluidDisplay().setTransformationMatrix(new TransformBuilder()
                .translate(0.0, -0.45 + scale / 2, 0.0)
                .scale(0.9, scale, 0.9)
                .buildForItemDisplay()
        );
        return result;
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        Component info;
        if (!isFormedAndFullyLoaded()) {
            info = Component.translatable("pylon.pylonbase.message.fluid_hatch.no_casing");
        } else if (fluid == null) {
            info = Component.translatable("pylon.pylonbase.message.fluid_hatch.no_multiblock");
        } else {
            info = Component.translatable("pylon.pylonbase.message.fluid_hatch.working")
                    .arguments(
                            PylonArgument.of("bars", BaseUtils.createFluidAmountBar(
                                    fluidAmount(fluid),
                                    fluidCapacity(fluid),
                                    20,
                                    TextColor.color(200, 255, 255)
                            )),
                            PylonArgument.of("fluid", fluid.getName())
                    );
        }
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("info", info)
        ));
    }

    public void setFluidType(PylonFluid fluid) {
        if (this.fluid == fluid) {
            return;
        }
        
        if (this.fluid != null) {
            deleteFluidBuffer(this.fluid);
            getFluidDisplay().setTransformationMatrix(new TransformBuilder()
                    .scale(0, 0, 0)
                    .buildForItemDisplay()
            );
        }
        this.fluid = fluid;
        if (fluid != null) {
            createFluidBuffer(fluid, 0, true, true);
        }
        if (isFormedAndFullyLoaded() && fluid != null) {
            FluidTankCasing casing = BlockStorage.getAs(FluidTankCasing.class, getBlock().getRelative(BlockFace.UP));
            Preconditions.checkState(casing != null);
            setFluidCapacity(fluid, casing.capacity);
        }
    }

    public @NotNull ItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "fluid");
    }

    @Override
    public void postBreak(@NotNull BlockBreakContext context) {
        PylonFluidBufferBlock.super.postBreak(context);
        Waila.removeWailaOverride(getBlock().getRelative(BlockFace.UP));
    }
}
