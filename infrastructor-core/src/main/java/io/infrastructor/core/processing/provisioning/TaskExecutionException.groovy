package io.infrastructor.core.processing.provisioning

import io.infrastructor.core.processing.ProvisioningExecutionException

class TaskExecutionException extends ProvisioningExecutionException {
    
    TaskExecutionException(String message) {
        super(message)
    }
}

