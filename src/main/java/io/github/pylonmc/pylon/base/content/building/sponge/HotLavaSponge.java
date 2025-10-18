package io.github.pylonmc.pylon.base.content.building.sponge;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author balugaq
 * @see PowerfulSponge
 * @see LavaSponge
 */
public class HotLavaSponge extends PowerfulWaterSponge {
    private static final Config SETTINGS = Settings.get(BaseKeys.HOT_LAVA_SPONGE);
    private static final int CHECK_RANGE = SETTINGS.getOrThrow("check-range", ConfigAdapter.INT);
    private final Location PARTICLE_DISPLAY_LOCATION = getBlock().getLocation().clone().add(0.5, 0.5, 0.5);

    public HotLavaSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public HotLavaSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void spawnParticles() {
        new ParticleBuilder(Particle.FLAME)
                .location(PARTICLE_DISPLAY_LOCATION)
                .count(3)
                .spawn();
    }

    @Override
    public int checkRange() {
        return CHECK_RANGE;
    }

    @Override
    public void updateSponge(@NotNull Block sponge) {
        super.updateSponge(sponge);
        BlockStorage.placeBlock(sponge, BaseKeys.LAVA_SPONGE);
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