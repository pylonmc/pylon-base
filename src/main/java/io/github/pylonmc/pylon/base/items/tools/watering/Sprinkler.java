package io.github.pylonmc.pylon.base.items.tools.watering;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class Sprinkler extends PylonBlock implements PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("sprinkler");

    public static final int HORIZONTAL_RANGE = 4;
    public static final int VERTICAL_RANGE = 4;

    // TODO make this more configurable
    private static final WateringSettings SETTINGS = new WateringSettings(
            HORIZONTAL_RANGE,
            VERTICAL_RANGE,
            0.01,
            0.007,
            0.01,
            0.01,
            Sound.WEATHER_RAIN
    );

    @SuppressWarnings("unused")
    public Sprinkler(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public Sprinkler(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block);
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

            int horizontalRadiusToCheck = 2 * HORIZONTAL_RANGE;
            int verticalRadiusToCheck = 2 * VERTICAL_RANGE;
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
