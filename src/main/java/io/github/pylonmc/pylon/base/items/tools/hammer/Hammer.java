package io.github.pylonmc.pylon.base.items.tools.hammer;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public abstract class Hammer extends PylonItem implements BlockInteractor {

    protected abstract Material getToolMaterial();
    protected abstract Material getBaseBlock();
    protected abstract MiningLevel getMiningLevel();
    protected abstract int getCooldown();
    protected abstract Sound getSound();

    public Hammer(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
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

        if (getBaseBlock() != clickedBlock.getType()) {
            player.sendMessage(Component.translatable("pylon.pylonbase.message.hammer_cant_use"));
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
            if (!getMiningLevel().isAtLeast(recipe.level())) continue;

            if (!recipeMatches(items, recipe)) continue;

            anyRecipeAttempted = true;

            float adjustedChance = recipe.chance() *
                    // Each tier is twice as likely to succeed as the previous one
                    (1 << getMiningLevel().getNumericalLevel() - recipe.level().getNumericalLevel());
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
            player.setCooldown(hammer, getCooldown());
            hammer.damage(1, player);
            clickedBlock.getLocation().getWorld().playSound(clickedBlock.getLocation(), getSound(), 0.5F, 0.5F);
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

    protected static @NotNull ItemStack createItemStack(
            NamespacedKey key,
            Material material,
            double attackSpeed,
            double knockback,
            double attackDamage
    ) {
        return ItemStackBuilder.defaultBuilder(material, key)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                            pylonKey("hammer_attack_speed"),
                            attackSpeed,
                            AttributeModifier.Operation.ADD_NUMBER
                    ))
                    .addModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(
                            pylonKey("hammer_attack_knockback"),
                            knockback,
                            AttributeModifier.Operation.ADD_NUMBER
                    ))
                    .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                            pylonKey("hammer_attack_damage"),
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
}
