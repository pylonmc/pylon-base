package io.github.pylonmc.pylon.base.items.research.pack;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.item.research.Research;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public abstract class ResearchPack extends PylonItem implements Interactor {

    protected ResearchPack(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    abstract int getPoints();

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        long originalPoints = Research.getResearchPoints(player);
        Research.setResearchPoints(player, originalPoints + getPoints());

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
                PylonArgument.of("points", getPoints())
        ));

        event.getItem().subtract();
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
        return Map.of("points", Component.text(getPoints()));
    }
}
