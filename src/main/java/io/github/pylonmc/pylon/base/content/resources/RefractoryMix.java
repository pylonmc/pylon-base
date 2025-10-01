package io.github.pylonmc.pylon.base.content.resources;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.tools.Moldable;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class RefractoryMix extends PylonBlock implements Moldable {

    private static final NamespacedKey MOLDING_CLICKS_KEY = baseKey("molding-clicks");

    public static final int TOTAL_MOLDING_CLICKS = Settings.get(BaseKeys.REFRACTORY_MIX).getOrThrow("total-molding-clicks", ConfigAdapter.INT);

    private int moldingClicksRemaining;


    @SuppressWarnings("unused")
    public RefractoryMix(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        moldingClicksRemaining = TOTAL_MOLDING_CLICKS;
    }

    @SuppressWarnings("unused")
    public RefractoryMix(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        moldingClicksRemaining = pdc.get(MOLDING_CLICKS_KEY, PylonSerializers.INTEGER);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(MOLDING_CLICKS_KEY, PylonSerializers.INTEGER, moldingClicksRemaining);
    }

    @Override
    public void doMoldingClick() {
        moldingClicksRemaining--;
    }

    @Override
    public boolean isMoldingFinished() {
        return moldingClicksRemaining == 0;
    }

    @Override
    public ItemStack moldingResult() {
        return BaseItems.UNFIRED_REFRACTORY_BRICK;
    }

    @Override
    public @Nullable WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of(
                        "percent",
                        UnitFormat.PERCENT.format(100 * (TOTAL_MOLDING_CLICKS - moldingClicksRemaining) / TOTAL_MOLDING_CLICKS)
                )
        ));
    }
}
