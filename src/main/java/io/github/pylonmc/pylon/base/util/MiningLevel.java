package io.github.pylonmc.pylon.base.util;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

// TODO move to pylon-core
public enum MiningLevel {
    ANY(0, null),
    WOOD(1, Tag.INCORRECT_FOR_WOODEN_TOOL),
    GOLD(1, Tag.INCORRECT_FOR_GOLD_TOOL),
    STONE(2, Tag.INCORRECT_FOR_STONE_TOOL),
    IRON(3, Tag.INCORRECT_FOR_IRON_TOOL),
    DIAMOND(4, Tag.INCORRECT_FOR_DIAMOND_TOOL),
    // Internally netherite is higher than diamond, but practically they are the same
    NETHERITE(4, Tag.INCORRECT_FOR_NETHERITE_TOOL),
    ;

    private static final Set<Material> UNBREAKABLE = Set.of(
            Material.BARRIER,
            Material.BEDROCK,
            Material.STRUCTURE_BLOCK,
            Material.JIGSAW,
            Material.END_PORTAL_FRAME
    );

    private final int numericalLevel;
    private final Tag<Material> incorrectTag;

    MiningLevel(int numericalLevel, Tag<Material> incorrectTag) {
        this.numericalLevel = numericalLevel;
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
        return numericalLevel >= level.numericalLevel;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int getNumericalLevel() {
        return numericalLevel;
    }
}
