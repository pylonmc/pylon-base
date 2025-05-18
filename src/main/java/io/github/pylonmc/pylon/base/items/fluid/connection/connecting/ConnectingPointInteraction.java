package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;


public record ConnectingPointInteraction(@NotNull FluidConnectionInteraction interaction) implements ConnectingPoint {

    public ConnectingPointInteraction(@NotNull FluidConnectionInteraction interaction) {
        this.interaction = interaction;
    }

    @Override
    public @NotNull BlockPosition position() {
        return interaction.getPoint().getPosition();
    }

    @Override
    public @NotNull Vector3f offset() {
        if (interaction.getFace() != null && interaction.getRadius() != null) {
            return interaction.getFace().getDirection().toVector3f().mul(interaction.getRadius());
        }
        return new Vector3f(0, 0, 0);
    }

    @Override
    public @Nullable BlockFace allowedFace() {
        return interaction.getFace();
    }

    @Override
    public boolean isStillValid() {
        return interaction.getEntity().isValid();
    }

    @Override
    public @NotNull FluidConnectionInteraction create() {
        return interaction;
    }

    @Override
    public @NotNull Set<UUID> getConnectedInteractions() {
        return Set.of(interaction.getUuid());
    }
}
