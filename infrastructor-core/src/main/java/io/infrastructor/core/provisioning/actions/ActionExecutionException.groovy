package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.provisioning.TaskExecutionException

class ActionExecutionException extends TaskExecutionException {
    
    ActionExecutionException(String message) {
        super(message)
    }
}

