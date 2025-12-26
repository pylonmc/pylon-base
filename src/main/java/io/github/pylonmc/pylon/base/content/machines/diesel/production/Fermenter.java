package io.github.pylonmc.pylon.base.content.machines.diesel.production;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class Fermenter extends PylonBlock implements PylonSimpleMultiblock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }
    }

    public Fermenter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    public Fermenter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 0, -1), new VanillaMultiblockComponent(Material.IRON_BLOCK));
        components.put(new Vector3i(0, 0, 1), new VanillaMultiblockComponent(Material.IRON_BLOCK));
        components.put(new Vector3i(-1, 0, 0), new PylonMultiblockComponent(BaseKeys.ITEM_INPUT_HATCH));
        components.put(new Vector3i(-1, 0, 0), new PylonMultiblockComponent(BaseKeys.FLUID_OUTPUT_HATCH));
        components.put(new Vector3i(-1, 0, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(-1, 0, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(1, 0, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(1, 0, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));

        for (int x = -1; x < 1; x++) {
            for (int y = 1 ; y < 5; y++) {
                for (int z = -1; z < 1; z++) {
                    Vector3i position = new Vector3i();
                    if (x == 0 && z == 0) {
                        components.put(position, new PylonMultiblockComponent(BaseKeys.FERMENTER_CORE));
                    } else {
                        components.put(position, new PylonMultiblockComponent(BaseKeys.FERMENTER_CASING));
                    }
                }
            }
        }

        components.remove(new Vector3i(-1, 1, 0));

        return components;
    }
}