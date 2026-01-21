package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ItemMagnet extends PylonItem implements PylonInteractor {
    @Getter
    private final double pickupDistance = getSettings().getOrThrow("pickup-distance", ConfigAdapter.DOUBLE);
    @Getter
    private final double attractForce = getSettings().getOrThrow("attract-force", ConfigAdapter.DOUBLE);

    private static final NamespacedKey ENABLED_KEY = BaseUtils.baseKey("item_magnet_toggler");

    public ItemMagnet(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("pickup-distance", UnitFormat.BLOCKS.format(pickupDistance)));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        ItemStack stack = event.getItem();
        if (stack == null) return; // should never happen

        Player player = event.getPlayer();
        stack.editPersistentDataContainer(pdc -> {
            boolean enabled;
            if (!pdc.has(ENABLED_KEY)) {
                enabled = false;
            } else {
                enabled = pdc.get(ENABLED_KEY, PersistentDataType.BOOLEAN) != Boolean.TRUE;
            }

            String targetKey = enabled ? "enabled" : "disabled";
            player.sendMessage(Component.translatable("pylon.pylonbase.message.item_magnet." + targetKey));

            pdc.set(ENABLED_KEY, PersistentDataType.BOOLEAN, enabled);
        });

        CustomModelData data = stack.getDataOrDefault(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().build());
        CustomModelData newData = CustomModelData.customModelData()
                .addStrings(data.strings())
                .addFloats(data.floats())
                .addColors(data.colors())
                .addFlag(isEnabled())
                .build();
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, newData);
    }

    /**
     * Checks if an item magnet is enabled
     *
     * @return true is enabled else otherwise
     */
    public boolean isEnabled() {
        PersistentDataContainerView pdc = getStack().getPersistentDataContainer();

        if (!pdc.has(ENABLED_KEY)) {
            return true;
        }

        return pdc.get(ENABLED_KEY, PersistentDataType.BOOLEAN) == Boolean.TRUE;
    }

    public static class Ticker extends BukkitRunnable {

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Vector playerPosition = player.getLocation().toVector();
                for (var stack : player.getInventory()) {
                    PylonItem pylonItem = fromStack(stack);
                    if (!(pylonItem instanceof ItemMagnet itemMagnet)) continue;

                    if (!itemMagnet.isEnabled()) continue;

                    Collection<Item> nearbyItems = player.getLocation().getNearbyEntitiesByType(
                            Item.class,
                            itemMagnet.getPickupDistance()
                    );


                    for (Item item : nearbyItems) {
                        if (item.getPickupDelay() > 0) continue;

                        Vector direction = playerPosition.clone().subtract(item.getLocation().toVector()).normalize();

                        // it is near enough
                        if (direction.distanceSquared(playerPosition) < 0.25) continue;

                        Vector toMove = direction.multiply(itemMagnet.getAttractForce());
                        item.setVelocity(toMove);
                    }

                    break;
                }
            }
        }
    }
}
