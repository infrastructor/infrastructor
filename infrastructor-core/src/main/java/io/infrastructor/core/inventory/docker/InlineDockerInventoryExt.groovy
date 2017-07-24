package io.infrastructor.core.inventory.docker

class InlineDockerInventoryExt {
    def static inlineDockerInventory(Script script, Closure closure) {
        InlineDockerInventory.inlineDockerInventory(closure)
    }
}

