package io.github.pylonmc.pylon.base.content.science;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonConsumable;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@SuppressWarnings("UnstableApiUsage")
public class Loupe extends PylonItem implements PylonInteractor, PylonConsumable {

    private static final NamespacedKey CONSUMED_KEY = new NamespacedKey(PylonBase.getInstance(), "consumed");
    private static final PersistentDataType<PersistentDataContainer, Map<Material, Integer>> CONSUMED_TYPE =
            PylonSerializers.MAP.mapTypeFrom(
                    PylonSerializers.KEYED.keyedTypeFrom(Material.class, Registry.MATERIAL::getOrThrow),
                    PylonSerializers.INTEGER
            );

    @Getter
    private static final Map<ItemRarity, ItemConfig> itemConfigs = new EnumMap<>(ItemRarity.class);

    static {
        for (ItemRarity rarity : ItemRarity.values()) {
            itemConfigs.put(
                    rarity,
                    ItemConfig.loadFrom(Settings.get(BaseKeys.LOUPE).getSectionOrThrow(rarity.name().toLowerCase(Locale.ROOT)))
            );
        }
    }

    public Loupe(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        var items = player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of());
        if (offhand.isEmpty()) {
            // get scanned block
            Block toScan = player.getTargetBlockExact(5, FluidCollisionMode.SOURCE_ONLY);

            // nothing found or scanning air
            if (toScan == null || toScan.getType().isAir()) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.nothing"));
                event.setCancelled(true);
                return;
            }

            // scan for entities first and process the first one found only
            var entityItemType = hasValidItem(toScan, items);
            if (entityItemType != null) {
                ItemStack stack = entityItemType.getItemStack();
                if (PylonItem.fromStack(stack) != null) {
                    player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.is_pylon"));
                    event.setCancelled(true);
                    return;
                }

                boolean invalid = processMaterial(stack.getType(), player);
                event.setCancelled(invalid);
                return;
            }

            // process block aimed at
            if (BlockStorage.get(toScan) != null) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.is_pylon"));
                event.setCancelled(true);
                return;
            }

            Material blockType = toScan.getType();
            event.setCancelled(processMaterial(blockType, player));
            return;
        }

        if (PylonItem.fromStack(offhand) != null) {
            player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.is_pylon"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(processMaterial(offhand.getType(), player));
    }

    private static boolean processMaterial(Material type, Player player) {
        var items = player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of());
        ItemRarity rarity = type.getDefaultData(DataComponentTypes.RARITY);
        int maxUses = itemConfigs.get(rarity).uses;

        if (items.getOrDefault(type, 0) >= maxUses) {
            player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.already_examined"));
            return true;
        }

        return false;
    }

    @Override
    public void onConsumed(@NotNull PlayerItemConsumeEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        var items = new HashMap<>(player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of()));

        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand.isEmpty()) {
            Block toScan = player.getTargetBlockExact(5, FluidCollisionMode.SOURCE_ONLY);

            if (toScan == null || toScan.getType().isAir()) return;

            // scan for entities first and process the first one found only
            org.bukkit.entity.Item entityItem = hasValidItem(toScan, items);
            if (entityItem != null) {
                ItemStack stack = entityItem.getItemStack();
                if(addPoints(stack.getType(), stack.effectiveName(), player)) return;

                if (stack.getAmount() == 1) {
                    entityItem.remove();
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                    entityItem.setItemStack(stack);
                }

                return;
            }

            // process block aimed at
            Material blockType = toScan.getType();
            BlockType bt = blockType.asBlockType();
            if (bt == null) return; // shouldn't happen

            if (addPoints(blockType, Component.translatable(bt.translationKey()), player)) return;
            if (blockType.getHardness() > 0f) { // filter out unbreakable blocks
                toScan.setType(Material.AIR);
                new BlockBreakEvent(toScan, player).callEvent();
            }
        }

        if (addPoints(offhand.getType(), offhand.effectiveName(), player)) return;
        offhand.subtract();
    }

    private static boolean addPoints(Material type, Component name, Player player) {
        var items = new HashMap<>(player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of()));
        ItemConfig config = itemConfigs.get(type.getDefaultData(DataComponentTypes.RARITY));

        int currentUses = items.getOrDefault(type, 0);
        if (currentUses >= config.uses) return true; // This should never happen

        items.put(type, currentUses + 1);
        player.getPersistentDataContainer().set(CONSUMED_KEY, CONSUMED_TYPE, items);
        long points = Research.getResearchPoints(player);
        Research.setResearchPoints(player, points + config.points);

        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.loupe.examined",
                PylonArgument.of("item", name)
        ));
        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.gained_research_points",
                PylonArgument.of("points", config.points),
                PylonArgument.of("total", Research.getResearchPoints(player))
        ));
        return false;
    }

    private static org.bukkit.entity.Item hasValidItem(Block toScan, Map<Material, Integer> items) {
        Collection<org.bukkit.entity.Item> entityItems = toScan.getLocation().getNearbyEntitiesByType(org.bukkit.entity.Item.class, 1.2);
        for (var item : entityItems) {
            ItemStack stack = item.getItemStack();
            ItemRarity rarity = stack.getType().getDefaultData(DataComponentTypes.RARITY);
            int maxUses = itemConfigs.get(rarity).uses;

            // found valid item that hasn't been scanned yet
            if (items.getOrDefault(stack.getType(), 0) < maxUses) {
                return item;
            }
        }

        return null;
    }

    public record ItemConfig(int uses, int points) {
        public static ItemConfig loadFrom(ConfigSection section) {
            return new ItemConfig(
                    section.getOrThrow("uses", ConfigAdapter.INT),
                    section.getOrThrow("points", ConfigAdapter.INT)
            );
        }
    }
}
