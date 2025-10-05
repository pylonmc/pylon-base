package io.github.pylonmc.pylon.base.content.resources;

import io.github.pylonmc.pylon.base.content.tools.Moldable;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class RefractoryMix extends PylonBlock implements Moldable {

    private static final NamespacedKey MOLDING_CLICKS_KEY = baseKey("molding-clicks");

    private int moldingClicksRemaining;


    @SuppressWarnings("unused")
    public RefractoryMix(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        moldingClicksRemaining = totalMoldingClicks();
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
    public @Nullable WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of(
                        "percent",
                        UnitFormat.PERCENT.format(100 * (totalMoldingClicks() - moldingClicksRemaining) / totalMoldingClicks())
                )
        ));
    }
}
