package io.github.pylonmc.pylon.base.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting.ConnectingService;
import io.github.pylonmc.pylon.base.items.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.display.PylonItemDisplay;
import io.github.pylonmc.pylon.core.entity.display.builder.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.builder.transform.LineBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidPipeDisplay extends PylonItemDisplay {

    public static final NamespacedKey KEY = pylonKey("fluid_pipe_display");

    private static final NamespacedKey AMOUNT_KEY = pylonKey("amount");
    private static final NamespacedKey PIPE_KEY = pylonKey("pipe");
    private static final NamespacedKey FROM_KEY = pylonKey("from");
    private static final NamespacedKey TO_KEY = pylonKey("to");

    @Getter private FluidPipe pipe;
    private int amount;
    private UUID from;
    private UUID to;

    @SuppressWarnings("unused")
    public FluidPipeDisplay(@NotNull WrapperEntity entity, @NotNull NamespacedKey key, @NotNull Location location) {
        super(entity, key, location);
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidPipeDisplay(@NotNull WrapperEntity entity, @NotNull PersistentDataContainer pdc) {
        super(entity, pdc);

        // will fail to load if schema not found; no way around this
        pipe = (FluidPipe) PylonItem.fromStack(pdc.get(PIPE_KEY, PylonSerializers.ITEM_STACK));
        this.amount = pdc.get(AMOUNT_KEY, PylonSerializers.INTEGER);
        from = pdc.get(FROM_KEY, PylonSerializers.UUID);
        to = pdc.get(TO_KEY, PylonSerializers.UUID);

        // When fluid points are loaded back, their segment's fluid per second and predicate won't be preserved, so
        // we wait for them to load and then set their segments' fluid per second and predicate
        EntityStorage.whenEntityLoads(from, FluidConnectionInteraction.class, interaction -> {
            FluidManager.setFluidPerSecond(interaction.getPoint().getSegment(), pipe.fluidPerSecond);
            FluidManager.setFluidPredicate(interaction.getPoint().getSegment(), pipe.getPredicate());
        });

        // Technically only need to do this for one of the end points since they're part of the same segment, but
        // we do it twice just to be safe
        EntityStorage.whenEntityLoads(to, FluidConnectionInteraction.class, interaction -> {
            FluidManager.setFluidPerSecond(interaction.getPoint().getSegment(), pipe.fluidPerSecond);
            FluidManager.setFluidPredicate(interaction.getPoint().getSegment(), pipe.getPredicate());
        });
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        pdc.set(PIPE_KEY, PylonSerializers.ITEM_STACK, pipe.getStack());
        pdc.set(AMOUNT_KEY, PylonSerializers.INTEGER, amount);
        pdc.set(FROM_KEY, PylonSerializers.UUID, from);
        pdc.set(TO_KEY, PylonSerializers.UUID, to);
    }

    private static @NotNull FluidPipeDisplay makeDisplay(
            @NotNull FluidPipe pipe,
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to
    ) {
        float height = from.getEntity().getInteractionHeight();
        Location fromLocation = from.getEntity().getLocation().add(0, height / 2, 0);
        Location toLocation = to.getEntity().getLocation().add(0, height / 2, 0);
        // We use a center location rather than just spawning at fromLocation or toLocation to prevent the entity
        // from being spawned just inside a block - this causes it to render as black due to being inside the block
        Location centerLocation = fromLocation.clone().add(toLocation).multiply(0.5);
        Vector3f fromOffset = centerLocation.clone().subtract(fromLocation).toVector().toVector3f();
        Vector3f toOffset = centerLocation.clone().subtract(toLocation).toVector().toVector3f();

        return (FluidPipeDisplay) new ItemDisplayBuilder()
                .transformation(new LineBuilder()
                        .from(fromOffset)
                        .to(toOffset)
                        .thickness(0.1)
                        .build()
                        .buildForItemDisplay()
                )
                .material(pipe.material)
                .buildPacketBased(KEY, centerLocation);
    }

    /**
     * Convenience function that constructs the display, but then also adds it to EntityStorage
     */
    public static @NotNull FluidPipeDisplay make(
            @NotNull FluidPipe pipe,
            int amount,
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to
    ) {
        FluidPipeDisplay display = makeDisplay(pipe, from, to);
        display.pipe = pipe;
        display.amount = amount;
        display.from = from.getUuid();
        display.to = to.getUuid();
        EntityStorage.add(display);
        return display;
    }

    public @Nullable FluidConnectionInteraction getFrom() {
        return EntityStorage.getAs(FluidConnectionInteraction.class, from);
    }

    public @Nullable FluidConnectionInteraction getTo() {
        return EntityStorage.getAs(FluidConnectionInteraction.class, to);
    }

    public void delete(boolean removeMarkersIfEmpty, @Nullable Player player) {
        FluidConnectionInteraction from = getFrom();
        FluidConnectionInteraction to = getTo();
        Preconditions.checkState(from != null);
        Preconditions.checkState(to != null);

        ItemStack itemToGive = pipe.getStack().clone();
        itemToGive.setAmount(amount);
        if (player != null) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.give(itemToGive);
            }
        } else {
            Location location = to.getPoint().getPosition().plus(from.getPoint().getPosition()).getLocation().multiply(0.5);
            location.getWorld().dropItemNaturally(location, itemToGive);
        }

        ConnectingService.disconnect(from, to, removeMarkersIfEmpty);
    }
}
