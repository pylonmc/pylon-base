package io.github.pylonmc.pylon.content.machines.hydraulics;

public interface HydraulicRefuelable {
    double getHydraulicFluid();
    double getDirtyHydraulicFluid();
    void setHydraulicFluid(double amount);
    void setDirtyHydraulicFluid(double amount);
    double getHydraulicFluidCapacity();
    double getDirtyHydraulicFluidCapacity();

    default double getHydraulicFluidSpace() {
        return getHydraulicFluidCapacity() - getHydraulicFluid();
    }

    default double getDirtyHydraulicFluidSpace() {
        return getDirtyHydraulicFluidCapacity() - getDirtyHydraulicFluid();
    }
}