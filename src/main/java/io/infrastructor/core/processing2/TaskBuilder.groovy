package io.infrastructor.core.processing2

import io.infrastructor.core.processing2.ExecutionContext
import io.infrastructor.core.processing2.actions.DebugActionBuilder
import io.infrastructor.core.processing2.actions.DirectoryActionBuilder
import io.infrastructor.core.processing2.actions.FetchActionBuilder
import io.infrastructor.core.processing2.actions.FileActionBuilder
import io.infrastructor.core.processing2.actions.FileUploadActionBuilder
import io.infrastructor.core.processing2.actions.GroupActionBuilder
import io.infrastructor.core.processing2.actions.InsertBlockActionBuilder
import io.infrastructor.core.processing2.actions.ReplaceActionBuilder
import io.infrastructor.core.processing2.actions.ReplaceLineActionBuilder
import io.infrastructor.core.processing2.actions.ShellActionBuilder
import io.infrastructor.core.processing2.actions.TemplateActionBuilder
import io.infrastructor.core.processing2.actions.UserActionBuilder
import io.infrastructor.core.processing2.actions.WaitForPortActionBuilder
import io.infrastructor.core.utils.FilteringUtils
import io.infrastructor.core.utils.ProgressPrinter

import static io.infrastructor.core.utils.ParallelUtils.executeParallel
import io.infrastructor.core.inventory.CommandExecutionException

class TaskBuilder {
    def nodes
    def logger
    
    class Task {
        def name = ''
        def tags = { true }
        def parallel = 1
        
        def execute(def nodes, def logger, Closure closure) {
            def filtered = filter(nodes, tags)
            
            ProgressPrinter printer = new ProgressPrinter()
            printer.setTotal(filtered.size())
            printer.setStatus("EXECUTING")
            
            executeParallel(filtered, parallel) { node -> 
                try {
                    def task = closure.clone()
                    task.resolveStrategy = Closure.DELEGATE_FIRST
                    task.delegate = context(node, logger)
                    printer.print(":task - $name")
                    task()
                } catch (CommandExecutionException ex) {
                    printer.print( "action '$name' failed on node '$node.id': $ex.result" )
                    return ex.result
                } finally {
                    printer.increase()
                }
            }
            
            println "Task execution is done"
        }
        
        private def context(def node, def logger) {
            def ctx = new ExecutionContext()
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
    
    def TaskBuilder(def nodes, def logger) {
        this.nodes = nodes
        this.logger = logger
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
