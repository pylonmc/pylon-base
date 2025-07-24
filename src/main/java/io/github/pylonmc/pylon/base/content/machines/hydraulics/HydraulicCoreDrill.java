package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class HydraulicCoreDrill extends PylonBlock implements PylonSimpleMultiblock {

    private static final NamespacedKey FACE_KEY = baseKey("face");
    private final BlockFace face;

    @SuppressWarnings("unused")
    public HydraulicCoreDrill(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        if (!(context instanceof BlockCreateContext.PlayerPlace playerPlace)) {
            throw new IllegalStateException("This block can only be placed by a player");
        }
        face = TransformUtil.yawToFace(playerPlace.getPlayer().getYaw());
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrill(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        face = pdc.get(FACE_KEY, PylonSerializers.BLOCK_FACE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(FACE_KEY, PylonSerializers.BLOCK_FACE, face);
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        return PylonSimpleMultiblock.rotateComponentsToFace(Map.of(
                new Vector3i(1, 0, 0), MultiblockComponent.of(Material.NETHER_BRICK_FENCE),
                new Vector3i(-1, 0, 0), MultiblockComponent.of(Material.NETHER_BRICK_FENCE),
                new Vector3i(0, 0, 1), MultiblockComponent.of(Material.NETHER_BRICK_FENCE),
                new Vector3i(0, 0, -1), MultiblockComponent.of(Material.NETHER_BRICK_FENCE)

//                new Vector3i(1, -1, 0), MultiblockComponent.of(BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH),
//                new Vector3i(-1, -1, 0), MultiblockComponent.of(BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH),
//                new Vector3i(0, -1, 1), MultiblockComponent.of(BaseKeys.),
//                new Vector3i(0, -1, -1), MultiblockComponent.of(BaseKeys.)
        ), face);
    }
}
