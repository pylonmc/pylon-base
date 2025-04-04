package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class WateringCan extends PylonItem<PylonItemSchema> implements BlockInteractor {
    public static final int HORIZONTAL_RANGE = 4;
    public static final int VERTRICAL_RANGE = 2;

    private static final double CROP_CHANCE = 0.01;
    private static final double SUGAR_CANE_CHANCE = 0.007;
    private static final double CACTUS_CHANCE = 0.01;
    private static final double SAPLING_CHANCE = 0.01;

    private static final Random random = new Random();

    public WateringCan(PylonItemSchema schema, ItemStack itemStack) {
        super(schema, itemStack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }

        event.setCancelled(true);

        Block center = event.getClickedBlock();
        if (center == null) {
            return;
        }

        water(center, HORIZONTAL_RANGE, VERTRICAL_RANGE);
    }

    public static void water(Block center, int horizontalRange, int verticalRange) {
        boolean wasAnyTickAttempted = false;
        for (int x = -horizontalRange; x < horizontalRange; x++) {
            for (int z = -horizontalRange; z < horizontalRange; z++) {
                Block block = center.getRelative(x, 0, z);

                // Search down (for a maximum of RANGE blocks) to find the first solid block
                int remainingYSteps = verticalRange;
                while (block.getType().isEmpty() && remainingYSteps > 0) {
                    block = block.getRelative(BlockFace.DOWN);
                    remainingYSteps--;
                }

                // Cannot be an 'or' because the compiler optimises it out lol
                if (tryGrowBlock(block)) {
                    wasAnyTickAttempted = true;
                }
            }
        }

        if (wasAnyTickAttempted) {
            playSound(center);
        }
    }

    private static boolean tryGrowBlock(@NotNull Block block) {
        if (block.getType() == Material.SUGAR_CANE) {
            return growSugarCane(block);
        } else if (block.getType() == Material.CACTUS) {
            return growCactus(block);
        } else if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() < ageable.getMaximumAge()) {
            return growCrop(block, ageable);
        } else if (Tag.SAPLINGS.isTagged(block.getType())) {
            return growSapling(block);
        }
        return false;
    }

    private static boolean growSugarCane(@NotNull Block block) {
        int height = 1;

        Block bottomBlock = block.getRelative(BlockFace.DOWN);
        while (bottomBlock.getType() == Material.SUGAR_CANE) {
            height++;
            bottomBlock = bottomBlock.getRelative(BlockFace.DOWN);
        }

        Block topBlock = block.getRelative(BlockFace.UP);
        while (topBlock.getType() == Material.SUGAR_CANE) {
            height++;
            topBlock = topBlock.getRelative(BlockFace.UP);
        }

        if (height < 3 && topBlock.getType() == Material.AIR) {
            new ParticleBuilder(Particle.HAPPY_VILLAGER)
                    .count(1)
                    .location(topBlock.getLocation().add(0.5, 0.0, 0.5))
                    .offset(0.3, 0.3, 0.3)
                    .spawn();

            if (random.nextDouble() < SUGAR_CANE_CHANCE) {
                topBlock.setType(Material.SUGAR_CANE);
            }

            return true;
        }

        return false;
    }

    private static boolean growCactus(@NotNull Block block) {
        int height = 1;

        Block bottomBlock = block.getRelative(BlockFace.DOWN);
        while (bottomBlock.getType() == Material.CACTUS) {
            height++;
            bottomBlock = bottomBlock.getRelative(BlockFace.DOWN);
        }

        Block topBlock = block.getRelative(BlockFace.UP);
        while (topBlock.getType() == Material.CACTUS) {
            height++;
            topBlock = topBlock.getRelative(BlockFace.UP);
        }

        if (height < 3 && topBlock.getType() == Material.AIR) {
            new ParticleBuilder(Particle.HAPPY_VILLAGER)
                    .count(1)
                    .location(topBlock.getLocation().add(0.5, 0.0, 0.5))
                    .offset(0.3, 0.3, 0.3)
                    .spawn();

            if (random.nextDouble() < CACTUS_CHANCE) {
                topBlock.setType(Material.CACTUS);
            }

            return true;
        }

        return false;
    }

    private static boolean growCrop(@NotNull Block block, @NotNull Ageable ageable) {
        new ParticleBuilder(Particle.SPLASH)
                    .count(3)
                    .location(block.getLocation().add(0.5, 0.0, 0.5))
                    .offset(0.3, 0, 0.3)
                    .spawn();

        if (random.nextDouble() < CROP_CHANCE) {
            ageable.setAge(ageable.getAge() + 1);
            block.setBlockData(ageable);
        }

        return true;
    }

    private static boolean growSapling(@NotNull Block block) {
        if (random.nextDouble() < SAPLING_CHANCE) {
            block.applyBoneMeal(BlockFace.UP);
        }

        return true;
    }

    private static void playSound(@NotNull Block block) {
        block.getLocation().getWorld().playSound(block.getLocation(), Sound.WEATHER_RAIN, 0.3F, 1.0F);
    }
}
