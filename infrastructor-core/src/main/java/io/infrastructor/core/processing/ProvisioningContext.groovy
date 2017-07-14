package io.infrastructor.core.processing

import static io.infrastructor.core.validation.ValidationHelper.validate

class ProvisioningContext {
    def nodes
    
    def static provision(def nodes, def closure) {
        def context = new ProvisioningContext(nodes: nodes)
        context.with(closure)
    }
    
    def execute(def executable) {
        validate(executable)
        executable.execute(nodes)
    }
}

