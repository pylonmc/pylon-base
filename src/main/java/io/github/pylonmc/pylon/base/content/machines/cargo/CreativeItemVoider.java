package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonCargoBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroupType;
import io.github.pylonmc.pylon.core.logistics.slot.LogisticSlot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CreativeItemVoider extends PylonBlock
        implements PylonDirectionalBlock, PylonCargoBlock, PylonEntityHolderBlock {

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.PINK_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input");

    @SuppressWarnings("unused")
    public CreativeItemVoider(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing(), "input");
        setCargoTransferRate(Integer.MAX_VALUE);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.6)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(inputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, 0.125)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CreativeItemVoider(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    private static class VoiderLogisticSlot implements LogisticSlot {

        @Override
        public @Nullable ItemStack getItemStack() {
            return null;
        }

        @Override
        public long getAmount() {
            return 0;
        }

        @Override
        public long getMaxAmount(@NotNull ItemStack stack) {
            return Long.MAX_VALUE;
        }

        @Override
        public void set(@Nullable ItemStack stack, long amount) {
            // bye bye items
        }
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticGroupType.INPUT, new VoiderLogisticSlot());
    }
}
