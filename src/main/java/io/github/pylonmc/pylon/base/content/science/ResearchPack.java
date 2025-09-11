package io.github.pylonmc.pylon.base.content.science;

import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ResearchPack extends PylonItem implements PylonInteractor {

    public final int points = getSettings().getOrThrow("points", ConfigAdapter.INT);
    public final int cooldownTicks = getSettings().getOrThrow("cooldown-ticks", ConfigAdapter.INT);

    public ResearchPack(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
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

        event.getPlayer().setCooldown(getStack(), cooldownTicks);
        event.getItem().subtract();
    }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("points", UnitFormat.RESEARCH_POINTS.format(points)),
                PylonArgument.of("cooldown", UnitFormat.SECONDS.format(cooldownTicks / 20.0))
        );
    }
}
