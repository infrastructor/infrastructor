package io.infrastructor.core.inventory

class InlineInventoryMixin {
    
    def static inlineInventory(Script script, Closure closure) {
        InlineInventory.inlineInventory(closure)
    }
}