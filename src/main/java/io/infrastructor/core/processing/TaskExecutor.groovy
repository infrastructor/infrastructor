package io.infrastructor.core.processing

import io.infrastructor.core.utils.FilteringUtils
import io.infrastructor.core.validation.ValidationException
import java.util.concurrent.atomic.AtomicInteger
import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.processing.actions.ActionContext
import io.infrastructor.core.processing.actions.ActionProcessingException

import static io.infrastructor.cli.logging.ConsoleLogger.*
import static io.infrastructor.cli.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.cli.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

class TaskExecutor {
    def nodes
    
    class Task {
        def name = 'unnamed'
        def tags = { true }
        def parallel = 1
        
        def execute(def nodes, Closure closure) {
            def filtered = tags ? nodes.findAll { FilteringUtils.match(it.listTags(), tags) } : nodes
            
            info "${blue(":task '${name}'")}"
            
            AtomicInteger errorCounter = new AtomicInteger()
            
            withTextStatus { statusLine -> 
                withProgressStatus(filtered.size(), 'nodes processed') { progressLine ->
                    executeParallel(filtered, parallel) { node -> 
                        try {
                            statusLine "> task: $name"
                            ProxyMetaClass.getInstance(ActionContext.class).use {
                                ActionContext.metaClass.'static'.node = { node }
                                def cloned = closure.clone()
                                cloned(node)
                            }
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
    
    def TaskExecutor(def nodes) {
        this.nodes = nodes
    }
    
    def nodes(Closure closure) {
        new Task().execute(nodes, closure)
    }
    
    def nodes(String tags, Closure closure) {
        nodes([tags: { tags as Boolean }], closure)
    }
    
    def nodes(Map params, Closure closure) {
        new Task(params).execute(nodes, closure)
    }
}
