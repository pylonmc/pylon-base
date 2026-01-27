package io.github.pylonmc.pylon.content.combat;

import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.base.RebarWeapon;
import io.papermc.paper.tag.EntityTags;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class BeheadingSword extends RebarItem implements RebarWeapon {

    private final double normalEntityHeadChance = getSettings().getOrThrow("head-chance.normal-entity", ConfigAdapter.DOUBLE);
    private final double witherSkeletonHeadChance = getSettings().getOrThrow("head-chance.wither-skeleton", ConfigAdapter.DOUBLE);

    public BeheadingSword(@NotNull ItemStack stack) {
        super(stack);
    }

    private static final Map<EntityType, ItemStack> ENTITY_HEADS = Map.of(
            EntityType.CREEPER, new ItemStack(Material.CREEPER_HEAD),
            EntityType.PIGLIN, new ItemStack(Material.PIGLIN_HEAD),
            EntityType.ENDER_DRAGON, new ItemStack(Material.DRAGON_HEAD),
            EntityType.ZOMBIE, new ItemStack(Material.ZOMBIE_HEAD),
            EntityType.SKELETON, new ItemStack(Material.SKELETON_SKULL)
    );

    @Override
    public void onUsedToKillEntity(@NotNull EntityDeathEvent event) {
        if (EntityTags.MINECARTS.isTagged(event.getEntityType())) {
            return;
        }
        if (event.getEntityType() == EntityType.WITHER_SKELETON) {
            if (ThreadLocalRandom.current().nextFloat() < witherSkeletonHeadChance && !event.getDrops().contains(new ItemStack(Material.WITHER_SKELETON_SKULL))) {
                event.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
            }
            return;
        }
        if (ThreadLocalRandom.current().nextFloat() > normalEntityHeadChance) {
            return;
        }
        ItemStack head;
        if (event.getEntity().getType() == EntityType.PLAYER) {
            // This cast is safe because RebarItemListener only calls this listener when the killer is a player
            head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer((Player) event.getEntity());
            head.setItemMeta(meta);
        } else {
            if (!ENTITY_HEADS.containsKey(event.getEntityType())) {
                return;
            }
            head = ENTITY_HEADS.get(event.getEntityType());
        }
        event.getDrops().add(head);
    }

    @Override
    public @NotNull List<RebarArgument> getPlaceholders() {
        return List.of(
                RebarArgument.of("default-chance", normalEntityHeadChance * 100),
                RebarArgument.of("wither-skeleton-chance", witherSkeletonHeadChance * 100)
        );
    }
}
