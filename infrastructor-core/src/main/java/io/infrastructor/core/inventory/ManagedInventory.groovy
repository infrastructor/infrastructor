package io.infrastructor.core.inventory

interface ManagedInventory extends Inventory {
    Inventory launch()
    void shutdown()
}
