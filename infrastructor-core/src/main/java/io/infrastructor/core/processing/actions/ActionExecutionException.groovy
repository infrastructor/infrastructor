package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.provisioning.TaskExecutionException

public class ActionExecutionException extends TaskExecutionException {
    public ActionExecutionException(String message) {
        super(message)
    }
}

