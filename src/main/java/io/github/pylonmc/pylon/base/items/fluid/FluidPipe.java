package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.base.misc.PipeConnectorService;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.EntityInteractor;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class FluidPipe extends PylonItem<PylonItemSchema> implements EntityInteractor {

    public FluidPipe(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public void onUsedToRightClickEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!(EntityStorage.get(event.getRightClicked()) instanceof FluidConnectionInteraction interaction)) {
            return;
        }

        if (PipeConnectorService.getOrigin(event.getPlayer()) != null) {
            PipeConnectorService.finishConnection(event.getPlayer(), interaction.getPoint());
        } else {
            PipeConnectorService.startConnection(event.getPlayer(), interaction.getPoint());
        }
    }
}
