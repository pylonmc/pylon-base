package io.github.pylonmc.pylon.base.recipes.intermediate;

import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.config.adapter.MapConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.TextDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param damageConsume
 *   If true and item has durability -> damage
 *   If true and item has no durability -> consume item
 *   If false -> do nothing
 */
public record Step(
    NamespacedKey tool,
    int uses,
    boolean damageConsume,
    List<String> removeDisplays,
    List<DisplayData> addDisplays
) {
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

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StateDisplay {
        private final PylonEntityHolderBlock entityHolder;
        public static final Set<String> ENTITY_IDENTIFIERS = Set.of(
            "display$amount_left",
            "display$item_to_use",
            "display$item_name"
        );

        public static StateDisplay init(PylonEntityHolderBlock entityHolder) {
            StateDisplay display = new StateDisplay(entityHolder);


            Location centerLocation = entityHolder.getBlock().getRelative(BlockFace.UP).getLocation().toCenterLocation().add(0, 1, 0);

            entityHolder.addEntity("display$amount_left", new TextDisplayBuilder()
                    .transformation(new TransformBuilder()
                        .translate(0, 0, 0)
                    ).billboard(Display.Billboard.VERTICAL).build(centerLocation));
            entityHolder.addEntity("display$item_to_use", new ItemDisplayBuilder()
                    .transformation(new TransformBuilder()
                        .translate(0, -0.3, 0)
                        .scale(0.25)
                        .rotate(0, Math.PI, 0) // no idea why but it spawns flipped
                    ).billboard(Display.Billboard.VERTICAL).build(centerLocation));
            entityHolder.addEntity("display$item_name", new TextDisplayBuilder()
                    .transformation(new TransformBuilder()
                        .translate(0, -0.8, 0)
                    ).billboard(Display.Billboard.VERTICAL).build(centerLocation));

            display.setVisibility(false);

            return display;
        }

        public static StateDisplay load(PylonEntityHolderBlock entityHolder) {
            return new StateDisplay(entityHolder);
        }

        public TextDisplay amountLeft() {
            return entityHolder.getHeldEntityOrThrow(TextDisplay.class, "display$amount_left");
        }

        public ItemDisplay itemToUse() {
            return entityHolder.getHeldEntityOrThrow(ItemDisplay.class, "display$item_to_use");
        }

        public TextDisplay itemName() {
            return entityHolder.getHeldEntityOrThrow(TextDisplay.class, "display$item_name");
        }

        public void setVisibility(boolean visibility) {
            this.amountLeft().setVisibleByDefault(visibility);
            this.itemToUse().setVisibleByDefault(visibility);
            this.itemName().setVisibleByDefault(visibility);
        }

        public void update(Step step, ActionStep current) {
            if (step == null || current == null) {
                setVisibility(false);
                return;
            }

            setVisibility(true);

            ItemStack stack = step.asStack();
            this.amountLeft().text(step.getUseTimes(step.uses - current.getUsedAmount()));
            this.itemToUse().setItemStack(stack);
            this.itemName().text(stack.getData(DataComponentTypes.ITEM_NAME));
        }
    }

    public ItemStack asStack() {
        return asStack(uses);
    }

    public ItemStack asStack(int times) {
        ItemStack output;

        TranslatableComponent lore = getUseTimes(times);
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

    public TranslatableComponent getUseTimes(int times) {
        // todo: maybe generalize key to procedural-crafting as this will be reused for assembly table
        return Component.translatable("pylon.pylonbase.guide.recipe.blueprint-workbench.used-times-lore")
            .arguments(
                PylonArgument.of(
                    "times",
                    times
                )
            );
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

    public interface StepsHolder {

        @Getter
        @AllArgsConstructor
        enum Result {
            INVALID_TOOL(false),
            NEXT_STEP(true),
            NEXT_PROGRESS(true),
            COMPLETED_RECIPE(true);

            private final boolean success;
        }

        List<Step> steps();

        default @NotNull Result progressRecipe(@NotNull ActionStep currentProgress, @NotNull Step currentStep, @NotNull ItemStack item, @NotNull Player player, boolean shouldDamage) {
            PylonItem pylonItem = PylonItem.fromStack(item);
            NamespacedKey key = pylonItem != null ? pylonItem.getKey() : item.getType().getKey();

            if (!currentStep.tool().equals(key)) {
                return Result.INVALID_TOOL;
            }

            if (shouldDamage && currentStep.damageConsume()) {
                PylonUtils.damageItem(item, 1, player.getWorld());
            }

            int newUsedAmount = currentProgress.getUsedAmount() + 1;
            int remainingAmount = currentStep.uses() - newUsedAmount;
            if (remainingAmount == 0) {
                int newStep = currentProgress.getStep() + 1;
                if (newStep < this.steps().size()) {
                    currentProgress.setStep(newStep);
                    return Result.NEXT_STEP;
                } else {
                    return Result.COMPLETED_RECIPE;
                }
            } else {
                currentProgress.setUsedAmount(newUsedAmount);
                return Result.NEXT_PROGRESS;
            }
        }
    }
}
