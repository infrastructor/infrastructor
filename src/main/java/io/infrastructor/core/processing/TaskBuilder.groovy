package io.infrastructor.core.processing

import io.infrastructor.core.processing.actions.LogActionBuilder
import io.infrastructor.core.processing.actions.DirectoryActionBuilder
import io.infrastructor.core.processing.actions.FetchActionBuilder
import io.infrastructor.core.processing.actions.FileActionBuilder
import io.infrastructor.core.processing.actions.FileUploadActionBuilder
import io.infrastructor.core.processing.actions.GroupActionBuilder
import io.infrastructor.core.processing.actions.InsertBlockActionBuilder
import io.infrastructor.core.processing.actions.ReplaceActionBuilder
import io.infrastructor.core.processing.actions.ReplaceLineActionBuilder
import io.infrastructor.core.processing.actions.ShellActionBuilder
import io.infrastructor.core.processing.actions.TemplateActionBuilder
import io.infrastructor.core.processing.actions.UserActionBuilder
import io.infrastructor.core.processing.actions.WaitForPortActionBuilder
import io.infrastructor.core.utils.FilteringUtils

import static io.infrastructor.cli.logging.ProgressLogger.*
import static io.infrastructor.cli.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.cli.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

class TaskBuilder {
    def nodes
    
    class Task {
        def name = 'unnamed'
        def tags = { true }
        def parallel = 1
        
        def execute(def nodes, Closure closure) {
            def filtered = filter(nodes, tags)
            
            info "\n${blue(':task ' + name)}"
            
            withTextStatus { statusLine -> 
                withProgressStatus(filtered.size(), 'nodes processed') { progressLine ->
                    executeParallel(filtered, parallel) { node -> 
                        try {
                            statusLine "> task: $name"
                            initializeAndRun(closure, node)
                        } catch (TaskExecutionException ex) {
                            error "FAILED: $ex.message, $ex.context"
                            throw ex
                        } finally {
                            progressLine.increase()
                        }
                    }
                }
                
                info "${blue(':task ' + name + " - done")}"
            }
        }
        
        
        private def initializeAndRun(def closure, def node) {
            def cloned = closure.clone()
            def ctx = new TaskExecutionContext(node, cloned.owner)
            ctx.functions << ['directory':   new DirectoryActionBuilder(node: node)]
            ctx.functions << ['fetch':       new FetchActionBuilder(node: node)]
            ctx.functions << ['file':        new FileActionBuilder(node: node)]
            ctx.functions << ['upload':      new FileUploadActionBuilder(node: node)]
            ctx.functions << ['group':       new GroupActionBuilder(node: node)]
            ctx.functions << ['insertBlock': new InsertBlockActionBuilder(node: node)]
            ctx.functions << ['replace':     new ReplaceActionBuilder(node: node)]
            ctx.functions << ['replaceLine': new ReplaceLineActionBuilder(node: node)]
            ctx.functions << ['shell':       new ShellActionBuilder(node: node)]
            ctx.functions << ['template':    new TemplateActionBuilder(node: node)]
            ctx.functions << ['user':        new UserActionBuilder(node: node)]
            ctx.functions << ['waitForPort': new WaitForPortActionBuilder(node: node)]
            cloned.delegate = ctx
            cloned()
        }
        
        private def filter(def nodes, def tags) {
            tags ? nodes.findAll { FilteringUtils.match(it.listTags(), tags) } : nodes
        }
    }
    
    def TaskBuilder(def nodes) {
        this.nodes = nodes
    }
    
    def nodes(Map params, Closure closure) {
        new Task(params).execute(nodes, closure)
    }
    
    def nodes(Closure closure) {
        new Task().execute(nodes, closure)
    }
    
    def nodes(String tags, Closure closure) {
        nodes([tags: { tags as Boolean }], closure)
    }
}
