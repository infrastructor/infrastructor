package io.infrastructor.core.utils

class ActionRegistrationException extends RuntimeException {
    
    ActionRegistrationException(String message) {
        super(message)
    }
    
    ActionRegistrationException(String message, Throwable cause) {
        super(message, cause)
    }
}
