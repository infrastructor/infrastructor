package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.provisioning.TaskExecutionException

class ActionExecutionException extends TaskExecutionException {
    
    public ActionExecutionException(String message) {
        super(message)
    }
}

