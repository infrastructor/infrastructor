package io.infrastructor.core.processing

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.processing.ActionProcessingException
import io.infrastructor.core.actions.DirectoryActionBuilder
import io.infrastructor.core.actions.FetchActionBuilder
import io.infrastructor.core.actions.FileActionBuilder
import io.infrastructor.core.actions.FileUploadActionBuilder
import io.infrastructor.core.actions.GroupActionBuilder
import io.infrastructor.core.actions.GroupActionBuilder
import io.infrastructor.core.actions.InsertBlockActionBuilder
import io.infrastructor.core.actions.ReplaceActionBuilder
import io.infrastructor.core.actions.ReplaceLineActionBuilder
import io.infrastructor.core.actions.ShellActionBuilder
import io.infrastructor.core.actions.TemplateActionBuilder
import io.infrastructor.core.actions.UserActionBuilder
import io.infrastructor.core.actions.WaitForPortActionBuilder

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.cli.ConsoleLogger.info
import static io.infrastructor.cli.ConsoleLogger.error
import static io.infrastructor.core.validation.ValidationHelper.validate
import static io.infrastructor.core.actions.InputAction.*


public class ActionProcessor {
    
    def node
    def builders = [:]
    
    public ActionProcessor(def node) {
        this.node = node
        builders['directory']   = new DirectoryActionBuilder()
        builders['group']       = new GroupActionBuilder()
        builders['fetch']       = new FetchActionBuilder()
        builders['file']        = new FileActionBuilder()
        builders['insertBlock'] = new InsertBlockActionBuilder()
        builders['shell']       = new ShellActionBuilder()
        builders['replace']     = new ReplaceActionBuilder()
        builders['replaceLine'] = new ReplaceLineActionBuilder()
        builders['template']    = new TemplateActionBuilder()
        builders['upload']      = new FileUploadActionBuilder()
        builders['user']        = new UserActionBuilder()
        builders['waitForPort'] = new WaitForPortActionBuilder()
    }
    
    def static setup(def node, Closure closure) {
        closure.delegate = new ActionProcessor(node)
        closure()
    }
    
    def methodMissing(String name, Object args) {
        def builder = builders[name]
        if (builder != null) {
            debug "running action '$name' on node '$node.id'"
            def action = builder."$name"(*args)
            validate(action)
            
            try {
                action.execute(node)
            } catch (CommandExecutionException ex) {
                error "action '$name' failed on node '$node.id': $ex.result"
                return ex.result
            }
            
            debug "action $name executed succesfully on node '$node.id': result: $node.lastResult"
            return node.lastResult
        } else {
            error "unknown action '$name'"
            throw new ActionProcessingException("Unknown action: $name")
        }
    }
}
