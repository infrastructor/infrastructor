package io.infrastructor.core.processing.provisioning

import io.infrastructor.core.processing.ProvisioningExecutionException

class TaskExecutionException extends ProvisioningExecutionException {
    public TaskExecutionException(String message) {
        super(message)
    }
}

