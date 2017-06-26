package io.infrastructor.core.processing

class TaskExecutionException extends RuntimeException {
    
    def action
    def result
        
    def TaskExecutionException(def message, def action, def result) {
        super(message)
        this.action = action
        this.result = result
    }
}

