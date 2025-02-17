package io.github.pylonmc.pylon.base.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Set;

// TODO move to pylon-core
public enum MiningLevel {
    ANY(null),
    WOOD(Tag.INCORRECT_FOR_WOODEN_TOOL),
    STONE(Tag.INCORRECT_FOR_STONE_TOOL),
    GOLD(Tag.INCORRECT_FOR_GOLD_TOOL),
    IRON(Tag.INCORRECT_FOR_IRON_TOOL),
    DIAMOND(Tag.INCORRECT_FOR_DIAMOND_TOOL),
    NETHERITE(Tag.INCORRECT_FOR_NETHERITE_TOOL),
    ;

    private static final Set<Material> UNBREAKABLE = Set.of(
            Material.BARRIER,
            Material.BEDROCK,
            Material.STRUCTURE_BLOCK,
            Material.JIGSAW,
            Material.END_PORTAL_FRAME
    );

    private Tag<Material> incorrectTag;

    MiningLevel(Tag<Material> incorrectTag) {
        this.incorrectTag = incorrectTag;
    }

    public boolean canMine(Material material) {
        if (UNBREAKABLE.contains(material)) {
            return false;
        } else if (incorrectTag == null) {
            return !material.createBlockData().requiresCorrectToolForDrops();
        } else {
            return !incorrectTag.isTagged(material);
        }
    }

    public boolean isAtLeast(@NotNull MiningLevel level) {
        return this.ordinal() >= level.ordinal();
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
