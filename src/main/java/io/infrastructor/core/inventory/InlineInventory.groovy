package io.infrastructor.core.inventory

public class InlineInventory {
    
    def inventory = new Inventory()
    
    def node(Map params) {
        node(params, {})
    }
    
    def node(Closure closure) {
        node([:], closure)
    }
    
    def node(Map params, Closure closure) {
        def node = new Node(params)
        node.with(closure)
        inventory << node
    }
    
    public static Inventory inlineInventory(Closure closure) {
        def inlineInventory = new InlineInventory()
        inlineInventory.with(closure)
        inlineInventory.inventory
    }
}