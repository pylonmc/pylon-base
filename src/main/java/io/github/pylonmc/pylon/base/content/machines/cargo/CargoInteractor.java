package io.github.pylonmc.pylon.base.content.machines.cargo;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonLogisticBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroup;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CargoInteractor extends PylonBlock implements PylonDirectionalBlock, PylonMultiblock {

    public static final NamespacedKey TARGET_LOGISTIC_GROUP_KEY = BaseUtils.baseKey("target_logistic_group");
    public static final List<Material> GROUP_MATERIALS = List.of(
            Material.LIGHT_BLUE_CONCRETE,
            Material.CYAN_CONCRETE,
            Material.BLUE_CONCRETE,
            Material.PURPLE_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.PINK_CONCRETE
    );

    public @Nullable String targetLogisticGroup;
    public final Map<String, LogisticGroup> targetGroups = new HashMap<>();

    protected CargoInteractor(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setFacing(context.getFacing());
    }

    protected CargoInteractor(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        refreshTargetInfo();
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, TARGET_LOGISTIC_GROUP_KEY, PylonSerializers.STRING, targetLogisticGroup);
    }

    public @NotNull Block getTargetBlock() {
        return getBlock().getRelative(getFacing().getOppositeFace());
    }

    public void refreshTargetInfo() {
        Block targetBlock = getTargetBlock();
        if (targetBlock.isEmpty()) {
            // No target any more, so target group becomes null
            targetLogisticGroup = null;
            return;
        }

        // Refresh list of target groups
        targetGroups.clear();
        PylonLogisticBlock targetLogisticBlock = BlockStorage.getAs(PylonLogisticBlock.class, targetBlock);
        if (targetLogisticBlock != null) {
            targetGroups.putAll(targetLogisticBlock.getLogisticGroups());
        } else {
            targetGroups.putAll(PylonUtils.getVanillaLogisticSlots(targetBlock));
        }
        targetGroups.entrySet().removeIf(pair -> !isValidGroup(pair.getValue()));

        // Check target group still exists after refresh
        if (!targetGroups.containsKey(targetLogisticGroup)) {
            targetLogisticGroup = null;
        }

        // Find new logistic group if group does not exist
        if (targetLogisticGroup == null) {
            targetLogisticGroup = targetGroups.keySet()
                    .stream()
                    .sorted()
                    .findFirst()
                    .orElse(null);
        }

        Bukkit.getLogger().severe("1 " + targetGroups.size() + " " + targetGroups.keySet());
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getTargetBlock()));
    }

    @Override
    public boolean checkFormed() {
        refreshTargetInfo();
        return true;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return otherBlock.equals(getTargetBlock());
    }

    public class InventoryCycleItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            // Check if there is no target logistic group (because the target block doesn't have
            // any valid inventories)
            Block block = getTargetBlock();
            if (targetLogisticGroup == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name("pylon.pylonbase.gui.no-target-logistic-group");
            }

            // Find index of current group
            Preconditions.checkState(!block.isEmpty()); // Should be true if targetLogisticGroup is not null
            List<String> availableGroups = targetGroups
                    .keySet()
                    .stream()
                    .sorted()
                    .toList();
            int index = availableGroups.indexOf(targetLogisticGroup);
            Preconditions.checkState(index != -1);

            // Find display name of current group
            Component displayName;
            PylonBlock pylonBlock = BlockStorage.get(block);
            if (pylonBlock instanceof PylonLogisticBlock) {
                displayName = Component.translatable(
                        "pylon." + pylonBlock.getKey().getNamespace() + ".inventory." + targetLogisticGroup
                );
            } else {
                displayName = Component.translatable("pylon.pylonbase.inventory." + targetLogisticGroup);
            }

            // Construct display item
            Material material = GROUP_MATERIALS.get(index % GROUP_MATERIALS.size());
            ItemStackBuilder builder = ItemStackBuilder.gui(material, "logistic-group:" + index)
                    .name(Component.translatable("pylon.pylonbase.gui.logistic-group-cycle-item.name")
                            .arguments(PylonArgument.of("inventory", displayName))
                    );
            if (availableGroups.size() > 1) {
                builder.lore(Component.translatable("pylon.pylonbase.gui.logistic-group-cycle-item.lore"));
            }
            return builder;
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            if (targetLogisticGroup == null) {
                return;
            }

            List<String> availableGroups = targetGroups
                    .keySet()
                    .stream()
                    .sorted()
                    .toList();
            int index = availableGroups.indexOf(targetLogisticGroup);
            targetLogisticGroup = availableGroups.get((index + 1) % availableGroups.size());
            notifyWindows();
        }
    }

    public abstract boolean isValidGroup(@NotNull LogisticGroup group);
}
