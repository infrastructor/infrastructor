package io.infrastructor.core.inventory.docker

import static io.infrastructor.core.validation.ValidationHelper.validate
import io.infrastructor.core.inventory.Inventory

public class InlineDockerInventory {
    
    def nodes = []
    
    public static InlineDockerInventory inlineDockerInventory(Closure closure) {
        def inventory = new InlineDockerInventory()
        inventory.with(closure)
        return inventory
    }
    
    def node(Map params, Closure closure) {
        def dockerNode = new DockerNode(params)
        dockerNode.with(closure)
        nodes << validate(dockerNode)
    }
    
    def node(Map params) {
        node(params, {})
    }
    
    def node(Closure closure) {
        node([:], closure)
    }
    
    def setup(Closure setupClosure) {
        def inventoryNodes = nodes.collect { it.launch() }
        new Inventory(nodes: inventoryNodes).setup(setupClosure)
    }
    
    def shutdown() {
        nodes.each { it.shutdown() }
    }
}

