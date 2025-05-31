package io.github.pylonmc.pylon.base.items.tools.watering;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class Sprinkler extends PylonBlock implements PylonTickingBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "range", UnitFormat.BLOCKS.format(SETTINGS.horizontalRange())
            );
        }
    }

    public static final NamespacedKey KEY = pylonKey("sprinkler");

    public static final WateringSettings SETTINGS = WateringSettings.fromConfig(Settings.get(KEY));

    @SuppressWarnings("unused")
    public Sprinkler(Block block, BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public Sprinkler(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return 5;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER) {
            return;
        }

        WateringCan.water(getBlock(), SETTINGS);

        new ParticleBuilder(Particle.SPLASH)
                .count(5)
                .location(getBlock().getLocation().add(0.5, 0.5, 0.5))
                .spawn();
    }

    public static class SprinklerPlaceListener implements Listener {
        @EventHandler
        private static void handle(@NotNull PrePylonBlockPlaceEvent event) {
            if (!(event.getPylonBlock() instanceof Sprinkler)) {
                return;
            }

            int horizontalRadiusToCheck = 2 * SETTINGS.horizontalRange();
            int verticalRadiusToCheck = 2 * SETTINGS.verticalRange();
            for (int x = -horizontalRadiusToCheck; x < horizontalRadiusToCheck; x++) {
                for (int z = -horizontalRadiusToCheck; z < horizontalRadiusToCheck; z++) {
                    for (int y = -verticalRadiusToCheck; y < verticalRadiusToCheck; y++) {
                        if (!(BlockStorage.get(event.getBlock().getRelative(x, y, z)) instanceof Sprinkler)) {
                            continue;
                        }

                        event.setCancelled(true);
                        if (event.getContext() instanceof BlockCreateContext.PlayerPlace context) {
                            context.getPlayer().sendMessage(Component.translatable(
                                    "pylon.pylonbase.message.sprinkler_too_close",
                                    PylonArgument.of("radius", horizontalRadiusToCheck)
                            ));
                        }
                        break;
                    }
                }
            }
        }
    }
}
