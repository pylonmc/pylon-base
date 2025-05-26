package io.github.pylonmc.pylon.base.items.weapons;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Weapon;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.tag.EntityTags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class BeheadingSword extends PylonItemSchema {

    private final double normalEntityHeadChance = getSettings().getOrThrow("head-chance.normal-entity", Double.class);
    private final double witherSkeletonHeadChance = getSettings().getOrThrow("head-chance.wither-skeleton", Double.class);

    public BeheadingSword(NamespacedKey key, Function<NamespacedKey, ItemStack> templateSupplier) {
        super(key, BeheadingSwordItem.class, templateSupplier);
        template.setData(DataComponentTypes.MAX_DAMAGE, getSettings().getOrThrow("durability", Integer.class));
    }

    public static class BeheadingSwordItem extends PylonItem<BeheadingSword> implements Weapon {

        private static final Map<EntityType, ItemStack> ENTITY_HEAD_MAP = Map.of(
                EntityType.CREEPER, new ItemStack(Material.CREEPER_HEAD),
                EntityType.PIGLIN, new ItemStack(Material.PIGLIN_HEAD),
                EntityType.ENDER_DRAGON, new ItemStack(Material.DRAGON_HEAD),
                EntityType.ZOMBIE, new ItemStack(Material.ZOMBIE_HEAD),
                EntityType.SKELETON, new ItemStack(Material.SKELETON_SKULL)
        );

        public BeheadingSwordItem(BeheadingSword schema, ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public void onUsedToKillEntity(@NotNull EntityDeathEvent event) {
            if (EntityTags.MINECARTS.isTagged(event.getEntityType())) {
                return;
            }
            if (event.getEntityType() == EntityType.WITHER_SKELETON) {
                if (ThreadLocalRandom.current().nextFloat() < getSchema().witherSkeletonHeadChance && !event.getDrops().contains(new ItemStack(Material.WITHER_SKELETON_SKULL))) {
                    event.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                }
                return;
            }
            if (ThreadLocalRandom.current().nextFloat() > getSchema().normalEntityHeadChance) {
                return;
            }
            ItemStack head;
            if (event.getEntity().getType() == EntityType.PLAYER) {
                // This cast is safe because PylonItemListener only calls this listener when the killer is a player
                Player killer = ((Player) event.getDamageSource().getCausingEntity());
                head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(killer);
                head.setItemMeta(meta);
            } else {
                if (!ENTITY_HEAD_MAP.containsKey(event.getEntityType())) {
                    return;
                }
                head = ENTITY_HEAD_MAP.get(event.getEntityType());
            }
            event.getDrops().add(head);
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
            return Map.of(
                    "default-chance", Component.text(getSchema().normalEntityHeadChance * 100),
                      "wither-skeleton-chance", Component.text(getSchema().witherSkeletonHeadChance * 100)
            );
        }
    }
}
