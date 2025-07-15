package io.github.pylonmc.pylon.base.content.machines.simple;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;
import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;


public class Press extends PylonBlock implements PylonInteractableBlock, PylonFluidBlock, PylonEntityHolderBlock {

    public static final NamespacedKey OIL_AMOUNT_KEY = baseKey("oil_amount");

    private static final Config settings = Settings.get(BaseKeys.PRESS);
    public static final int TIME_PER_ITEM_TICKS = settings.getOrThrow("time-per-item-ticks", Integer.class);
    public static final int RETURN_TO_START_TIME_TICKS = settings.getOrThrow("return-to-start-time-ticks", Integer.class);
    public static final int CAPACITY_MB = settings.getOrThrow("capacity-mb", Integer.class);

    public static class PressItem extends PylonItem {

        public PressItem(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of(
                    "time_per_item", UnitFormat.SECONDS.format(TIME_PER_ITEM_TICKS / 20.0)
            ));
        }
    }

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
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "press_cover", new SimpleItemDisplay(new ItemDisplayBuilder()
                        .material(Material.SPRUCE_PLANKS)
                        .transformation(getCoverTransform(0.4))
                        .build(getBlock().getLocation().toCenterLocation())
                ),
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.NORTH)
        );
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(double deltaSeconds) {
        return Map.of(BaseFluids.PLANT_OIL, oilAmount);
    }

    @Override
    public void removeFluid(@NotNull PylonFluid fluid, double amount) {
        oilAmount -= amount;
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(
                getName(),
                List.of(
                        PylonArgument.of("plant_oil_amount", UnitFormat.MILLIBUCKETS.format(Math.round(oilAmount))),
                        PylonArgument.of("plant_oil_capacity", UnitFormat.MILLIBUCKETS.format(CAPACITY_MB)),
                        PylonArgument.of("plant_oil", BaseFluids.PLANT_OIL.getName())
                )
        );
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

        tryStartRecipe(event.getPlayer());
    }

    public boolean tryStartRecipe(@Nullable Player player) {
        if (currentRecipe != null) {
            return false;
        }

        List<Item> items = getBlock()
                .getLocation()
                .toCenterLocation()
                .getNearbyEntities(0.5, 0.8, 0.5)
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
                    if (recipe.oilAmount > availableSpace || !new PrePylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this, player).callEvent()) {
                        continue;
                    }

                    stack.subtract();
                    startRecipe(recipe);
                    return true;
                }
            }
        }

        return false;
    }

    public void startRecipe(Recipe recipe) {
        this.currentRecipe = recipe;
        getCover().setTransform(TIME_PER_ITEM_TICKS - RETURN_TO_START_TIME_TICKS, getCoverTransform(0.0));

        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
            getCover().setTransform(RETURN_TO_START_TIME_TICKS, getCoverTransform(0.0));

            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                this.oilAmount += recipe.oilAmount;
                new PylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this).callEvent();
                this.currentRecipe = null;
            }, RETURN_TO_START_TIME_TICKS);
        }, TIME_PER_ITEM_TICKS - RETURN_TO_START_TIME_TICKS);
    }

    public @NotNull SimpleItemDisplay getCover() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "press_cover");
    }

    public static @NotNull Matrix4f getCoverTransform(double translation) {
        return new TransformBuilder()
                .translate(0, translation, 0)
                .scale(0.9, 0.1, 0.9)
                .buildForItemDisplay();
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
            ConfigSection config = Settings.get(BaseKeys.PRESS).getSectionOrThrow("oil-amount");
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
            return List.of(BaseFluids.PLANT_OIL);
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
                    .addIngredient('p', ItemButton.fromStack(BaseItems.PRESS))
                    .addIngredient('i', ItemButton.fromStack(input))
                    .addIngredient('c', GuiItems.progressCyclingItem(TIME_PER_ITEM_TICKS,
                            ItemStackBuilder.of(Material.CLOCK)
                                    .name(net.kyori.adventure.text.Component.translatable(
                                            "pylon.pylonbase.guide.recipe.press",
                                            PylonArgument.of("time", UnitFormat.SECONDS.format(TIME_PER_ITEM_TICKS / 20.0))
                                    ))
                    ))
                    .addIngredient('o', new FluidButton(BaseFluids.PLANT_OIL.getKey(), oilAmount))
                    .build();
        }
    }
}
