package io.infrastructor.core.processing

import static io.infrastructor.cli.logging.ConsoleLogger.*

class ExecutionContext {
    
    def parent 
    def handlers = [:]
    
    def methodMissing(String name, Object args) {
        if (handlers.containsKey(name)) {
            handlers[name]."$name"(*args)
        } else {
            parent.$'name'(*args)
        }
    }
}
