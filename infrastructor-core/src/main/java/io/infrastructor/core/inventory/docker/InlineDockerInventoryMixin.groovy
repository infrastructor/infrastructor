package io.infrastructor.core.inventory.docker

class InlineDockerInventoryMixin {
    def static inlineDockerInventory(Script script, Closure closure) {
        InlineDockerInventory.inlineDockerInventory(closure)
    }
}

