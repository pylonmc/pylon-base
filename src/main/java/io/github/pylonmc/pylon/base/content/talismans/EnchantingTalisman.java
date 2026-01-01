package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.talismans.base.PDCKeyTalisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.EnchantmentView;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EnchantingTalisman extends PDCKeyTalisman<Double, Double> {
    public final double bonusLevelChance = getSettings().getOrThrow("bonus-level-chance", ConfigAdapter.DOUBLE);
    public static final NamespacedKey ENCHANTING_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "enchanting_talisman");
    private static final NamespacedKey ENCHANTING_TALISMAN_BONUS_KEY = new NamespacedKey(PylonBase.getInstance(), "enchanting_talisman_bonus");
    private static final NamespacedKey ENCHANTING_ITEM_UUID_KEY = new NamespacedKey(PylonBase.getInstance(), "enchanting_talisman_uuid");

    public EnchantingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull NamespacedKey getPdcEffectKey() {
        return ENCHANTING_TALISMAN_BONUS_KEY;
    }

    @Override
    public @NotNull PersistentDataType<Double, Double> getPdcType() {
        return PersistentDataType.DOUBLE;
    }

    @Override
    public @NotNull Double getPdcValue() {
        return bonusLevelChance;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("bonus_level_chance", UnitFormat.PERCENT.format(bonusLevelChance * 100).decimalPlaces(2))
        );
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return ENCHANTING_TALISMAN_KEY;
    }

    public static class EnchantingListener implements Listener {
        @EventHandler
        public void onPreEnchant(PrepareItemEnchantEvent event) {
            Double bonusLevelChance = event.getEnchanter().getPersistentDataContainer().get(ENCHANTING_TALISMAN_BONUS_KEY, PersistentDataType.DOUBLE);
            if (bonusLevelChance == null) {
                return;
            }
            UUID itemId;
            String uuidStr = event.getItem().getItemMeta().getPersistentDataContainer().get(ENCHANTING_ITEM_UUID_KEY, PersistentDataType.STRING);
            if(uuidStr != null) {
                itemId = UUID.fromString(uuidStr);
            } else {
                itemId = UUID.randomUUID();
                event.getItem().editMeta(meta -> meta.getPersistentDataContainer().set(ENCHANTING_ITEM_UUID_KEY, PersistentDataType.STRING, itemId.toString()));
            }
            // Seed needs to not only be based off the enchantment seed but also the item, so the same enchantments are not always getting the buff
            for (EnchantmentOffer offer : event.getOffers()) {
                if (offer == null) continue;
                Random randGen = new Random(event.getView().getEnchantmentSeed()
                        ^ itemId.getLeastSignificantBits()
                        ^ itemId.getMostSignificantBits()
                        ^ offer.getEnchantment().displayName(0).hashCode()
                );
                if (randGen.nextDouble() > bonusLevelChance) {
                    return;
                }
                offer.setEnchantmentLevel(Math.min(offer.getEnchantmentLevel() + 1, offer.getEnchantment().getMaxLevel()));
            }
        }

        @EventHandler
        public void onEnchant(EnchantItemEvent event){
            // recreating the calculation above but only using the information available in EnchantItemEvent to change the level when it is actually applied to the item
            Double bonusLevelChance = event.getEnchanter().getPersistentDataContainer().get(ENCHANTING_TALISMAN_BONUS_KEY, PersistentDataType.DOUBLE);
            if (bonusLevelChance == null) {
                return;
            }
            if(!(event.getEnchanter().getOpenInventory() instanceof EnchantmentView etableView)){
                return;
            }
            String itemIdStr = event.getItem().getPersistentDataContainer().get(ENCHANTING_ITEM_UUID_KEY, PersistentDataType.STRING);
            if(itemIdStr == null){
                return;
            }
            UUID itemId = UUID.fromString(itemIdStr);
            for(Enchantment enchant : event.getEnchantsToAdd().keySet()){
                Random randGen = new Random(etableView.getEnchantmentSeed()
                        ^ itemId.getLeastSignificantBits()
                        ^ itemId.getMostSignificantBits()
                        ^ enchant.displayName(0).hashCode()
                );
                if (randGen.nextDouble() > bonusLevelChance) {
                    return;
                }
                event.getEnchantsToAdd().replace(enchant, event.getEnchantsToAdd().get(enchant) + 1);
            }
        }
    }
}
