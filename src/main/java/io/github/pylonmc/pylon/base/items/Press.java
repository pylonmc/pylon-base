package io.github.pylonmc.pylon.base.items;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;
import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;


public class Press extends PylonBlock implements PylonInteractableBlock, PylonFluidIoBlock {

    public static final NamespacedKey KEY = pylonKey("press");
    public static final NamespacedKey OIL_AMOUNT_KEY = pylonKey("oil_amount");

    public static final int TIME_PER_ITEM_TICKS = Settings.get(KEY).getOrThrow("time-per-item-ticks", Integer.class);
    public static final int RETURN_TO_START_TIME_TICKS = Settings.get(KEY).getOrThrow("return-to-start-time-ticks", Integer.class);
    public static final int CAPACITY_MB = Settings.get(KEY).getOrThrow("capacity-mb", Integer.class);

    // Not worth the effort to persist across unloads
    @Getter private @Nullable Recipe currentRecipe;
    @Getter private double oilAmount;

    @SuppressWarnings("unused")
    public Press(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        oilAmount = 0.0;
    }

    @SuppressWarnings("unused")
    public Press(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        oilAmount = pdc.get(OIL_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(OIL_AMOUNT_KEY, PylonSerializers.DOUBLE, oilAmount);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.NORTH)
        );
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        Map<String, PylonEntity<?>> entities = PylonFluidIoBlock.super.createEntities(context);
        entities.put("press_cover", new PressCoverEntity(getBlock()));
        return entities;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.PLANT_OIL, oilAmount);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        oilAmount -= amount;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick() || event.getHand() != EquipmentSlot.HAND || event.getPlayer().isSneaking()) {
            return;
        }

        event.setCancelled(true);

        if (currentRecipe != null) {
            return;
        }

        List<Item> items = getBlock()
                .getLocation()
                .toCenterLocation()
                .getNearbyEntities(0.5, 0.5, 0.5)
                .stream()
                .filter(Item.class::isInstance)
                .map(Item.class::cast)
                .toList();

        List<ItemStack> stacks = items.stream()
                .map(Item::getItemStack)
                .toList();

        for (Recipe recipe : Recipe.RECIPE_TYPE.getRecipes()) {
            for (ItemStack stack : stacks) {
                if (isPylonSimilar(recipe.input, stack)) {
                    double availableSpace = CAPACITY_MB - oilAmount;
                    if (recipe.oilAmount > availableSpace || !new PrePylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this, event.getPlayer()).callEvent()) {
                        continue;
                    }

                    stack.subtract();
                    startRecipe(recipe);
                    break;
                }
            }
        }
    }

    public void startRecipe(Recipe recipe) {
        this.currentRecipe = recipe;
        getCover().goDown();
        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
            getCover().goUp();
            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                this.oilAmount += recipe.oilAmount;
                new PylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this).callEvent();
                this.currentRecipe = null;
            }, RETURN_TO_START_TIME_TICKS);
        }, TIME_PER_ITEM_TICKS - RETURN_TO_START_TIME_TICKS);
    }

    public @NotNull PressCoverEntity getCover() {
        return Objects.requireNonNull(getHeldEntity(PressCoverEntity.class, "press_cover"));
    }

    public static class PressCoverEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("press_cover");

        @SuppressWarnings("unused")
        public PressCoverEntity(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public PressCoverEntity(@NotNull  Block block) {
            super(
                    KEY,
                    new ItemDisplayBuilder()
                            .material(Material.SPRUCE_PLANKS)
                            .transformation(new TransformBuilder()
                                    .translate(0, 0.4, 0)
                                    .scale(0.9, 0.1, 0.9))
                            .build(block.getLocation().toCenterLocation())
            );
        }

        public void goDown() {
            getEntity().setTransformationMatrix(new TransformBuilder()
                    .scale(0.9, 0.1, 0.9)
                    .buildForItemDisplay());
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(TIME_PER_ITEM_TICKS - RETURN_TO_START_TIME_TICKS);
        }

        public void goUp() {
            getEntity().setTransformationMatrix(new TransformBuilder()
                    .translate(0, 0.4, 0)
                    .scale(0.9, 0.1, 0.9)
                    .buildForItemDisplay());
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(RETURN_TO_START_TIME_TICKS);
        }
    }

    public record Recipe(
            NamespacedKey key,
            ItemStack input,
            double oilAmount
    ) implements PylonRecipe {

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "press")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
            ConfigSection config = Settings.get(KEY).getSectionOrThrow("oil-amount");
            for (String key : config.getKeys()) {
                Material material = Material.getMaterial(key.toUpperCase());
                Preconditions.checkState(material != null, "No such material " + key);
                int amount = config.get(key, Integer.class);
                RECIPE_TYPE.addRecipe(new Press.Recipe(material.getKey(), new ItemStack(material), amount));
            }
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            return List.of(new RecipeChoice.ExactChoice(input));
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            return List.of();
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
            return List.of(PylonFluids.PLANT_OIL);
        }

        @Override
        public @NotNull Gui display() {
            return Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# # # # # # # # #",
                            "# p # # i c o # #",
                            "# # # # # # # # #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient('p', ItemButton.fromStack(PylonItems.PRESS))
                    .addIngredient('i', ItemButton.fromStack(input))
                    .addIngredient('c', GuiItems.progressCyclingItem(TIME_PER_ITEM_TICKS,
                            ItemStackBuilder.of(Material.CLOCK)
                                    .name(net.kyori.adventure.text.Component.translatable(
                                            "pylon.pylonbase.guide.recipe.press",
                                            PylonArgument.of("time", UnitFormat.SECONDS.format(TIME_PER_ITEM_TICKS / 20.0))
                                    ))
                    ))
                    .addIngredient('o', new FluidButton(PylonFluids.PLANT_OIL.getKey(), oilAmount))
                    .build();
        }
    }
}
