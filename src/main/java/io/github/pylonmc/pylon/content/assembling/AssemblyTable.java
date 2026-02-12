package io.github.pylonmc.pylon.content.assembling;

import io.github.pylonmc.pylon.recipes.AssemblingRecipe;
import io.github.pylonmc.pylon.util.PylonUtils;
import io.github.pylonmc.rebar.block.RebarBlock;
import io.github.pylonmc.rebar.block.base.RebarGuiBlock;
import io.github.pylonmc.rebar.block.base.RebarInteractBlock;
import io.github.pylonmc.rebar.block.base.RebarVirtualInventoryBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.datatypes.RebarSerializers;
import io.github.pylonmc.rebar.recipe.RecipeInput;
import io.github.pylonmc.rebar.util.MachineUpdateReason;
import io.github.pylonmc.rebar.util.RebarUtils;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.Map;

public class AssemblyTable extends RebarBlock implements
        RebarInteractBlock,
        RebarVirtualInventoryBlock,
        RebarGuiBlock {

    private final NamespacedKey RECIPE_KEY = PylonUtils.pylonKey("recipe");
    private final NamespacedKey STEP_INDEX_KEY = PylonUtils.pylonKey("step_index");
    private final NamespacedKey REMAINING_CLICKS_KEY = PylonUtils.pylonKey("remaining_clicks");
    private final PersistentDataType<String, AssemblingRecipe> ASSEMBLING_RECIPE_TYPE = RebarSerializers.KEYED.keyedTypeFrom(
            AssemblingRecipe.class,
            AssemblingRecipe.RECIPE_TYPE::getRecipeOrThrow
    );

    private @Nullable AssemblingRecipe recipe;
    private int stepIndex;
    private int remainingClicks;
    private final VirtualInventory inputInventory = new VirtualInventory(7);
    private final VirtualInventory outputInventory = new VirtualInventory(5);

    @SuppressWarnings("unused")
    public AssemblyTable(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        recipe = null;
        stepIndex = -1;
        remainingClicks = -1;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public AssemblyTable(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        recipe = pdc.get(RECIPE_KEY, ASSEMBLING_RECIPE_TYPE);
        stepIndex = pdc.get(STEP_INDEX_KEY, PersistentDataType.INTEGER);
        remainingClicks = pdc.get(REMAINING_CLICKS_KEY, PersistentDataType.INTEGER);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        RebarUtils.setNullable(pdc, RECIPE_KEY, ASSEMBLING_RECIPE_TYPE, recipe);
        pdc.set(STEP_INDEX_KEY, PersistentDataType.INTEGER, stepIndex);
        pdc.set(REMAINING_CLICKS_KEY, PersistentDataType.INTEGER, remainingClicks);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.builder()
                .setStructure(
                        "# # # # # # # # #",
                        "I i i i i i i i I",
                        "# # # # # # # # #",
                        "# O o o o o o O #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('I', GuiItems.input())
                .addIngredient('i', inputInventory)
                .addIngredient('O', GuiItems.output())
                .addIngredient('o', outputInventory)
                .build();
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull VirtualInventory> getVirtualInventories() {
        return Map.of(
                "input", inputInventory,
                "output", outputInventory
        );
    }

    /**
     * Returns true if tool used
     */
    public boolean useTool(@NotNull String toolName, @Nullable Player player) {
        Bukkit.getLogger().severe("bruh " + stepIndex + " " + remainingClicks);
        if (recipe == null) {
            Bukkit.getLogger().severe("1");

            recipe = AssemblingRecipe.findRecipe(inputInventory.getItems());
            if (recipe == null) {
                Bukkit.getLogger().severe("4");
                return false;
            }

            Bukkit.getLogger().severe("5");
            stepIndex = recipe.steps().size() - 1;
            remainingClicks = recipe.steps().getLast().clicks();
            for (RecipeInput.Item item : recipe.inputs()) {
                for (ItemStack stack : inputInventory.getItems()) {
                    if (stack != null && item.matches(stack)) {
                        stack.subtract(item.getAmount());
                        break;
                    }
                }
            }
        }

        Bukkit.getLogger().severe("6");

        AssemblingRecipe.Step step = recipe.steps().get(stepIndex);
        if (!step.tool().equals(toolName)) {
            return false;
        }

        remainingClicks--;
        if (remainingClicks == 0) {
            // Step finished
            stepIndex--;
        }

        if (stepIndex != -1) {
            // Recipe not finished
            remainingClicks = recipe.steps().get(stepIndex).clicks();
            return true;
        }

        // Recipe finished
        if (!outputInventory.canHold(recipe.results())) {
            // Can't hold output
            remainingClicks++;
            if (recipe != null) {
                player.sendMessage(Component.translatable("pylon.message.assembly_table.full"));
            }
            return false;
        }

        // Can hold output
        remainingClicks = -1;
        stepIndex = -1;
        for (ItemStack stack : recipe.results()) {
            outputInventory.addItem(new MachineUpdateReason(), stack);
        }
        recipe = null;
        return true;
    }
}
