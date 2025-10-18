package io.github.pylonmc.pylon.base.content.machines.cargo;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.cargo.CargoPointType;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CargoBuffer extends PylonBlock implements PylonDirectionalBlock, PylonGuiBlock {

    private static final NamespacedKey FACE_KEY = baseKey("face");

    private final BlockFace face;
    private final VirtualInventory inventory = new VirtualInventory(1);

    public CargoBuffer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        if (!(context instanceof BlockCreateContext.PlayerPlace playerPlaceContext)) {
            throw new IllegalArgumentException("Cargo extractor can only be placed by player");
        }

        face = PylonUtils.rotateToPlayerFacing(playerPlaceContext.getPlayer(), BlockFace.NORTH, false);

        createCargoPoint(face, CargoPointType.OUTPUT);
        createCargoPoint(face.getOppositeFace(), CargoPointType.INPUT);
    }

    public CargoBuffer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        face = pdc.get(FACE_KEY, PylonSerializers.BLOCK_FACE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(FACE_KEY, PylonSerializers.BLOCK_FACE, face);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # x # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', inventory)
                .build();
    }

    @Override
    public @Nullable BlockFace getFacing() {
        return face;
    }

    @Override
    public @Nullable ItemStack[] getCargoItems(BlockFace face) {
        return inventory.getItems();
    }

    @Override
    public void setCargoItem(BlockFace face, int slot, ItemStack newItem) {
        Preconditions.checkArgument(face == this.face.getOppositeFace());
        inventory.setItem(UpdateReason.SUPPRESSED, slot, newItem);
    }
}
