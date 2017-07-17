package io.infrastructor.core.validation


public class ValidationException extends RuntimeException {
    public ValidationException(String result) {
        super(result)
    }
}
