package io.github.pylonmc.pylon.base.items.research;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class ResearchPack extends PylonItem<ResearchPack.Schema> implements Interactor {

    public ResearchPack(@NotNull Schema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    public static class Schema extends PylonItemSchema {

        @Getter
        private final int points = getSettings().getOrThrow("points", Integer.class);

        public Schema(@NotNull NamespacedKey key, @NotNull Class<? extends @NotNull PylonItem<? extends @NotNull PylonItemSchema>> itemClass, @NotNull Function<@NotNull NamespacedKey, @NotNull ItemStack> templateSupplier) {
            super(key, itemClass, templateSupplier);
        }
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        long points = getSchema().points;
        long originalPoints = Research.getResearchPoints(player);
        Research.setResearchPoints(player, originalPoints + points);

        int happeningNum = ThreadLocalRandom.current().nextInt(4);
        Component happening = Component.translatable(
                "pylon.pylonbase.message.research_pack.happening." + happeningNum
        );
        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.research_pack.message",
                PylonArgument.of("happening", happening)
        ));
        player.sendMessage(Component.translatable(
                "pylon.pylonbase.message.gained_research_points",
                PylonArgument.of("points", points)
        ));
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of("points", UnitFormat.RESEARCH_POINTS.format(getSchema().points));
    }
}
