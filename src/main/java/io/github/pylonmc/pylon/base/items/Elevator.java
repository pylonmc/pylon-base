package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonJumpableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSneakableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class Elevator extends PylonBlock implements PylonSneakableBlock, PylonJumpableBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of("elevator_range", Component.text(getRange(getKey())));
        }
    }

    public static final NamespacedKey KEY = pylonKey("elevator");
    public static final NamespacedKey FIRST_KEY = pylonKey("elevator_1");
    public static final NamespacedKey SECOND_KEY = pylonKey("elevator_2");
    public static final NamespacedKey THIRD_KEY = pylonKey("elevator_3");

    public static final Material MATERIAL = Material.QUARTZ_SLAB;

    public static final ItemStack FIRST_STACK = ItemStackBuilder.pylonItem(MATERIAL, FIRST_KEY).build();
    public static final ItemStack SECOND_STACK = ItemStackBuilder.pylonItem(MATERIAL, SECOND_KEY).build();
    public static final ItemStack THIRD_STACK = ItemStackBuilder.pylonItem(MATERIAL, THIRD_KEY).build();

    public static final ElevatorSettings SETTINGS = ElevatorSettings.fromConfig(Settings.get(KEY));

    @SuppressWarnings("unused")
    public Elevator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public Elevator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    public static int getRange(@NotNull NamespacedKey key) {
        if (key.equals(KEY))
            return SETTINGS.elevatorFirstRange();
        if (key.equals(SECOND_KEY))
            return SETTINGS.elevatorSecondRange();
        if (key.equals(THIRD_KEY))
            return SETTINGS.elevatorThirdRange();
        return SETTINGS.elevatorFirstRange();
    }

    private @NotNull List<PylonBlock> getElevatorsInRange(boolean under, @NotNull Location location) {
        int range = getRange(getKey());
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
            player.sendActionBar(under ? Component.translatable("pylon.pylonbase.message.elevator.none_within_range.below") :
                    Component.translatable("pylon.pylonbase.message.elevator.none_within_range.above"));
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
