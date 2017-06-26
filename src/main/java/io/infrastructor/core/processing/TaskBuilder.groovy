package io.infrastructor.core.processing

import io.infrastructor.core.processing.actions.DebugActionBuilder
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
import io.infrastructor.core.utils.ProgressLogger

import static io.infrastructor.core.utils.ParallelUtils.executeParallel
import static org.fusesource.jansi.Ansi.Color.GREEN
import static org.fusesource.jansi.Ansi.Color.RED

class TaskBuilder {
    def nodes
    def logger = new ProgressLogger()
    
    class Task {
        def name = 'unnamed'
        def tags = { true }
        def parallel = 1
        
        def execute(def nodes, def logger, Closure closure) {
            def filtered = filter(nodes, tags)
            
            logger.setTotal(filtered.size())
            logger.status("EXECUTING")
            logger.info(":task - $name")
            
            executeParallel(filtered, parallel) { node -> 
                try {
                    def task = closure.clone()
                    task.resolveStrategy = Closure.DELEGATE_FIRST
                    task.delegate = context(node, logger)
                    task()
                } catch (TaskExecutionException ex) {
                    logger.error("FAILED: $ex.message, task: $name, node: $node.id, action: $ex.action, result: $ex.result")
                    throw ex
                } finally {
                    logger.increase()
                }
            }
            
            logger.info(":task - $name done.")
            logger.finish("Done.")
        }
        
        private def context(def node, def logger) {
            def ctx = new TaskExecutionContext()
            ctx.handlers << ['debug':       new DebugActionBuilder(logger)]
            ctx.handlers << ['directory':   new DirectoryActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['fetch':       new FetchActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['file':        new FileActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['upload':      new FileUploadActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['group':       new GroupActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['insertBlock': new InsertBlockActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['replace':     new ReplaceActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['replaceLine': new ReplaceLineActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['shell':       new ShellActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['template':    new TemplateActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['user':        new UserActionBuilder(node: node, logger: logger)]
            ctx.handlers << ['waitForPort': new WaitForPortActionBuilder(node: node, logger: logger)]
            ctx
        }
        
        private def filter(def nodes, def tags) {
            tags ? nodes.findAll { FilteringUtils.match(it.listTags(), tags) } : nodes
        }
    }
    
    def TaskBuilder(def nodes) {
        this.nodes = nodes
    }
    
    def nodes(Map params, Closure closure) {
        new Task(params).execute(nodes, logger, closure)
    }
    
    def nodes(Closure closure) {
        new Task().execute(nodes, logger, closure)
    }
    
    def nodes(String tags, Closure closure) {
        nodes([tags: { tags as Boolean }], closure)
    }
}
