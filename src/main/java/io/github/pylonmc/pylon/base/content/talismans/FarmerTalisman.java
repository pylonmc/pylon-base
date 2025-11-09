package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FarmerTalisman extends Talisman {
    public float extraCropChance = getSettings().getOrThrow("extra-crop-chance", ConfigAdapter.FLOAT);
    public int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    public static final NamespacedKey FARMER_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "farmer_talisman");
    public static final NamespacedKey FARMER_TALISMAN_CHANCE_KEY = new NamespacedKey(PylonBase.getInstance(), "farmer_talisman_chance");
    private static final Random RNG = new Random();

    public FarmerTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        player.getPersistentDataContainer().set(FARMER_TALISMAN_CHANCE_KEY, PersistentDataType.FLOAT, extraCropChance);
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        player.getPersistentDataContainer().remove(FARMER_TALISMAN_CHANCE_KEY);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return FARMER_TALISMAN_KEY;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("extra_crop_chance", UnitFormat.PERCENT.format(extraCropChance * 100)));
    }

    public static class FarmerTalismanListener implements Listener {
        @EventHandler
        public void onBlockBreak(BlockDropItemEvent event) {
            if (!event.getPlayer().getPersistentDataContainer().has(FARMER_TALISMAN_CHANCE_KEY)) {
                return;
            }
            List<Item> additionalDrops = new ArrayList<>();
            for (Item drop : event.getItems()) {
                if (!Tag.CROPS.isTagged(drop.getItemStack().getType())) {
                    continue;
                }
                if (RNG.nextFloat() > event.getPlayer().getPersistentDataContainer().get(FARMER_TALISMAN_CHANCE_KEY, PersistentDataType.FLOAT)) {
                    continue;
                }
                additionalDrops.add(drop.getWorld().dropItem(drop.getLocation(), drop.getItemStack().clone()));
            }
            event.getItems().addAll(additionalDrops);
        }
    }
}
