package io.github.pylonmc.pylon.base.content.building;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonJumpableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSneakableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Elevator extends PylonBlock implements PylonSneakableBlock, PylonJumpableBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of(
                    "elevator_range",
                    UnitFormat.BLOCKS.format(getSettings().getOrThrow("range", ConfigAdapter.INT))
            ));
        }
    }

    @SuppressWarnings("unused")
    public Elevator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public Elevator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    private @NotNull List<PylonBlock> getElevatorsInRange(boolean under, @NotNull Location location) {
        int range = getSettings().getOrThrow("range", ConfigAdapter.INT);
        int checkingLevel = 1;
        List<PylonBlock> blocks = new ArrayList<>();

        while (checkingLevel <= range) {
            location.add(0.0, under ? -1.0 : 1.0, 0.0);
            PylonBlock pylonBlock = BlockStorage.get(location.getBlock());
            if (pylonBlock instanceof Elevator) {
                blocks.add(pylonBlock);
            }
            checkingLevel++;
        }

        return blocks;
    }

    private double getDistance(@NotNull Location playerLocation, @NotNull Location elevatorLocation) {
        return elevatorLocation.y() - playerLocation.y();
    }

    private void teleportPlayer(@NotNull Player player, @NotNull Location location, boolean under) {
        List<PylonBlock> elevators = getElevatorsInRange(under, location);

        if (elevators.isEmpty()) {
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.elevator.none_within_range." + (under ? "below" : "above")));
            return;
        }

        PylonBlock elevator = elevators.getFirst();
        double distance = getDistance(player.getLocation(), elevator.getBlock().getLocation());

        player.teleport(player.getLocation().add(0, distance + 1, 0));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    @Override
    public void onSneakStart(@NotNull PlayerToggleSneakEvent event) {
        teleportPlayer(event.getPlayer(), getBlock().getLocation(), true);
    }

    @Override
    public void onJump(@NotNull PlayerJumpEvent event) {
        teleportPlayer(event.getPlayer(), getBlock().getLocation(), false);
    }
}
