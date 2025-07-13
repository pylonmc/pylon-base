package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonSplashPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class CleansingPotion extends PylonItem implements PylonSplashPotion {

    public CleansingPotion(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onSplash(@NotNull PotionSplashEvent event) {
        for(LivingEntity entity : event.getAffectedEntities()){
            if(entity.getType() == EntityType.ZOMBIE_VILLAGER){
                // Convert to regular villager
                ((ZombieVillager)entity).setConversionTime(0, true);
                entity.heal(requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue());
            }
        }
    }
}
