package io.infrastructor.core.processing

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.validation.ValidationException

class TaskExecutionContext {
    
    def functions  = [:]
    def properties = [:]
    
    def TaskExecutionContext(def node) {
        properties['node'] = node
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
            throw new TaskExecutionException("action not found error", [action: name, args: args])
        }
    }
    
    def propertyMissing(String name, def value) { 
        if (properties.containsKey(name)) {
            properties[name] = value 
        } else {
            throw new TaskExecutionException("property not found error", [property: name, value: value])
        }
    }
    
    def propertyMissing(String name) { 
        if (properties.containsKey(name)) {
            return properties[name]
        } else {
            throw new TaskExecutionException("property not found error", [property: name])
        }
    }
}

