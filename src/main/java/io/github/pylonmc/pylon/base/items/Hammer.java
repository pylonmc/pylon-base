package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.MiningLevel;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class Hammer extends PylonItemSchema {

    private final MiningLevel miningLevel;

    public Hammer(
            NamespacedKey id,
            Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
            ItemStack template,
            MiningLevel miningLevel,
            RecipeChoice baseItem
    ) {
        super(id, itemClass, template);
        this.miningLevel = miningLevel;
        ShapedRecipe recipe = new ShapedRecipe(id, template);
        recipe.shape(
                "III",
                "IS ",
                " S "
        );
        recipe.setIngredient('I', baseItem);
        recipe.setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(RecipeUtils.reflect(recipe));
    }

    public static class Item extends PylonItem<Hammer> implements BlockInteractor {
        public Item(Hammer schema, ItemStack itemStack) {
            super(schema, itemStack);
        }

        @Override
        public void onUsedToRightClickBlock(PlayerInteractEvent event) {
            event.setUseInteractedBlock(Event.Result.DENY);

            Player p = event.getPlayer();
            ItemStack hammer = event.getItem();
            if (p.hasCooldown(hammer)) return;

            Block clickedBlock = event.getClickedBlock();
            World world = clickedBlock.getWorld();

            hammer.damage(1, p);

            if (event.getBlockFace() != BlockFace.UP) return;

            MiningLevel thisLevel = getSchema().miningLevel;
            p.setCooldown(hammer, (5 - thisLevel.getNumericalLevel()) * 20);
            if (thisLevel.canMine(clickedBlock.getType())) {
                p.sendMessage(Component.text("This block is too soft to use a hammer on").color(NamedTextColor.RED));
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

            recipeLoop:
            for (Recipe recipe : RECIPE_TYPE) {
                if (thisLevel.isAtLeast(recipe.level())) {
                    for (ItemStack ingredient : recipe.ingredients()) {
                        if (!containsAtLeast(items, ingredient)) {
                            continue recipeLoop;
                        }
                    }

                    float adjustedChance = recipe.chance() *
                            // Each tier is twice as likely to succeed as the previous one
                            (1 << thisLevel.getNumericalLevel() - recipe.level().getNumericalLevel());
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
            }

            for (ItemStack item : items) {
               world.dropItem(blockAbove.getLocation().add(0.5, 0.1, 0.5), item)
                       .setVelocity(new Vector(0, 0, 0));
            }
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
        public NamespacedKey getKey() {
            return key;
        }
    }

    public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "hammer")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }

    private static boolean containsAtLeast(Collection<ItemStack> items, ItemStack item) {
        for (ItemStack i : items) {
            if (i.isSimilar(item) && i.getAmount() >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }
}
