package io.infrastructor.core.provisioning

import io.infrastructor.core.logging.status.LightweightTaskProgressLogger
import io.infrastructor.core.provisioning.actions.ActionExecutionException
import io.infrastructor.core.provisioning.actions.NodeContext

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.LightweightTaskProgressLogger.withLightweightTaskProgressLogger
import static io.infrastructor.core.provisioning.ProvisioningContext.provision
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

class Task {
    
    def name = 'unnamed task'
    def filter = { true }
    def parallel = 1
    def actions = {}
    def onSuccess = {}
    def onFailure = { throw new TaskExecutionException(":task '$name' - failed on ${context.failed.size()} node|s") }
    
    def execute(def inventory) {

        def filtered = inventory.filter(filter)

        info "${green("task: '${name}'")}"
            
        def failedNodes = [].asSynchronized()

        withLightweightTaskProgressLogger(name, filtered.size()) { LightweightTaskProgressLogger status ->
            debug "running task: $name with parallelism: $parallel on nodes: ${filtered.collect { it.getLogName() }}"
            
            executeParallel(filtered, parallel) { node -> 
                try {
                    status.run()

                    def clonned = actions.clone()
                    clonned.delegate = new NodeContext(node: node)
                    clonned(node)

                    status.done()
                } catch (ActionExecutionException ex) {
                    error "FAILED - node.id: ${node.getLogName()}, $ex.message"
                    failedNodes << node
                    status.fail()
                } catch (Exception ex) {
                    error "FAILED - node.id: ${node.getLogName()}, message: $ex.message"
                    failedNodes << node
                    status.fail()
                } finally {
                    node.disconnect()
                }
            }
        }
        
        // determine if we can go to the next task or we should stop the execution
        if (failedNodes.size() > 0) {
            provision(inventory, [failed: failedNodes], onFailure)
        } else {
            provision(inventory, onSuccess)
        }
                    
        info "${green("task: '$name', done on ${filtered.size()} node|s")}"
    }
}