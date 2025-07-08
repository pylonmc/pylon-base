package io.github.pylonmc.pylon.base.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting.ConnectingService;
import io.github.pylonmc.pylon.base.content.machines.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.item.PylonItem;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class FluidPipeDisplay extends PylonEntity<ItemDisplay> {
    private static final NamespacedKey AMOUNT_KEY = baseKey("amount");
    private static final NamespacedKey PIPE_KEY = baseKey("pipe");
    private static final NamespacedKey FROM_KEY = baseKey("from");
    private static final NamespacedKey TO_KEY = baseKey("to");

    @Getter private final FluidPipe pipe;
    private final int amount;
    private final UUID from;
    private final UUID to;

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidPipeDisplay(@NotNull ItemDisplay entity) {
        super(entity);
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
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

    public FluidPipeDisplay(
            @NotNull FluidPipe pipe,
            int amount,
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to
    ) {
        super(BaseKeys.FLUID_PIPE_DISPLAY, makeDisplay(pipe, from, to));
        this.pipe = pipe;
        this.amount = amount;
        this.from = from.getUuid();
        this.to = to.getUuid();
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(PIPE_KEY, PylonSerializers.ITEM_STACK, pipe.getStack());
        pdc.set(AMOUNT_KEY, PylonSerializers.INTEGER, amount);
        pdc.set(FROM_KEY, PylonSerializers.UUID, from);
        pdc.set(TO_KEY, PylonSerializers.UUID, to);
    }

    private static @NotNull ItemDisplay makeDisplay(
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

        return new ItemDisplayBuilder()
                .transformation(new LineBuilder()
                        .from(fromOffset)
                        .to(toOffset)
                        .thickness(0.1)
                        .build()
                        .buildForItemDisplay()
                )
                .material(pipe.material)
                .build(centerLocation);
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
        FluidPipeDisplay display = new FluidPipeDisplay(pipe, amount, from, to);
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
