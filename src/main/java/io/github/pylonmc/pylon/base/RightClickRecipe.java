package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.util.BlockType;
import io.github.pylonmc.pylon.base.util.ItemType;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// TODO move to pylon-core
public record RightClickRecipe(NamespacedKey key, ItemType item, BlockType block, BlockType result) implements Keyed {
    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<RightClickRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "right_click")
    );

    static {
        RECIPE_TYPE.register();

        Bukkit.getPluginManager().registerEvent(
                PlayerInteractEvent.class,
                new Listener() {},
                EventPriority.NORMAL,
                (_ignored, event) -> {
                    if (event instanceof PlayerInteractEvent e) {
                        ItemStack item = e.getItem();
                        if (item == null) return;
                        Block block = e.getClickedBlock();
                        if (block == null) return;
                        if (e.getAction().isRightClick()) {
                            for (RightClickRecipe recipe : RECIPE_TYPE) {
                                if (recipe.item().matches(item) && recipe.block().matches(block)) {
                                    e.setCancelled(true);
                                    item.subtract();
                                    recipe.result().place(block);
                                    break;
                                }
                            }
                        }
                    }
                },
                PylonBase.getInstance(),
                true
        );
    }
}
