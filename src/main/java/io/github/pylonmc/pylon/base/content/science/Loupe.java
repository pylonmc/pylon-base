package io.github.pylonmc.pylon.base.content.science;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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
        if (offhand.isEmpty()) {
            player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.nothing"));
            event.setCancelled(true);
            return;
        }
        if (PylonItem.fromStack(offhand) != null) {
            player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.is_pylon"));
            event.setCancelled(true);
            return;
        }
        ItemRarity rarity = offhand.getType().getDefaultData(DataComponentTypes.RARITY);
        int maxUses = itemConfigs.get(rarity).uses;

        var items = player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of());
        if (items.getOrDefault(offhand.getType(), 0) >= maxUses) {
            player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.already_examined"));
            event.setCancelled(true);
        }
    }

    @Override
    public void onConsumed(@NotNull PlayerItemConsumeEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand.isEmpty()) return; // This should be handled by the above function but just in case

        ItemConfig config = itemConfigs.get(offhand.getType().getDefaultData(DataComponentTypes.RARITY));
        var items = new HashMap<>(player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of()));
        int currentUses = items.getOrDefault(offhand.getType(), 0);
        if (currentUses >= config.uses) return; // This should never happen

        items.put(offhand.getType(), currentUses + 1);
        player.getPersistentDataContainer().set(CONSUMED_KEY, CONSUMED_TYPE, items);
        long points = Research.getResearchPoints(player);
        Research.setResearchPoints(player, points + config.points);

        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.loupe.examined",
                PylonArgument.of("item", offhand.effectiveName())
        ));
        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.gained_research_points",
                PylonArgument.of("points", config.points)
        ));
        offhand.subtract();
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
