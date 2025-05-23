package io.github.pylonmc.pylon.base.items.tools.hammer;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HammerDiamond extends Hammer {

    public static final NamespacedKey KEY = pylonKey("hammer_diamond");

    public static final Material TOOL_MATERIAL = Material.DIAMOND_PICKAXE;
    public static final Material BASE_BLOCK = Material.DIAMOND_BLOCK;
    public static final MiningLevel MINING_LEVEL = MiningLevel.DIAMOND;
    public static final int COOLDOWN = getSettings(KEY).getOrThrow("cooldown", Integer.class);
    public static final Sound SOUND = Registry.SOUNDS.get(
            NamespacedKey.fromString(
                    getSettings(KEY).getOrThrow("sound", String.class)
            )
    );

    public static final ItemStack ITEM_STACK = createItemStack(KEY, TOOL_MATERIAL, (1.0/ 1) - 4, 2, 5);

    public HammerDiamond(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    protected Material getToolMaterial() {
        return TOOL_MATERIAL;
    }

    @Override
    protected Material getBaseBlock() {
        return BASE_BLOCK;
    }

    @Override
    protected MiningLevel getMiningLevel() {
        return MINING_LEVEL;
    }

    @Override
    protected int getCooldown() {
        return COOLDOWN;
    }

    @Override
    protected Sound getSound() {
        return SOUND;
    }
}
