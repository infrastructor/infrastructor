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
import io.infrastructor.core.utils.ProgressLogger

import io.infrastructor.cli.ConsoleLogger

import static io.infrastructor.core.utils.ParallelUtils.executeParallel
import static org.fusesource.jansi.Ansi.Color.GREEN
import static org.fusesource.jansi.Ansi.Color.BLUE
import static org.fusesource.jansi.Ansi.Color.RED

class TaskBuilder {
    def nodes
    
    class Task {
        def name = 'unnamed'
        def tags = { true }
        def parallel = 1
        def logger = new ProgressLogger()
        
        def execute(def nodes, Closure closure) {
            def filtered = filter(nodes, tags)
            
            logger.setTotal(filtered.size())
            logger.status("EXECUTING")
            logger.print(":task - $name", BLUE)
            
            executeParallel(filtered, parallel) { node -> 
                try {
                    def task = closure.clone()
                    task.resolveStrategy = Closure.DELEGATE_FIRST
                    task.delegate = context(node, logger)
                    executeWithLogger(logger) {
                        task()
                    }
                } catch (TaskExecutionException ex) {
                    logger.error("FAILED: $ex.message, $ex.context")
                    throw ex
                } finally {
                    logger.increase()
                }
            }
            
            logger.print(":task - $name done.", BLUE)
            logger.finish("done.")
        }
        
        private def context(def node, def logger) {
            def ctx = new TaskExecutionContext(node)
            ctx.functions << ['debug':       new LogActionBuilder(logger)]
            ctx.functions << ['info':        new LogActionBuilder(logger)]
            ctx.functions << ['directory':   new DirectoryActionBuilder(node: node, logger: logger)]
            ctx.functions << ['fetch':       new FetchActionBuilder(node: node, logger: logger)]
            ctx.functions << ['file':        new FileActionBuilder(node: node, logger: logger)]
            ctx.functions << ['upload':      new FileUploadActionBuilder(node: node, logger: logger)]
            ctx.functions << ['group':       new GroupActionBuilder(node: node, logger: logger)]
            ctx.functions << ['insertBlock': new InsertBlockActionBuilder(node: node, logger: logger)]
            ctx.functions << ['replace':     new ReplaceActionBuilder(node: node, logger: logger)]
            ctx.functions << ['replaceLine': new ReplaceLineActionBuilder(node: node, logger: logger)]
            ctx.functions << ['shell':       new ShellActionBuilder(node: node, logger: logger)]
            ctx.functions << ['template':    new TemplateActionBuilder(node: node, logger: logger)]
            ctx.functions << ['user':        new UserActionBuilder(node: node, logger: logger)]
            ctx.functions << ['waitForPort': new WaitForPortActionBuilder(node: node, logger: logger)]
            ctx
        }
        
        private def filter(def nodes, def tags) {
            tags ? nodes.findAll { FilteringUtils.match(it.listTags(), tags) } : nodes
        }
        
        
        private def executeWithLogger(def logger, Closure closure) {
            def proxy = ProxyMetaClass.getInstance(ConsoleLogger.class)
            proxy.use {
                ConsoleLogger.metaClass.static.info  = logger.&info
                ConsoleLogger.metaClass.static.error = logger.&error
                ConsoleLogger.metaClass.static.debug = logger.&debug
                closure()
            }
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
