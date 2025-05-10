package io.github.pylonmc.pylon.base.misc;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;


// TODO probably remove this and change its user(s) to just use the mixing pot
/**
 * Input quantity is assumed to be 1.
 */
public record WaterCauldronRightClickRecipe(
        @NotNull NamespacedKey key,
        @NotNull RecipeChoice input,
        @NotNull ItemStack output
) implements Keyed {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<WaterCauldronRightClickRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "cauldron_right_click")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }

    public static class CauldronListener implements Listener {

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public static void handle(@NotNull PlayerInteractEvent event) {
            if (event.getHand() != EquipmentSlot.HAND
                    || event.getAction() != Action.RIGHT_CLICK_BLOCK
                    || event.getClickedBlock() == null
                    || event.getClickedBlock().getType() != Material.WATER_CAULDRON
                    || !(event.getClickedBlock().getBlockData() instanceof Levelled levelled)
                    || BlockStorage.get(event.getClickedBlock()) != null
                    || event.getItem() == null
            ) {
                return;
            }

            if (levelled.getLevel() < levelled.getMinimumLevel()) {
                return;
            }

            for (WaterCauldronRightClickRecipe recipe : RECIPE_TYPE.getRecipes()) {
                if (!recipe.input.test(event.getItem())) {
                    continue;
                }

                if (levelled.getLevel() == levelled.getMinimumLevel()) {
                    event.getClickedBlock().setType(Material.CAULDRON); // empty the cauldron
                } else {
                    levelled.setLevel(levelled.getLevel() - 1);
                    event.getClickedBlock().setBlockData(levelled);
                }

                new ParticleBuilder(Particle.SPLASH)
                        .count(30)
                        .location(event.getClickedBlock().getLocation().toCenterLocation())
                        .spawn();
                event.getClickedBlock().getWorld().dropItemNaturally(
                        event.getClickedBlock().getLocation().toCenterLocation().add(0, 1, 0),
                        recipe.output
                );
                event.getItem().subtract();
                event.setCancelled(true);
                return;
            }
        }
    }
}