package io.infrastructor.core.provisioning

import static io.infrastructor.core.validation.ValidationHelper.validate

class ProvisioningContext {

    def inventory
    def context = [:]

    def static provision(def inventory, def context, Closure closure) {
        def ctx = new ProvisioningContext(inventory: inventory, context: context)
        def clonned = closure.clone()
        clonned.delegate = ctx
        clonned(inventory)
    }

    def static provision(def inventory, Closure closure) {
        provision(inventory, [:], closure)
    }

    def execute(def executable) {
        try {
            validate(executable)
            executable.execute(inventory)
        } catch (ProvisioningExecutionException ex) {
            throw ex
        } catch (Exception ex) {
            throw new ProvisioningExecutionException(ex.getMessage())
        }
    }
}

