package io.infrastructor.core.inventory

public class CommandExecutionException extends RuntimeException {
    
    def result
    
    public CommandExecutionException(def result) {
        super()
        this.result = result
    }
}

