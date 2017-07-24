package io.infrastructor.core.inventory

public class InlineInventoryExt {
    def static inlineInventory(Script script, Closure closure) {
        InlineInventory.inlineInventory(closure)
    }
}