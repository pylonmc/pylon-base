package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
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
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Hammer extends PylonItem<Hammer.Schema> implements BlockInteractor {

    @SuppressWarnings("UnstableApiUsage")
    @NotNullByDefault
    public static class Schema extends PylonItemSchema {

        private final MiningLevel miningLevel;
        private final Material requiredBlock;
        private static final int COOLDOWN_TICKS = 2 * 20;

        public Schema(
                NamespacedKey id,
                Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                ItemStack template,
                MiningLevel miningLevel,
                Material requiredBlock,
                RecipeChoice baseItem
        ) {
            super(id, itemClass, template);
            this.miningLevel = miningLevel;
            this.requiredBlock = requiredBlock;
            ShapedRecipe recipe = new ShapedRecipe(id, template);
            recipe.shape(
                    " I ",
                    " SI",
                    "S  "
            );
            recipe.setIngredient('I', baseItem);
            recipe.setIngredient('S', Material.STICK);
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        }
    }

    public record Recipe(
            NamespacedKey key,
            List<ItemStack> ingredients,
            ItemStack result,
            MiningLevel level,
            float chance
    ) implements Keyed {
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
    }

    public Hammer(Schema schema, ItemStack itemStack) {
        super(schema, itemStack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        event.setUseInteractedBlock(Event.Result.DENY);

        Player player = event.getPlayer();
        ItemStack hammer = event.getItem();
        if (player.hasCooldown(hammer)) return;

        Block clickedBlock = event.getClickedBlock();
        World world = clickedBlock.getWorld();

        if (event.getBlockFace() != BlockFace.UP) return;

        if (getSchema().requiredBlock != clickedBlock.getType()) {
            player.sendMessage(Component.text("You cannot use this hammer on this block!").color(NamedTextColor.RED));
            return;
        }

        Block blockAbove = clickedBlock.getRelative(BlockFace.UP);

        List<ItemStack> items = new ArrayList<>();
        for (Entity e : world.getNearbyEntities(BoundingBox.of(blockAbove))) {
            if (e instanceof org.bukkit.entity.Item entity) {
                items.add(entity.getItemStack());
                entity.remove();
            }
        }

        boolean anyRecipeAttempted = false;
        for (Recipe recipe : Recipe.RECIPE_TYPE) {
            if (!getSchema().miningLevel.isAtLeast(recipe.level())) continue;

            if (!recipeMatches(items, recipe)) continue;

            anyRecipeAttempted = true;

            float adjustedChance = recipe.chance() *
                    // Each tier is twice as likely to succeed as the previous one
                    (1 << getSchema().miningLevel.getNumericalLevel() - recipe.level().getNumericalLevel());
            if (ThreadLocalRandom.current().nextFloat() > adjustedChance) continue;

            for (ItemStack ingredient : recipe.ingredients()) {
                for (ItemStack item : items) {
                    if (item.isSimilar(ingredient)) {
                        item.subtract(ingredient.getAmount());
                        break;
                    }
                }
            }
            items.removeIf(item -> item.getAmount() <= 0);
            items.add(recipe.result().clone());
            break;
        }

        if (anyRecipeAttempted) {
            player.setCooldown(hammer, Schema.COOLDOWN_TICKS);
            hammer.damage(1, player);
            clickedBlock.getLocation().getWorld().playSound(clickedBlock.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5F, 0.5F);
        }

        for (ItemStack item : items) {
            world.dropItem(blockAbove.getLocation().add(0.5, 0.1, 0.5), item)
                    .setVelocity(new Vector(0, 0, 0));
        }
    }

    private static boolean containsAtLeast(@NotNull Collection<ItemStack> items, ItemStack item) {
        return items.stream()
                .anyMatch(i -> i.isSimilar(item) && i.getAmount() >= item.getAmount());
    }

    private static boolean recipeMatches(List<ItemStack> items, @NotNull Recipe recipe) {
        return recipe.ingredients()
                .stream()
                .allMatch(ingredient -> containsAtLeast(items, ingredient));
    }
}
