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

import static io.infrastructor.core.validation.ValidationHelper.validate
import static io.infrastructor.core.actions.InputAction.*
import static org.fusesource.jansi.Ansi.Color.YELLOW

public class ActionProcessor {
    
    def node
    def builders = [:]
    def printer
    
    public ActionProcessor(def node, def printer) {
        this.node = node
        this.printer = printer
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
    
    def static setup(def node, def printer, Closure closure) {
        closure.delegate = new ActionProcessor(node, printer)
        //printer.print("running action set on node $node")
        closure()
        //printer.print("done running action set on node $node")
    }
    
    def methodMissing(String name, Object args) {
        printer.print("looking for action '$name' builder")

        def builder = builders[name]
        if (builder != null) {
            printer.print("running action '$name' on node '$node.id'")
            def action = builder."$name"(*args)
            validate(action)
            
            try {
                action.execute(node)
            } catch (CommandExecutionException ex) {
                printer.print( "action '$name' failed on node '$node.id': $ex.result")
                return ex.result
            }
            
            printer.print("action $name executed succesfully on node '$node.id': result: $node.lastResult")
            return node.lastResult
        } else {
            printer.print("unknown action '$name'")
            throw new ActionProcessingException("Unknown action: $name")
        }
    }
    
    public void info(String message) {
        printer.print("INFO: $message")
    }
    
    public void debug(String message) {
        printer.print("DEBUG: $message", YELLOW)
    }
}
