package io.infrastructor.core.provisioning

class TaskMixin {
    
    def static task(ProvisioningContext context, Closure closure) {
        task(context, [:], closure)
    }

    def static task(ProvisioningContext context, Map params) {
        task(context, params, {})
    }
    
    def static task(ProvisioningContext context, Map params, Closure closure) {
        def task = new Task(params)
        task.with(closure)
        context.execute(task)
    }
}



