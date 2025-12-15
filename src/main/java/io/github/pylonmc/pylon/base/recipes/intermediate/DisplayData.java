package io.github.pylonmc.pylon.base.recipes.intermediate;

import io.github.pylonmc.pylon.base.recipes.BlueprintWorkbenchRecipe;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.config.adapter.MapConfigAdapter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;

public record DisplayData(
    String name,
    Material material,
    double[] position,
    double[] scale,
    boolean mirrorX,
    boolean mirrorZ
) {
    public static final ConfigAdapter<DisplayData> ADAPTER = new ConfigAdapter<>() {
        @Override
        public @NotNull Type getType() {
            return DisplayData.class;
        }

        @Override
        public DisplayData convert(@NotNull Object value) {
            var map = MapConfigAdapter.STRING_TO_ANY.convert(value);

            String name = ConfigAdapter.STRING.convert(map.get("name"));
            if (name.contains("$"))
                throw new IllegalArgumentException("DisplayData's name shouldn't have '$' characters, as it is used internally");

            Material material = ConfigAdapter.MATERIAL.convert(map.get("material"));

            var doubleListAdapter = ConfigAdapter.LIST.from(ConfigAdapter.DOUBLE);
            List<Double> positionList = doubleListAdapter.convert(map.get("position"));
            List<Double> scaleList = doubleListAdapter.convert(map.get("scale"));

            if (positionList.size() != 2)
                throw new IllegalArgumentException("DisplayData's position must be a 2 double list");
            if (scaleList.size() != 2)
                throw new IllegalArgumentException("DisplayData's scale must be a 2 double list");

            double[] position = new double[]{positionList.get(0), positionList.get(1)};
            double[] scale = new double[]{scaleList.get(0), scaleList.get(1)};

            boolean mirrorX = ConfigAdapter.BOOLEAN.convert(map.getOrDefault("mirror_x", false));
            boolean mirrorZ = ConfigAdapter.BOOLEAN.convert(map.getOrDefault("mirror_z", false));

            return new DisplayData(
                name,
                material,
                position,
                scale,
                mirrorX,
                mirrorZ
            );
        }
    };
}
