package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class HealthTalisman extends PylonItemSchema {
    public final int healthAmount = getSettings().getOrThrow("max-health-boost", Integer.class);
    private static final NamespacedKey healthBoostedKey = new NamespacedKey(PylonBase.getInstance(), "talisman_health_boosted");
    public final AttributeModifier healthModifier = new AttributeModifier(
            healthBoostedKey,
            healthAmount,
            AttributeModifier.Operation.ADD_NUMBER
    );

    public HealthTalisman(NamespacedKey id,
                          Class<? extends PylonItem<? extends HealthTalisman>> itemClass,
                          Function<NamespacedKey, ItemStack> template,
                          Function<ItemStack, ShapedRecipe> recipeFunc) {
        super(id, itemClass, template);
        if (this.template.getMaxStackSize() != 1) {
            throw new IllegalArgumentException("Max stack size for health talisman must be equal to 1");
        }
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipeFunc.apply(this.template));
    }

    public static class Item extends PylonItem<HealthTalisman> {

        public Item(HealthTalisman schema, ItemStack itemStack) {
            super(schema, itemStack);
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
            return Map.of("health-boost", Component.text(getSchema().healthAmount));
        }
    }

    public static class HealthTalismanTicker extends BukkitRunnable {

        @Override
        // Suppresses warnings from doing PDC.has() and then .get() and assuming .get is not null, and assuming that the player has the max health attribute
        @SuppressWarnings("DataFlowIssue")
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean foundItem = false;
                PersistentDataContainer playerPDC = player.getPersistentDataContainer();
                AttributeInstance playerHealth = player.getAttribute(Attribute.MAX_HEALTH);
                Integer playerHealthBoost = playerPDC.get(healthBoostedKey, PersistentDataType.INTEGER);
                for (ItemStack itemStack : player.getInventory()) {
                    PylonItem<?> pylonItem = PylonItem.fromStack(itemStack);
                    if (!(pylonItem instanceof HealthTalisman.Item talisman)) {
                        continue;
                    }
                    if (playerHealthBoost == null) {
                        playerHealth.addModifier(talisman.getSchema().healthModifier);
                        playerPDC.set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getSchema().healthAmount);
                        foundItem = true;
                    } else if (playerHealthBoost < talisman.getSchema().healthAmount) {
                        playerHealth.removeModifier(healthBoostedKey);
                        playerHealth.addModifier(talisman.getSchema().healthModifier);
                        playerPDC.set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getSchema().healthAmount);
                        foundItem = true;
                    } else if (talisman.getSchema().healthAmount == playerHealthBoost) {
                        foundItem = true;
                    }
                }
                if (!foundItem && playerHealthBoost != null) {
                    playerHealth.removeModifier(healthBoostedKey);
                    playerPDC.remove(healthBoostedKey);
                }
            }
        }
    }
}
