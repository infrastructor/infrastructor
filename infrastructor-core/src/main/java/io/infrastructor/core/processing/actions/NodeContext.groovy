package io.infrastructor.core.processing.actions

import io.infrastructor.core.inventory.RemoteExecutionException
import io.infrastructor.core.processing.actions.ActionExecutionException
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.validation.ValidationHelper.validate

class NodeContext {
    def node
    
    def execute(def action) {
        try {
            debug "action: ${action.class.simpleName}, node: ${node.getLogName()}, params: ${action.properties.findAll { 'class' != it.key }}"
            validate(action)
            action.execute(node)
        } catch (RemoteExecutionException ex) {
            throw new ActionExecutionException("action: ${action.class.simpleName}, message: remote command failed, details: $ex.message")
        } catch (ValidationException ex) {
            throw new ActionExecutionException("action: ${action.class.simpleName}, message: action validation error, details: $ex.message")
        } catch (Exception ex) {
            throw new ActionExecutionException("action: ${action.class.simpleName}, message: action execution error, details: $ex.message")
        }
    }
}

