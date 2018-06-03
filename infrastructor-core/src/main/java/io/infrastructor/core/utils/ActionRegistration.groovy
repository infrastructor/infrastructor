package io.infrastructor.core.utils

import io.infrastructor.core.processing.actions.ApplyAction
import io.infrastructor.core.processing.actions.NodeContext
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class ActionRegistration {
    @NotNull
    @Size(min=1)
    String name
    @NotNull
    Closure closure

    def execute() {
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