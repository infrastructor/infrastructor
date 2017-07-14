package io.infrastructor.core.processing.provisioning

import io.infrastructor.core.processing.ProvisioningContext

class TaskMixin {
    
    def static task(ProvisioningContext context, Closure closure) {
        task(context, [:], closure)
    }

    def static task(ProvisioningContext context, String name, Closure closure) {
        task(context, [name: name], closure)
    }
    
    def static task(ProvisioningContext context, Map params, Closure closure) {
        def task = new Task(params)
        task.closure = closure
        context.execute(task)
    }
}



