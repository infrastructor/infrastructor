package io.infrastructor.core.processing.provisioning

import io.infrastructor.core.processing.actions.ActionExecutionException
import io.infrastructor.core.processing.actions.NodeContext
import io.infrastructor.core.validation.ValidationException
import io.infrastructor.core.utils.FilteringUtils
import java.util.concurrent.atomic.AtomicInteger

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.logging.status.TaskProgressLogger.withTaskProgressStatus
import static io.infrastructor.core.processing.ProvisioningContext.provision
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
            
        info "${blue("> task: '${name}'")}"
            
        def failedNodes = [].asSynchronized() 
     
        withTaskProgressStatus(name) { status -> 
            filtered.each { status.updateStatus(it.id, 'waiting') }
            executeParallel(filtered, parallel) { node -> 
                try {
                    status.updateStatus(node.id, "${blue("running")}")
                    new NodeContext(node: node).with(actions.clone())
                    status.updateStatus(node.id, "${green("done")}")
                } catch (ActionExecutionException ex) {
                    error "FAILED - node.id: $node.id, $ex.message"
                    failedNodes << node
                    status.updateStatus(node.id, "${red("falied")}")
                } catch(Exception ex) {
                    error "FAILED - node.id: $node.id, message: $ex.message"
                    failedNodes << node
                    status.updateStatus(node.id, "${red("falied")}")
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
                    
        info "${blue("> task: '$name', done on ${filtered.size()} node|s")}"
    }
}

