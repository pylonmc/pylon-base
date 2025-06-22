package io.github.pylonmc.pylon.base.util;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public final class ExtraTags {

    private ExtraTags() {}

    public static final MaterialSetTag SEEDS = new MaterialSetTag(
            pylonKey("seeds"),
            Material.WHEAT_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.MELON_SEEDS,
            Material.TORCHFLOWER_SEEDS
    );
}
