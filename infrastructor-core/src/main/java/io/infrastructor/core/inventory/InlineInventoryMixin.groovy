package io.infrastructor.core.inventory

public class InlineInventoryMixin {
    
    def static inlineInventory(Script script, Closure closure) {
        InlineInventory.inlineInventory(closure)
    }
}