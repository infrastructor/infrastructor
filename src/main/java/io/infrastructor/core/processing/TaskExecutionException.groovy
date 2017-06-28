package io.infrastructor.core.processing

class TaskExecutionException extends RuntimeException {
    
    def context = [:]
        
    def TaskExecutionException(def message, def context) {
        super(message)
        this.context = context
    }
}

