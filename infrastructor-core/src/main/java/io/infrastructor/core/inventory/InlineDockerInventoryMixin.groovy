package io.infrastructor.core.inventory

class InlineDockerInventoryMixin {
    def static inlineDockerInventory(Script script, Closure closure) {
        InlineDockerInventory.inlineDockerInventory(closure)
    }
}

