package io.github.pylonmc.pylon.base.content.armor;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonArmor;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BronzeArmor extends PylonItem implements PylonArmor {
    public BronzeArmor(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull Key getEquipmentType() {
        return BaseUtils.baseKey("bronze");
    }
}
