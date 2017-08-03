package io.infrastructor.core.processing

import static io.infrastructor.core.validation.ValidationHelper.validate
import io.infrastructor.core.processing.provisioning.TaskExecutionException

class ProvisioningContext {
    def nodes
    
    def static provision(def nodes, def closure) {
        def context = new ProvisioningContext(nodes: nodes)
        context.with(closure)
    }
    
    def execute(def executable) {
        try {
            validate(executable)
            executable.execute(nodes)
        } catch(ProvisioningExecutionException ex) {
            throw ex
        } catch(Exception ex) {
            throw new ProvisioningExecutionException(ex.getMessage());
        }
    }
}

