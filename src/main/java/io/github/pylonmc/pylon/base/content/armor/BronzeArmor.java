package io.github.pylonmc.pylon.base.content.armor;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.base.RebarArmor;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BronzeArmor extends RebarItem implements RebarArmor {
    public BronzeArmor(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull Key getEquipmentType() {
        return BaseUtils.baseKey("bronze");
    }
}
