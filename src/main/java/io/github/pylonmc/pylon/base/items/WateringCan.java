package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class WateringCan extends PylonItemSchema {

    private final int horizontalRange = getSettings().getOrThrow("range.horizontal", Integer.class);
    private final int verticalRange = getSettings().getOrThrow("range.vertical", Integer.class);

    private final double cropChance = getSettings().getOrThrow("chances.crops", Double.class);
    private final double sugarCaneChance = getSettings().getOrThrow("chances.sugar_cane", Double.class);
    private final double cactusChance = getSettings().getOrThrow("chances.cactus", Double.class);
    private final double saplingChance = getSettings().getOrThrow("chances.sapling", Double.class);

    private static final Random random = new Random();

    public WateringCan(@NotNull NamespacedKey key, @NotNull Class<? extends @NotNull PylonItem<? extends @NotNull PylonItemSchema>> itemClass, @NotNull ItemStack template) {
        super(key, itemClass, template);
    }

    public static class WateringCanItem extends PylonItem<WateringCan> implements BlockInteractor {

        public WateringCanItem(WateringCan schema, ItemStack itemStack) {
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

            water(center, getSchema().horizontalRange, getSchema().verticalRange, getSchema());
        }

        public static void water(Block center, int horizontalRange, int verticalRange, WateringCan wateringCan) {
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
                    if (tryGrowBlock(block, wateringCan)) {
                        wasAnyTickAttempted = true;
                    }
                }
            }

            if (wasAnyTickAttempted) {
                playSound(center);
            }
        }

        private static boolean tryGrowBlock(@NotNull Block block, WateringCan wateringCan) {
            if (block.getType() == Material.SUGAR_CANE) {
                return growSugarCane(block, wateringCan);
            } else if (block.getType() == Material.CACTUS) {
                return growCactus(block, wateringCan);
            } else if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() < ageable.getMaximumAge()) {
                return growCrop(block, ageable, wateringCan);
            } else if (Tag.SAPLINGS.isTagged(block.getType())) {
                return growSapling(block, wateringCan);
            }
            return false;
        }

        private static boolean growSugarCane(@NotNull Block block, WateringCan wateringCan) {
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

                if (random.nextDouble() < wateringCan.sugarCaneChance) {
                    topBlock.setType(Material.SUGAR_CANE);
                }

                return true;
            }

            return false;
        }

        private static boolean growCactus(@NotNull Block block, WateringCan wateringCan) {
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

                if (random.nextDouble() < wateringCan.cactusChance) {
                    topBlock.setType(Material.CACTUS);
                }

                return true;
            }

            return false;
        }

        private static boolean growCrop(@NotNull Block block, @NotNull Ageable ageable, WateringCan wateringCan) {
            new ParticleBuilder(Particle.SPLASH)
                    .count(3)
                    .location(block.getLocation().add(0.5, 0.0, 0.5))
                    .offset(0.3, 0, 0.3)
                    .spawn();

            if (random.nextDouble() < wateringCan.cropChance) {
                ageable.setAge(ageable.getAge() + 1);
                block.setBlockData(ageable);
            }

            return true;
        }

        private static boolean growSapling(@NotNull Block block, WateringCan wateringCan) {
            if (random.nextDouble() < wateringCan.saplingChance) {
                block.applyBoneMeal(BlockFace.UP);
            }

            return true;
        }

        private static void playSound(@NotNull Block block) {
            block.getLocation().getWorld().playSound(block.getLocation(), Sound.WEATHER_RAIN, 0.3F, 1.0F);
        }
    }
}
