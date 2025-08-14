package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSponge;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PowerfulSponge extends PylonBlock implements PylonSponge {
    public static final String KEY_PLAYER = "player";

    public PowerfulSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        if (context instanceof BlockCreateContext.PlayerPlace p) {
            // Will it work...?
            BlockStorage.get(block).getSettings().set(KEY_PLAYER, p.getPlayer().getUniqueId());
        }
    }

    public static @Nullable Player getPlacer(@NotNull Block block) {
        return Bukkit.getPlayer(UUID.fromString(BlockStorage.get(block).getSettings().get(KEY_PLAYER, String.class)));
    }

    @Override
    public void onAbsorb(@NotNull SpongeAbsorbEvent event) {
        event.setCancelled(true);

        List<Block> blocks = getBlocksInManhattanDistance(event, getRange());
        Block sponge = event.getBlock();
        Player player = getPlacer(sponge);
        for (Block block : blocks) {
            // before absorbs, we need to check permission
            if (!BaseUtils.canBreakBlock(player, block)) {
                continue;
            }

            // now we ensured that the player can break the block
            absorb(block);
        }

        toWetSponge(sponge);
    }

    public @NotNull List<Block> getBlocksInManhattanDistance(@NotNull SpongeAbsorbEvent event, int distance) {
        List<Block> result = new ArrayList<>();

        if (distance < 0) {
            return result;
        }

        Block center = event.getBlock(); // sponge block
        World world = center.getWorld();
        WorldBorder border = world.getWorldBorder();
        int centerX = center.getX();
        int centerY = center.getY();
        int centerZ = center.getZ();

        for (int x = centerX - distance; x <= centerX + distance; x++) {
            int remainingX = distance - Math.abs(x - centerX);
            if (remainingX < 0) continue;

            for (int y = centerY - remainingX; y <= centerY + remainingX; y++) {
                int remainingY = remainingX - Math.abs(y - centerY);
                if (remainingY < 0) continue;

                for (int z = centerZ - remainingY; z <= centerZ + remainingY; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (border.isInside(block.getLocation())) {
                        continue;
                    }

                    if (!isAbsorbable(block)) {
                        continue;
                    }

                    result.add(block);
                }
            }
        }

        return result;
    }

    public abstract boolean isAbsorbable(@NotNull Block block);

    public abstract void absorb(@NotNull Block block);

    public abstract void toWetSponge(@NotNull Block sponge);

    public abstract int getRange();
}
