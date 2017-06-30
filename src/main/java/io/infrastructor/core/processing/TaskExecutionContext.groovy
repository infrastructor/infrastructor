package io.infrastructor.core.processing

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.processing.actions.ActionProcessingException
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.cli.logging.ConsoleLogger.*

class TaskExecutionContext {
    
    def parent
    def node
    def functions  = [:]
    
    def TaskExecutionContext(def node, def parent ) {
        this.node = node
        this.parent = parent
    }
    
    def methodMissing(String name, Object args) {
        if (functions.containsKey(name)) {
            try {
                functions[name]."$name"(*args)
            } catch (CommandExecutionException ex) {
                throw new TaskExecutionException("remote command failed",   [action: name, result: ex.result])
            } catch (ValidationException ex) {
                throw new TaskExecutionException("action validation error", [action: name, result: ex.result])
            } catch (ActionProcessingException ex) {
                throw new TaskExecutionException("action processing error", [action: name, message: ex.message])
            }
        } else {
            debug("function $name not found in the task execution context, looking at parent one")
            parent.$'name'(*args)
            throw new TaskExecutionException("action not found error", [action: name, args: args])
        }
    }
}

