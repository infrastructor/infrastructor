package io.infrastructor.core.processing.provisioning

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.processing.NodeContext
import io.infrastructor.core.processing.NodeTaskExecutionException
import io.infrastructor.core.processing.TaskExecutionException
import io.infrastructor.core.processing.actions.ActionProcessingException
import io.infrastructor.core.utils.FilteringUtils
import io.infrastructor.core.validation.ValidationException
import java.util.concurrent.atomic.AtomicInteger

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

class Task {
    def name = 'unnamed task'
    def filter = { true }
    def parallel = 1
    def closure = {}
    
    def execute(def nodes) {
        def filtered = filter ? nodes.findAll { FilteringUtils.match(it.listTags(), filter) } : nodes
            
        info "${blue(":task '${name}'")}"
            
        AtomicInteger errorCounter = new AtomicInteger()
            
        withTextStatus { statusLine -> 
            withProgressStatus(filtered.size(), 'nodes processed') { progressLine ->
                executeParallel(filtered, parallel) { node -> 
                    try {
                        statusLine "> task: $name"
                        //
                        new NodeContext(node: node).with(closure.clone())
                        //
                    } catch (NodeTaskExecutionException ex) {
                        error "FAILED - node.id: $node.id, message: $ex.message, $ex.context"
                        errorCounter.incrementAndGet()
                    } catch(Exception ex) {
                        error "FAILED - node.id: $node.id, message: $ex.message, class: ${ex.class.name}"
                        errorCounter.incrementAndGet()
                    } finally {
                        progressLine.increase()
                        node.disconnect()
                    }
                }
            }
        }
            
        // determine if we can go to the next task or we should stop the execution
        if (errorCounter.get() > 0) {
            def message = ":task '$name' - failed on ${errorCounter.get()} node|s"
            info "${red(message)}"
            throw new TaskExecutionException(message)
        }
                    
        info "${blue(":task '$name' - done")}"
    }
}

