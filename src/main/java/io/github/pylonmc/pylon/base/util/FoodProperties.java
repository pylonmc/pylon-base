package io.github.pylonmc.pylon.base.util;

public class FoodProperties {
    public FoodProperties(int nutrition, float saturation, boolean canAlwaysEat){
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.canAlwaysEat = canAlwaysEat;
    }
    public int nutrition;
    public float saturation;
    public boolean canAlwaysEat;
    public io.papermc.paper.datacomponent.item.FoodProperties toBukkitFoodProperties(){
        return io.papermc.paper.datacomponent.item.FoodProperties.food().canAlwaysEat(this.canAlwaysEat).nutrition(this.nutrition).saturation(this.saturation).build();
    }
}
