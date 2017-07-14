package io.infrastructor.core.processing

class NodeTaskExecutionException extends RuntimeException {
    
    def context = [:]
        
    def NodeTaskExecutionException(def message, def context) {
        super(message)
        this.context = context
    }
    
    public String toString() {
        "$message, $context"
    }
}

