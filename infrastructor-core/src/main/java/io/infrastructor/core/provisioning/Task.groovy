package io.infrastructor.core.provisioning

import io.infrastructor.core.provisioning.actions.ActionExecutionException
import io.infrastructor.core.provisioning.actions.NodeContext
import io.infrastructor.core.utils.FilteringUtils

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TaskProgressLogger.withTaskProgressStatus
import static io.infrastructor.core.provisioning.ProvisioningContext.provision
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

class Task {
    
    def name = 'unnamed task'
    def filter = { true }
    def parallel = 1
    def actions = {}
    def onSuccess = {}
    def onFailure = { throw new TaskExecutionException(":task '$name' - failed on ${context.failed.size()} node|s") }
    
    def execute(def nodes) {
        def filtered = filter ? nodes.findAll { FilteringUtils.match(it.listTags(), filter) } : nodes
            
        info "${green("task: '${name}'")}"
            
        def failedNodes = [].asSynchronized() 
     
        withTaskProgressStatus(name) { status -> 
            
            filtered.each { status.updateStatus(it.getLogName(), 'waiting') }
            
            debug "running task: $name with parallelism: $parallel on nodes: ${filtered.collect { it.getLogName() }}"
            
            executeParallel(filtered, parallel) { node -> 
                try {
                    status.updateStatus(node.getLogName(), "${blue("running")}")
                    
                    def clonned = actions.clone()
                    clonned.delegate = new NodeContext(node: node)
                    clonned(node)

                    status.updateStatus(node.getLogName(), "${green("done")}")
                } catch (ActionExecutionException ex) {
                    error "FAILED - node.id: ${node.getLogName()}, $ex.message"
                    failedNodes << node
                    status.updateStatus(node.getLogName(), "${red("failed")}")
                } catch (Exception ex) {
                    error "FAILED - node.id: ${node.getLogName()}, message: $ex.message"
                    failedNodes << node
                    status.updateStatus(node.getLogName(), "${red("failed")}")
                } finally {
                    status.increase()
                    node.disconnect()
                }
            }
        }
        
        // determine if we can go to the next task or we should stop the execution
        if (failedNodes.size() > 0) {
            provision(nodes, [failed: failedNodes], onFailure)
        } else {
            provision(nodes, onSuccess)
        }
                    
        info "${green("task: '$name', done on ${filtered.size()} node|s")}"
    }
}

