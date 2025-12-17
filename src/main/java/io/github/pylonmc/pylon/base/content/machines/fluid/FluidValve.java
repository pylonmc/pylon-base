package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class FluidValve extends PylonBlock implements PylonFluidTank, PylonInteractBlock {

    public static final NamespacedKey ENABLED_KEY = baseKey("enabled");

    private static final int BRIGHTNESS_OFF = 6;
    private static final int BRIGHTNESS_ON = 13;

    private boolean enabled;

    @SuppressWarnings("unused")
    public FluidValve(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();
        createFluidPoint(FluidPointType.INPUT, BlockFace.EAST, context, false, 0.25F);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.WEST, context, false, 0.25F);
        addEntity("main", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.WHITE_CONCRETE)
                        .addCustomModelDataString(getKey() + ":main")
                        .build()
                )
                .brightness(BRIGHTNESS_OFF)
                .transformation(new TransformBuilder()
                        .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST, false).getDirection().toVector3d())
                        .scale(0.25, 0.25, 0.5)
                )
                .build(getBlock().getLocation().toCenterLocation())
        );

        enabled = false;
        setDisableBlockTextureEntity(true);
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidValve(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        enabled = pdc.get(ENABLED_KEY, PylonSerializers.BOOLEAN);
        setDisableBlockTextureEntity(true);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(ENABLED_KEY, PylonSerializers.BOOLEAN, enabled);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        event.setUseItemInHand(Event.Result.DENY);

        enabled = !enabled;

        getHeldEntityOrThrow(ItemDisplay.class, "main")
                .setBrightness(new Display.Brightness(0, enabled ? BRIGHTNESS_ON : BRIGHTNESS_OFF));
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(PylonArgument.of(
                "status",
                Component.translatable("pylon.pylonbase.message.valve." + (enabled ? "enabled" : "disabled"))
        )));
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid) {
        if (!enabled) {
            return 0.0;
        }
        return PylonFluidTank.super.fluidAmountRequested(fluid);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids() {
        if (!enabled) {
            return Map.of();
        }
        return PylonFluidTank.super.getSuppliedFluids();
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }
}
