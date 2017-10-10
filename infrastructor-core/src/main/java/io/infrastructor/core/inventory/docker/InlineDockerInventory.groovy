package io.infrastructor.core.inventory.docker

import io.infrastructor.core.inventory.Inventory

import static io.infrastructor.core.validation.ValidationHelper.validate
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus

class InlineDockerInventory {
    
    def nodes = []
    
    def static inlineDockerInventory(Closure closure) {
        def inlineDockerInventory = new InlineDockerInventory()
        inlineDockerInventory.with(closure)
        return inlineDockerInventory
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
    
    def provision(Closure setupClosure) {
        new Inventory(nodes: launch()).provision(setupClosure)
        this
    }
    
    def launch() {
        def inventoryNodes = []
        
        withTextStatus("> launching docker nodes") { 
            withProgressStatus(nodes.size(), 'nodes launched')  { progressLine ->
                inventoryNodes = nodes.collect { 
                    def node = it.launch() 
                    progressLine.increase()
                    node
                }
            }
        }
        
        return inventoryNodes
    }
   
    def shutdown() {
        withTextStatus("> shutting down docker nodes") {
            withProgressStatus(nodes.size(), 'nodes terminated')  { progressLine ->
                nodes.each { 
                    it.shutdown()
                    progressLine.increase()
                }
            }
        }
    }
}

