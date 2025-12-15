package io.github.pylonmc.pylon.base.recipes.intermediate;

import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.config.adapter.MapConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @param damageConsume
 *   If true and item has durability -> damage
 *   If true and item has no durability -> consume item
 *   If false -> do nothing
 */
public record Step(NamespacedKey tool, int uses, boolean damageConsume, List<String> removeDisplays,
                   List<DisplayData> addDisplays) {
    public static final ConfigAdapter<Step> ADAPTER = new ConfigAdapter<>() {
        @Override
        public @NotNull Type getType() {
            return Step.class;
        }

        @Override
        public Step convert(@NotNull Object value) {
            var map = MapConfigAdapter.STRING_TO_ANY.convert(value);
            return new Step(
                ConfigAdapter.NAMESPACED_KEY.convert(map.get("tool")),
                ConfigAdapter.INT.convert(map.get("uses") != null ? map.get("uses") : 1),
                ConfigAdapter.BOOLEAN.convert(map.get("damage_consume") == Boolean.TRUE),
                ConfigAdapter.LIST.from(ConfigAdapter.STRING).convert(map.get("remove_displays") != null ? map.get("remove_displays") : Map.of()),
                ConfigAdapter.LIST.from(DisplayData.ADAPTER).convert(map.get("add_displays") != null ? map.get("add_displays") : Map.of())
            );
        }
    };

    public ItemStack asStack() {
        return asStack(uses);
    }

    public ItemStack asStack(int times) {
        ItemStack output;

        TranslatableComponent lore = Component.translatable("pylon.pylonbase.guide.recipe.blueprint-workbench.used-times-lore")
            .arguments(
                PylonArgument.of(
                    "times",
                    times
                )
            );
        if (tool.getNamespace().equals("minecraft")) {
            output = ItemStackBuilder.of(Registry.MATERIAL.get(tool))
                .lore(lore)
                .build();
        } else {
            PylonItemSchema pis = PylonRegistry.ITEMS.get(tool);
            output = ItemStackBuilder.of(pis.getItemStack())
                .lore(lore)
                .build();
        }

        return output;
    }

    /**
     * Points to a specific step in a step array and the relative used amount
     */
    @Data
    @AllArgsConstructor
    public static class ActionStep {
        private int step;
        private int usedAmount;

        public long toLong() {
            return ((long) step << 32) | (usedAmount & 0xFFFFFFFFL);
        }

        public ActionStep(long packed) {
            this.step = (int) (packed >> 32);
            this.usedAmount = (int) packed;
        }
    }
}
