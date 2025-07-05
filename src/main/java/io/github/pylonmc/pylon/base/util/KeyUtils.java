package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.base.PylonBase;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class KeyUtils {
    public static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
