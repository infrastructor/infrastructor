package io.infrastructor.core.utils

import io.infrastructor.core.provisioning.actions.ApplyAction
import io.infrastructor.core.provisioning.actions.NodeContext

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import static io.infrastructor.core.utils.GroovyShellUtils.load

class ActionRegistration {
    @NotNull
    @Size(min=1)
    String name
    
    String file = null
    Closure closure = null

    def execute() {
        if ((!closure && !file) || (closure && file)) {
            throw new ActionRegistrationException("Unable to register an action '$name': either a closure or a file must be specified")
        }

        if (file) { 
            try {
                closure = load(file)
            } catch(Exception ex) {
                throw new ActionRegistrationException("Unable to register an action '$name'", ex)
            }
        }

        NodeContext.metaClass[name] = { "$name"([:], {}) }
        NodeContext.metaClass[name] = { Map _params -> "$name"(_params, {}) }
        NodeContext.metaClass[name] = { Closure _closure -> "$name"([:], _closure) }
        NodeContext.metaClass[name] = { Map _params, Closure _closure ->
            def params = _params.clone()
            params.with(_closure)
            new ApplyAction(closure: closure, params: params).execute(delegate)
        }
    }
}