package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.BlueprintWorkbenchRecipe;
import io.github.pylonmc.pylon.base.recipes.intermediate.Step;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public abstract class ProceduralCraftingTable extends PylonBlock implements PylonEntityHolderBlock {
    public static final NamespacedKey CURRENT_RECIPE_KEY = baseKey("blueprint_workbench_current_recipe");
    public static final NamespacedKey CURRENT_PROGRESS_KEY = baseKey("blueprint_workbench_current_step");

    protected BlueprintWorkbenchRecipe currentRecipe;
    protected Step.ActionStep currentProgress;

    public ProceduralCraftingTable(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        this.currentRecipe = null;
        this.currentProgress = null;
    }

    public ProceduralCraftingTable(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        NamespacedKey currentRecipeKey = pdc.get(CURRENT_RECIPE_KEY, PylonSerializers.NAMESPACED_KEY);
        this.currentRecipe = currentRecipeKey == null ? null : BlueprintWorkbenchRecipe.RECIPE_TYPE.getRecipe(currentRecipeKey);

        Long currentStepLong = pdc.get(CURRENT_PROGRESS_KEY, PylonSerializers.LONG);
        this.currentProgress = currentStepLong == null ? null : new Step.ActionStep(currentStepLong);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, CURRENT_RECIPE_KEY, PylonSerializers.NAMESPACED_KEY, currentRecipe == null ? null : currentRecipe.getKey());
        PylonUtils.setNullable(pdc, CURRENT_PROGRESS_KEY, PylonSerializers.LONG, currentProgress == null ? null : currentProgress.toLong());
    }

    protected static void clearInventory(VirtualInventory inventory) {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(UpdateReason.SUPPRESSED, i, null);
        }
    }

    public void removeAddedEntities() {
        ArrayList<UUID> entities = new ArrayList<>(getHeldEntities().size());
        for (var entry : this.getHeldEntities().entrySet()) {
            if (Step.StateDisplay.ENTITY_IDENTIFIERS.contains(entry.getKey())) continue;
            entities.add(entry.getValue());
        }

        for (UUID entityId : entities) {
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null && entity.isValid()) entity.remove();
        }
    }


    protected void updateRecipeEntities() {
        if (currentRecipe == null || currentProgress == null) {
            removeAddedEntities();
            return;
        }

        Step current = this.getStep();
        current.removeDisplays().forEach(this::removeEntity);

        Location up = getBlock().getRelative(BlockFace.UP).getLocation()
            .toCenterLocation()
            .add(0, -0.495, 0);
        current.addDisplays().forEach(data -> {
            String name = data.name();
            double[] positions = data.position();
            double[] scale = data.scale();

            addEntityIfMissing(name, () -> new BlockDisplayBuilder()
                .material(data.material())
                .transformation(new TransformBuilder()
                    .translate(positions[0], 0, positions[1])
                    .scale(scale[0], 0, scale[1])
                )
                .build(up)
            );

            if (data.mirrorX()) {
                addEntityIfMissing(name + "$mirror_x", () -> new BlockDisplayBuilder()
                    .material(data.material())
                    .transformation(new TransformBuilder()
                        .translate(-positions[0], 0, positions[1])
                        .scale(scale[0], 0, scale[1])
                    )
                    .build(up)
                );
            }

            if (data.mirrorZ()) {
                addEntityIfMissing(name + "$mirror_z", () -> new BlockDisplayBuilder()
                    .material(data.material())
                    .transformation(new TransformBuilder()
                        .translate(positions[0], 0, -positions[1])
                        .scale(scale[0], 0, scale[1])
                    )
                    .build(up)
                );
            }
        });
    }

    protected void addEntityIfMissing(String name, Supplier<Entity> entity) {
        if (getHeldEntityUuid(name) != null) return;
        addEntity(name, entity.get());
    }

    public Step getStep() {
        return currentRecipe.steps().get(currentProgress.getStep());
    }
}
