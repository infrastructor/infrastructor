package io.infrastructor.core.utils

class ActionRegistrationException extends RuntimeException {
    
    public ActionRegistrationException(String message) {
        super(message)
    }
    
    public ActionRegistrationException(String message, Throwable cause) {
        super(message, cause)
    }
}
