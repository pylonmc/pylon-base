package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class StructureLocator extends PylonItem<StructureLocator.Schema> implements Interactor {
    private final TranslatableComponent FAILED_TO_FIND = Component.translatable("pylon.pylonbase.message.structurecompass.find_fail").arguments(
            PylonArgument.of("struct", Component.text(getSchema().structure.getKey().getKey().replace('_', ' '))),
            PylonArgument.of("range", Component.text(getSchema().radius))
    );
    private final TranslatableComponent REFRESHED_LOCATION = Component.translatable("pylon.pylonbase.message.structurecompass.refreshed");

    public StructureLocator(@NotNull Schema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        // No NPE since PylonItemListener uses event.getItem() already and does the null check
        assert event.getItem() != null;
        CompassMeta meta = (CompassMeta) event.getItem().getItemMeta();
        StructureSearchResult structLocation = event.getPlayer().getWorld().locateNearestStructure(
                event.getPlayer().getLocation(),
                getSchema().structure,
                getSchema().radius,
                true
        );
        if (structLocation == null) {
            event.getPlayer().sendMessage(FAILED_TO_FIND);
            return;
        }
        meta.setLodestoneTracked(true);
        meta.setLodestone(structLocation.getLocation());
        event.getItem().setItemMeta(meta);
        event.getPlayer().sendMessage(REFRESHED_LOCATION);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
        return Map.of("struct", Component.text(getSchema().structure.getKey().getKey().replace('_', ' ')),
                "range", Component.text(getSchema().radius));
    }


    public static class Schema extends PylonItemSchema {
        public final StructureType structure;
        public final int radius = getSettings().getOrThrow("radius", Integer.class);

        public Schema(@NotNull NamespacedKey key,
                      @NotNull Function<@NotNull NamespacedKey, @NotNull ItemStack> templateSupplier,
                      @NotNull StructureType structure) {
            super(key, StructureLocator.class, templateSupplier);
            this.structure = structure;
        }
    }
}
