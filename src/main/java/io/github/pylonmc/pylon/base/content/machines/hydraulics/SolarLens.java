package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import kotlin.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;


public class SolarLens extends PylonBlock {
    public SolarLens(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public SolarLens(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<String, Pair<String, Integer>> getBlockTextureProperties() {
        Map<String, Pair<String, Integer>> properties = super.getBlockTextureProperties();
        Set<BlockFace> faces = ((MultipleFacing) getBlock().getBlockData()).getFaces();
        for (BlockFace face : PylonUtils.CARDINAL_FACES) {
            properties.put(face.name(), new Pair<>(faces.contains(face) ? "true" : "false", 2));
        }
        return properties;
    }
}
