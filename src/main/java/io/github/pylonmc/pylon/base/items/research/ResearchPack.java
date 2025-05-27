package io.github.pylonmc.pylon.base.items.research;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class ResearchPack extends PylonItem implements Interactor {

    public static final NamespacedKey RESEARCH_PACK_1_KEY = pylonKey("research_pack_1");

    public static final ItemStack RESEARCH_PACK_1_STACK = ItemStackBuilder.pylonItem(Material.BOOK, RESEARCH_PACK_1_KEY)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    public final int points = getSettings().getOrThrow("points", Integer.class);

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

        event.getItem().subtract();
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of("points", UnitFormat.RESEARCH_POINTS.format(points));
    }
}
