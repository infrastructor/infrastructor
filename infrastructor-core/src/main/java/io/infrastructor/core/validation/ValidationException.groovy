package io.infrastructor.core.validation

class ValidationException extends RuntimeException {
    
    ValidationException(String result) {
        super(result)
    }
}
