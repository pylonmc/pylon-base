package io.github.pylonmc.pylon.base.content.resources;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInventoryTicker;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class IronBloom extends PylonItem implements PylonInventoryTicker {

    public static final long DAMAGE_INTERVAL = Settings.get(BaseKeys.IRON_BLOOM).getOrThrow("damage-interval", ConfigAdapter.LONG);
    public static final int UNPROTECTED_DAMAGE = Settings.get(BaseKeys.IRON_BLOOM).getOrThrow("unprotected-damage", ConfigAdapter.INT);

    private static final NamespacedKey TEMPERATURE_KEY = baseKey("temperature");
    public static final int MAX_TEMPERATURE = 12;

    private static final NamespacedKey WORKING_KEY = baseKey("working");
    public static final int MIN_WORKING = -15;
    public static final int MAX_WORKING = 15;

    public IronBloom(@NotNull ItemStack stack) {
        super(stack);
    }

    /**
     * Returns a temperature between 0 and 12 inclusive.
     */
    public int getTemperature() {
        return getStack().getPersistentDataContainer().getOrDefault(TEMPERATURE_KEY, PylonSerializers.INTEGER, 0);
    }

    /**
     * @param temperature the temperature to set, must be between 0 and 12 inclusive.
     */
    public void setTemperature(int temperature) {
        Preconditions.checkArgument(temperature >= 0 && temperature <= MAX_TEMPERATURE, "Temperature must be between 0 and 12 inclusive.");
        getStack().editPersistentDataContainer((pdc) -> pdc.set(TEMPERATURE_KEY, PylonSerializers.INTEGER, temperature));
    }

    /**
     * Returns a working between -15 and 15 inclusive.
     */
    public int getWorking() {
        return getStack().getPersistentDataContainer().getOrDefault(WORKING_KEY, PylonSerializers.INTEGER, 0);
    }

    /**
     * @param working the working to set, must be between -15 and 15 inclusive.
     */
    public void setWorking(int working) {
        Preconditions.checkArgument(working >= MIN_WORKING && working <= MAX_WORKING, "Working must be between 0 and 15 inclusive.");
        getStack().editPersistentDataContainer((pdc) -> pdc.set(WORKING_KEY, PylonSerializers.INTEGER, working));
    }

    @Override
    public long getTickInterval() {
        return DAMAGE_INTERVAL;
    }

    @Override
    public void onTick(@NotNull Player player) {
        if (PylonUtils.isPylonSimilar(player.getInventory().getItemInMainHand(), BaseItems.TONGS) ||
                PylonUtils.isPylonSimilar(player.getInventory().getItemInOffHand(), BaseItems.TONGS)) {
            return;
        }
        player.damage(UNPROTECTED_DAMAGE);
    }
}
