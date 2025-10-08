package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author balugaq
 */
public class LavaSponge extends PowerfulSponge {
    private static final Config settings = Settings.get(BaseKeys.LAVA_SPONGE);
    private static final int CHECK_RANGE = settings.getOrThrow("check-range", ConfigAdapter.INT);

    public LavaSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public LavaSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public boolean isAbsorbable(@NotNull Block block) {
        return block.getType() == Material.LAVA || block.getType() == Material.LAVA_CAULDRON;
    }

    @Override
    public void absorb(@NotNull Block block) {
        if (block.getType() == Material.LAVA) {
            block.setType(Material.AIR);
        } else if (block.getType() == Material.LAVA_CAULDRON) {
            block.setType(Material.CAULDRON);
        }
    }

    @Override
    public int getRange() {
        return CHECK_RANGE;
    }

    @Override
    public void toWetSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge, new BlockBreakContext.PluginBreak(sponge, false));
        BlockStorage.placeBlock(sponge, BaseKeys.HOT_LAVA_SPONGE);
    }

    public void tick(double deltaSeconds) {
        tryAbsorbNearbyBlocks();
    }

    /**
     * @author balugaq
     */
    public static class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("check-range", UnitFormat.BLOCKS.format(CHECK_RANGE).decimalPlaces(1))
            );
        }
    }
}
