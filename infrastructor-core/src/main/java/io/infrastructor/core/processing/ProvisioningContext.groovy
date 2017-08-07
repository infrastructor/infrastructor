package io.infrastructor.core.processing

import static io.infrastructor.core.validation.ValidationHelper.validate
import io.infrastructor.core.processing.provisioning.TaskExecutionException

class ProvisioningContext {
    def nodes
    def context = [:]
    
    def static provision(def nodes, def context, Closure closure) {
        def ctx = new ProvisioningContext(nodes: nodes, context: context)
        ctx.with(closure)
    }
    
    def static provision(def nodes, Closure closure) {
        def ctx = new ProvisioningContext(nodes: nodes)
        ctx.with(closure)
    }
    
    def execute(def executable) {
        try {
            validate(executable)
            executable.execute(nodes)
        } catch(ProvisioningExecutionException ex) {
            throw ex
        } catch(Exception ex) {
            throw new ProvisioningExecutionException(ex.getMessage())
        }
    }
}

