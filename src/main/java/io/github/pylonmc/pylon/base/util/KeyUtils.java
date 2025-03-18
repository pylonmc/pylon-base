package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.base.PylonBase;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;


public final class KeyUtils {
    private KeyUtils() {}

    public static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
