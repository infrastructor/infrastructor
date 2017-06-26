package io.infrastructor.core.processing

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.validation.ValidationException

class TaskExecutionContext {
    def handlers = [:]
    
    def methodMissing(String name, Object args) {
        if (handlers.containsKey(name)) {
            try {
                handlers[name]."$name"(*args)
            } catch (CommandExecutionException ex) {
                throw new TaskExecutionException("remote command failed", name, ex.result)
            } catch (ValidationException ex) {
                throw new TaskExecutionException("action validation error", name, ex.result)
            } catch (ActionProcessingException ex) {
                throw new TaskExecutionException("action processing error", name, ex.message)
            }
        } else {
            throw new TaskExecutionException("action not found error", name, args)
        }
    }
}

