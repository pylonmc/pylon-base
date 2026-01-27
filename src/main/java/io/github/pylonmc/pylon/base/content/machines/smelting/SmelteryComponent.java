package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmelteryComponent extends PylonBlock {

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private @Nullable SmelteryController controller = null;

    @SuppressWarnings("unused")
    public SmelteryComponent(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public SmelteryComponent(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }
}
