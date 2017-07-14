package io.infrastructor.core.processing

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.processing.actions.ActionProcessingException
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.core.validation.ValidationHelper.validate

class NodeContext {
    def node
    
    def execute(def action) {
        try {
            validate(action)
            action.execute(node)
        } catch (CommandExecutionException ex) {
            throw new NodeTaskExecutionException("remote command failed",   [action: action.class.simpleName, result: ex.result])
        } catch (ValidationException ex) {
            throw new NodeTaskExecutionException("action validation error", [action: action.class.simpleName, result: ex.result])
        } catch (ActionProcessingException ex) {
            throw new NodeTaskExecutionException("action processing error", [action: action.class.simpleName, message: ex.message])
        }
    }
}

