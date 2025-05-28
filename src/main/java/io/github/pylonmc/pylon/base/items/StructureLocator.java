package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public class StructureLocator extends PylonItem implements Interactor {
    // I am well aware that this is cursed and prone to breaking. Best way I could find to do this through BlockSettings since StructureType doesn't have a fromString method.
    public final StructureType structure = (StructureType)StructureType.class.getField(getSettings().getOrThrow("structure", String.class).toUpperCase()).get(null);
    public final int radius = getSettings().getOrThrow("radius", Integer.class);
    private final TranslatableComponent FAILED_TO_FIND = Component.translatable("pylon.pylonbase.message.structurecompass.find_fail").arguments(
            PylonArgument.of("struct", Component.text(structure.getKey().getKey().replace('_', ' '))),
            PylonArgument.of("range", Component.text(radius))
    );
    private final TranslatableComponent REFRESHED_LOCATION = Component.translatable("pylon.pylonbase.message.structurecompass.refreshed");
    private static final TranslatableComponent ALREADY_USED = Component.translatable("pylon.pylonbase.message.structurecompass.alreadyused");
    private static final NamespacedKey COMPASS_USED_KEY = pylonKey("structure_compass_used");

    public StructureLocator(@NotNull ItemStack stack) throws NoSuchFieldException, IllegalAccessException {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        // No NPE since PylonItemListener uses event.getItem() already and does the null check
        assert event.getItem() != null;
        if(event.getItem().getItemMeta().getPersistentDataContainer().has(COMPASS_USED_KEY)){
            event.getPlayer().sendMessage(ALREADY_USED);
            return;
        }
        CompassMeta meta = (CompassMeta) event.getItem().getItemMeta();
        StructureSearchResult structLocation = event.getPlayer().getWorld().locateNearestStructure(
                event.getPlayer().getLocation(),
                structure,
                radius,
                true
        );
        if (structLocation == null) {
            event.getPlayer().sendMessage(FAILED_TO_FIND);
            return;
        }
        meta.setLodestoneTracked(true);
        meta.setLodestone(structLocation.getLocation());
        meta.getPersistentDataContainer().set(COMPASS_USED_KEY, PersistentDataType.BOOLEAN, true);
        event.getItem().setItemMeta(meta);
        event.getPlayer().sendMessage(REFRESHED_LOCATION);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of("struct", Component.text(structure.getKey().getKey().replace('_', ' ')),
                "range", Component.text(radius));
    }
}
