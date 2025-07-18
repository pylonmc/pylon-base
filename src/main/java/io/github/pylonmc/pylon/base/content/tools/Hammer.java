package io.github.pylonmc.pylon.base.content.tools;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoCycleItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class Hammer extends PylonItem implements PylonBlockInteractor {


    public final Material baseBlock = getBaseBlock(getKey());
    public final MiningLevel miningLevel = getMiningLevel(getKey());
    public final int cooldownTicks = getSettings().getOrThrow("cooldown-ticks", Integer.class);
    public final Sound sound = Registry.SOUNDS.get(
            NamespacedKey.fromString(
                    getSettings().getOrThrow("sound", String.class)
            )
    );

    public Hammer(@NotNull ItemStack stack) {
        super(stack);
    }

    public boolean tryDoRecipe(@NotNull Block block, @Nullable Player player) {
        if (baseBlock != block.getType()) {
            if (player != null) {
                player.sendMessage(Component.translatable("pylon.pylonbase.message.hammer_cant_use"));
            }
            return false;
        }

        Block blockAbove = block.getRelative(BlockFace.UP);

        List<ItemStack> items = new ArrayList<>();
        for (Entity e : block.getWorld().getNearbyEntities(BoundingBox.of(blockAbove))) {
            if (e instanceof Item entity) {
                items.add(entity.getItemStack());
                entity.remove();
            }
        }

        boolean anyRecipeAttempted = false;
        for (Recipe recipe : Recipe.RECIPE_TYPE) {
            if (!miningLevel.isAtLeast(recipe.level())) continue;

            if (!recipeMatches(items, recipe)) continue;

            if (!new PrePylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, null, player).callEvent()) {
                continue;
            }

            anyRecipeAttempted = true;

            float adjustedChance = recipe.chance() *
                    // Each tier is twice as likely to succeed as the previous one
                    (1 << miningLevel.getNumericalLevel() - recipe.level().getNumericalLevel());
            if (ThreadLocalRandom.current().nextFloat() > adjustedChance) continue;

            for (ItemStack item : items) {
                if (item.isSimilar(recipe.input)) {
                    item.subtract(recipe.input.getAmount());
                    break;
                }
            }

            items.removeIf(item -> item.getAmount() <= 0);
            items.add(recipe.result().clone());
            new PylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe).callEvent();
            break;
        }

        if (anyRecipeAttempted) {
            if (player != null) {
                player.setCooldown(getStack(), cooldownTicks);
                getStack().damage(1, player);
            } else {
                if (!getStack().hasData(DataComponentTypes.UNBREAKABLE)) {
                    int newDamage = getStack().getData(DataComponentTypes.DAMAGE) + 1;
                    if (newDamage >= getStack().getData(DataComponentTypes.MAX_DAMAGE)) {
                        getStack().subtract();
                    } else {
                        getStack().setData(DataComponentTypes.DAMAGE, newDamage);
                    }
                }
            }
            block.getLocation().getWorld().playSound(block.getLocation(), sound, 0.5F, 0.5F);
        }

        for (ItemStack item : items) {
            block.getWorld().dropItem(blockAbove.getLocation().add(0.5, 0.1, 0.5), item)
                    .setVelocity(new Vector(0, 0, 0));
        }

        return anyRecipeAttempted;
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        event.setUseInteractedBlock(Event.Result.DENY);

        Player player = event.getPlayer();
        if (player.hasCooldown(getStack()) || event.getBlockFace() != BlockFace.UP) return;

        Block clickedBlock = event.getClickedBlock();
        Preconditions.checkState(clickedBlock != null);

        tryDoRecipe(clickedBlock, player);
    }

    private static Material getBaseBlock(@NotNull NamespacedKey key) {
        return Map.of(
                BaseKeys.HAMMER_STONE, Material.STONE,
                BaseKeys.HAMMER_IRON, Material.IRON_BLOCK,
                BaseKeys.HAMMER_DIAMOND, Material.DIAMOND_BLOCK
        ).get(key);
    }

    private static MiningLevel getMiningLevel(@NotNull NamespacedKey key) {
        return Map.of(
                BaseKeys.HAMMER_STONE, MiningLevel.STONE,
                BaseKeys.HAMMER_IRON, MiningLevel.IRON,
                BaseKeys.HAMMER_DIAMOND, MiningLevel.DIAMOND
        ).get(key);
    }

    private static @NotNull @Unmodifiable List<ItemStack> hammersWithMiningLevelAtLeast(@NotNull MiningLevel level) {
        return PylonRegistry.ITEMS.getValues().stream()
                .map(PylonItemSchema::getItemStack)
                .filter(item -> fromStack(item) instanceof Hammer hammer
                                && hammer.miningLevel.isAtLeast(level))
                .toList();
    }

    private static boolean containsAtLeast(@NotNull Collection<ItemStack> items, ItemStack item) {
        return items.stream()
                .anyMatch(i -> i.isSimilar(item) && i.getAmount() >= item.getAmount());
    }

    private static boolean recipeMatches(List<ItemStack> items, @NotNull Recipe recipe) {
        return containsAtLeast(items, recipe.input);
    }

    public static @NotNull ItemStack createItemStack(
            NamespacedKey key,
            Material material,
            double attackSpeed,
            double knockback,
            double attackDamage
    ) {
        return ItemStackBuilder.pylonItem(material, key)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                            baseKey("hammer_attack_speed"),
                            attackSpeed,
                            AttributeModifier.Operation.ADD_NUMBER
                    ))
                    .addModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(
                            baseKey("hammer_attack_knockback"),
                            knockback,
                            AttributeModifier.Operation.ADD_NUMBER
                    ))
                    .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                            baseKey("hammer_attack_damage"),
                            attackDamage,
                            AttributeModifier.Operation.ADD_NUMBER
                    )))
            .build();
    }

    public static @NotNull ShapedRecipe getRecipe(NamespacedKey key, ItemStack stack, Material toolMaterial) {
        ShapedRecipe recipe = new ShapedRecipe(key, stack)
                .shape(
                        " I ",
                        " SI",
                        "S  "
                )
                .setIngredient('I', new RecipeChoice.ExactChoice(new ItemStack(toolMaterial)))
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        return recipe;
    }

    public record Recipe(
            NamespacedKey key,
            ItemStack input,
            ItemStack result,
            MiningLevel level,
            float chance
    ) implements PylonRecipe {

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "hammer")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            return List.of(new RecipeChoice.ExactChoice(input));
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            return List.of(result);
        }

        @Override
        public @NotNull Gui display() {
            return Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# # # # # # # # #",
                            "# # # i h o # # #",
                            "# # # # # # # # #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient('i', ItemButton.fromStack(input))
                    .addIngredient('h', new AutoCycleItem(20,
                            hammersWithMiningLevelAtLeast(level)
                                    .stream()
                                    .map(ItemStackBuilder::of)
                                    .toList()
                                    .toArray(new ItemStackBuilder[]{})
                    ))
                    .addIngredient('o', ItemButton.fromStack(result))
                    .build();
        }
    }
}
