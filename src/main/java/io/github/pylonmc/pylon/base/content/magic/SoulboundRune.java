package io.github.pylonmc.pylon.base.content.magic;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.magic.base.Rune;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SoulboundRune extends Rune {
    private static final TranslatableComponent SOULBIND_MSG = Component.translatable("pylon.pylonbase.item.soulbound_rune.soulbind-message");
    private static final TranslatableComponent TOOLTIP = Component.translatable("pylon.pylonbase.item.soulbound_rune.tooltip");
    private static final TranslatableComponent FAIL_PICKUP_MSG = Component.translatable("pylon.pylonbase.item.soulbound_rune.pickupfail-message");
    private static final NamespacedKey SOULBOUND_KEY = new NamespacedKey(PylonBase.getInstance(), "soulbound");

    public SoulboundRune(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean isApplicableToTarget(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
        return !(PylonItem.fromStack(target) instanceof SoulboundRune) && !target.getPersistentDataContainer().has(SOULBOUND_KEY);
    }

    @Override
    public void onContactItem(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
        int consume = Math.min(rune.getAmount(), target.getAmount());

        ItemStack soulboundItem = ItemStackBuilder.of(target.asQuantity(consume))
                .lore(GlobalTranslator.render(TOOLTIP.arguments(
                        PylonArgument.of("playername", event.getPlayer().getName())
                ), event.getPlayer().locale()))
                .build();
        soulboundItem.editMeta(meta -> {
            meta.getPersistentDataContainer().set(SOULBOUND_KEY, PylonSerializers.UUID, event.getPlayer().getUniqueId());
        });

        // (N)Either left runes or targets
        int leftRunes = rune.getAmount() - consume;
        int leftTargets = target.getAmount() - consume;

        Location dropLocation = event.getItemDrop().getLocation();
        World world = dropLocation.getWorld();
        if (leftRunes > 0) {
            world.dropItemNaturally(dropLocation, rune.asQuantity(leftRunes)).setGlowing(true);
        }
        if (leftTargets > 0) {
            world.dropItemNaturally(dropLocation, target.asQuantity(leftTargets)).setGlowing(true);
        }
        world.dropItemNaturally(dropLocation, soulboundItem).setGlowing(true);

        target.setAmount(0);
        rune.setAmount(0);
        event.getPlayer().sendMessage(SOULBIND_MSG);
    }

    public static class SoulboundRuneTicker extends BukkitRunnable {

        @Override
        public void run() {
            for(Player player : Bukkit.getOnlinePlayers()){
                Inventory inv = player.getInventory();
                for(int i = 0; i < inv.getSize(); i++){
                    ItemStack curStack = inv.getItem(i);
                    if(curStack == null || curStack.isEmpty()) continue;
                    UUID ownerUUID = curStack.getPersistentDataContainer().get(SOULBOUND_KEY, PylonSerializers.UUID);
                    if(ownerUUID != null && !ownerUUID.equals(player.getUniqueId())){
                        System.out.println(ownerUUID);
                        System.out.println(player.getUniqueId());
                        player.dropItem(curStack);
                        player.sendMessage(FAIL_PICKUP_MSG.arguments(
                                PylonArgument.of("playername", Bukkit.getPlayer(ownerUUID).getName())
                        ));
                        inv.setItem(i, null);
                    }
                }
            }
        }
    }
}
