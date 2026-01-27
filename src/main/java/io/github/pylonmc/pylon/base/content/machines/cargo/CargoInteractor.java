package io.github.pylonmc.pylon.base.content.machines.cargo;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.PylonDirectionalBlock;
import io.github.pylonmc.rebar.block.base.PylonLogisticBlock;
import io.github.pylonmc.rebar.block.base.PylonMultiblock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.datatypes.PylonSerializers;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.logistics.LogisticGroup;
import io.github.pylonmc.rebar.util.PylonUtils;
import io.github.pylonmc.rebar.util.position.BlockPosition;
import io.github.pylonmc.rebar.util.position.ChunkPosition;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.HashMap;
import java.util.HashSet;
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

    @Setter @Getter protected @Nullable String targetLogisticGroup;
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
            setTargetLogisticGroup(null);
            return;
        }

        // Refresh list of target groups
        targetGroups.clear();
        PylonLogisticBlock targetLogisticBlock = BlockStorage.getAs(PylonLogisticBlock.class, targetBlock);
        if (targetLogisticBlock != null) {
            targetGroups.putAll(targetLogisticBlock.getLogisticGroups());
        } else {
            targetGroups.putAll(LogisticGroup.getVanillaLogisticSlots(targetBlock));
        }
        targetGroups.entrySet().removeIf(pair -> !isValidGroup(pair.getValue()));

        // Check target group still exists after refresh
        if (!targetGroups.containsKey(targetLogisticGroup)) {
            setTargetLogisticGroup(null);
        }

        // Find new logistic group if group does not exist
        if (targetLogisticGroup == null) {
            setTargetLogisticGroup(targetGroups.keySet()
                    .stream()
                    .sorted()
                    .findFirst()
                    .orElse(null)
            );
        }
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        Set<ChunkPosition> chunks = new HashSet<>();
        chunks.add(new ChunkPosition(getTargetBlock()));
        // FUCK DOUBLE CHESTS
        for (BlockFace face : PylonUtils.CARDINAL_FACES) {
            chunks.add(new ChunkPosition(getTargetBlock().getRelative(face)));
        }
        return chunks;
    }

    @Override
    public boolean checkFormed() {
        refreshTargetInfo();
        return true;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        Set<Block> blocks = new HashSet<>();
        blocks.add(getTargetBlock());
        // FUCK DOUBLE CHESTS
        for (BlockFace face : PylonUtils.CARDINAL_FACES) {
            blocks.add(getTargetBlock().getRelative(face));
        }
        return blocks.contains(otherBlock);
    }

    public class InventoryCycleItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            // Check if there is no target logistic group (because the target block doesn't have
            // any valid inventories)
            Block block = getTargetBlock();
            if (targetLogisticGroup == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name(Component.translatable("pylon.pylonbase.gui.no-target-logistic-group"));
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
            setTargetLogisticGroup(availableGroups.get((index + 1) % availableGroups.size()));
            notifyWindows();
        }
    }

    public abstract boolean isValidGroup(@NotNull LogisticGroup group);
}
