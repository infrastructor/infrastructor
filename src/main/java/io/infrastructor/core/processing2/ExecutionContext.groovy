package io.infrastructor.core.processing2

import io.infrastructor.core.validation.ValidationHelper

class ExecutionContext {
    def handlers = [:]
    
    def methodMissing(String name, Object args) {
        if (handlers.containsKey(name)) {
            
            def action = handlers[name]."$name"(*args)
            ValidationHelper.validate(action) // NODES??
            def result = action.execute()
            println "ExecutionContext result: $result"
            
            return result
        } else {
            println "Unknown function: $name, args: $args" 
        }
    }
}


