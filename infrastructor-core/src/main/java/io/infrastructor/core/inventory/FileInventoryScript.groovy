package io.infrastructor.core.inventory

abstract class FileInventoryScript extends Script {
    def node(Map params) {
        node(params, {})
    }
    
    def node(Closure setup) {
        node([:], setup)
    }
        
    def node(Map params, Closure setup) {
        def node = new Node(params)
        node.with(setup)
        this.binding.inventory << node
    }
}

