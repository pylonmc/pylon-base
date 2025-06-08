package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonJumpableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSneakableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
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

import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class Elevator extends PylonBlock implements PylonSneakableBlock, PylonJumpableBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }
    }

    public static final NamespacedKey KEY = pylonKey("elevator");
    public static final NamespacedKey SECOND_KEY = pylonKey("elevator_second");
    public static final NamespacedKey THIRD_KEY = pylonKey("elevator_third");

    public static final Material MATERIAL = Material.QUARTZ_SLAB;

    public static final ItemStack STACK = ItemStackBuilder.pylonItem(MATERIAL, KEY).build();
    public static final ItemStack SECOND_STACK = ItemStackBuilder.pylonItem(MATERIAL, SECOND_KEY).build();
    public static final ItemStack THIRD_STACK = ItemStackBuilder.pylonItem(MATERIAL, THIRD_KEY).build();

    @SuppressWarnings("unused")
    public Elevator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public Elevator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    private int getRange() {
        NamespacedKey key = this.getKey();

        if (key == KEY)
            return 5;
        if (key == SECOND_KEY)
            return 15;
        if (key == THIRD_KEY)
            return 384; // 320 + 64

        return 5;
    }

    private @NotNull List<PylonBlock> getElevatorsInRange(boolean under, @NotNull Location location) {
        int range = getRange();
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

    @Override
    public void onSneakStart(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location location = getBlock().getLocation();

        List<PylonBlock> elevators = getElevatorsInRange(true, location);

        if (elevators.isEmpty()) {
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.elevator.none_within_range.below"));
            return;
        }

        PylonBlock elevator = elevators.getFirst();
        double distance = getDistance(player.getLocation(), elevator.getBlock().getLocation());

        player.teleport(player.getLocation().add(0, distance + 1, 0));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    @Override
    public void onJump(@NotNull PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Location location = getBlock().getLocation();

        List<PylonBlock> elevators = getElevatorsInRange(false, location);

        if (elevators.isEmpty()) {
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.elevator.none_within_range.above"));
            return;
        }

        PylonBlock elevator = elevators.getFirst();
        double distance = getDistance(player.getLocation(), elevator.getBlock().getLocation());

        player.teleport(player.getLocation().add(0, distance + 1, 0));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }
}
