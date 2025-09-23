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
import io.papermc.paper.registry.keys.SoundEventKeys;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@SuppressWarnings("UnstableApiUsage")
public class Loupe extends PylonItem implements PylonInteractor, PylonConsumable {

    public static final NamespacedKey CONSUMED_KEY = new NamespacedKey(PylonBase.getInstance(), "consumed");
    public static final PersistentDataType<PersistentDataContainer, Map<Material, Integer>> CONSUMED_TYPE =
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

    private static final Map<UUID, RayTraceResult> scanning = new HashMap<>();

    public Loupe(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasCooldown(getStack())) {
            return;
        }

        RayTraceResult scan = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5, FluidCollisionMode.SOURCE_ONLY, false, 0, hit -> hit != player);
        if (scan == null) {
            return;
        }

        RayTraceResult initialScan = scanning.get(player.getUniqueId());
        if (initialScan != null && Objects.equals(scan.getHitBlock(), initialScan.getHitBlock()) && Objects.equals(scan.getHitEntity(), initialScan.getHitEntity())) {
            return;
        }

        if (scan.getHitEntity() instanceof Item hit) {
            ItemStack stack = hit.getItemStack();
            if (PylonItem.fromStack(stack) != null) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.is_pylon"));
            } else if (!stack.getPersistentDataContainer().isEmpty()) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.is_other_plugin"));
            } else if (!canExamine(stack.getType(), player)) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.already_examined"));
            } else {
                player.playSound(Sound.sound(SoundEventKeys.BLOCK_BELL_RESONATE, Sound.Source.PLAYER, 1f, 0.7f));
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.examining")
                        .arguments(PylonArgument.of("item", stack.effectiveName())));
                scanning.put(player.getUniqueId(), scan);
            }
        } else if (scan.getHitEntity() instanceof LivingEntity entity) {
            // todo: scanning entities, allow scanning players?
        } else if (scan.getHitBlock() != null) {
            Block hit = scan.getHitBlock();
            Material type = hit.getType();
            if (BlockStorage.get(hit) != null) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.is_pylon"));
            } else if (type.getHardness() < 0f) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.is_unbreakable"));
            } else if (!canExamine(type, player)) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.already_examined"));
            } else {
                player.playSound(Sound.sound(SoundEventKeys.BLOCK_BELL_RESONATE, Sound.Source.PLAYER, 1f, 0.7f));
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.loupe.examining")
                        .arguments(PylonArgument.of("item", Component.translatable(type))));
                scanning.put(player.getUniqueId(), scan);
            }
        }
    }

    @Override
    public void onConsumed(@NotNull PlayerItemConsumeEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        RayTraceResult initialScan = scanning.remove(player.getUniqueId());
        if (initialScan == null) {
            return;
        }

        RayTraceResult scan = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5, FluidCollisionMode.SOURCE_ONLY, false, 0, hit -> hit != player);
        if (scan == null || !Objects.equals(scan.getHitBlock(), initialScan.getHitBlock()) || !Objects.equals(scan.getHitEntity(), initialScan.getHitEntity())) {
            return;
        }

        if (scan.getHitEntity() instanceof Item hit) {
            ItemStack stack = hit.getItemStack();
            if (PylonItem.fromStack(stack) != null || !stack.getPersistentDataContainer().isEmpty() || !canExamine(stack.getType(), player)) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.examine_failed")
                        .arguments(PylonArgument.of("item", stack.effectiveName())));
                return;
            }

            PlayerAttemptPickupItemEvent pickupEvent = new PlayerAttemptPickupItemEvent(player, hit, stack.getAmount() - 1);
            if (!pickupEvent.callEvent()) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.examine_failed")
                        .arguments(PylonArgument.of("item", stack.effectiveName())));
                return;
            }

            if (stack.getAmount() == 1) {
                hit.remove();
            } else {
                stack.subtract();
                hit.setItemStack(stack);
            }
            addPoints(stack.getType(), stack.effectiveName(), player);
            player.setCooldown(getStack(), 60);
        } else if (scan.getHitEntity() instanceof LivingEntity entity) {
            // todo: scanning entities, allow scanning players?
        } else if (scan.getHitBlock() != null) {
            Block hit = scan.getHitBlock();
            Material type = hit.getType();
            if (type.getHardness() < 0f || BlockStorage.get(hit) != null || !canExamine(type, player)) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.examine_failed")
                        .arguments(PylonArgument.of("item", Component.translatable(type))));
                return;
            }

            BlockBreakEvent breakEvent = new BlockBreakEvent(hit, player);
            breakEvent.setDropItems(false);
            breakEvent.setExpToDrop(0);
            if (!breakEvent.callEvent()) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.loupe.examine_failed")
                        .arguments(PylonArgument.of("item", Component.translatable(type))));
                return;
            }

            hit.getWorld().playEffect(hit.getLocation(), Effect.STEP_SOUND, hit.getBlockData());
            hit.setType(Material.AIR, true);
            addPoints(type, Component.translatable(type), player);
            player.setCooldown(getStack(), 60);
        }
    }

    private static void addPoints(Material type, Component name, Player player) {
        ItemConfig config = itemConfigs.get(type.getDefaultData(DataComponentTypes.RARITY));
        var items = new HashMap<>(player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of()));

        items.put(type, items.getOrDefault(type, 0) + 1);
        player.getPersistentDataContainer().set(CONSUMED_KEY, CONSUMED_TYPE, items);

        long points = Research.getResearchPoints(player) + config.points;
        Research.setResearchPoints(player, points);

        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.loupe.examined",
                PylonArgument.of("item", name)
        ));
        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.gained_research_points",
                PylonArgument.of("points", config.points),
                PylonArgument.of("total", points)
        ));
    }

    private static boolean canExamine(Material type, Player player) {
        var items = player.getPersistentDataContainer().getOrDefault(CONSUMED_KEY, CONSUMED_TYPE, Map.of());
        ItemRarity rarity = type.getDefaultData(DataComponentTypes.RARITY);
        int maxUses = itemConfigs.get(rarity).uses;
        return items.getOrDefault(type, 0) < maxUses;
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
