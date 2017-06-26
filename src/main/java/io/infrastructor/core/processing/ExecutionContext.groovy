package io.infrastructor.core.processing

class ExecutionContext {
    
    def handlers = [:]
    
    def methodMissing(String name, Object args) {
        if (handlers.containsKey(name)) {
            handlers[name]."$name"(*args)
        } else {
            println "Unknown function: $name, args: $args" 
        }
    }
}
