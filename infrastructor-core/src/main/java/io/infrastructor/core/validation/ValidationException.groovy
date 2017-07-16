package io.infrastructor.core.validation


public class ValidationException extends RuntimeException {
    
    def result = [:]
    
    public ValidationException(def result) {
        this.result = result
    }
}
