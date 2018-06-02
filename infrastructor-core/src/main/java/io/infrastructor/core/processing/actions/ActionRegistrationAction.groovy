package io.infrastructor.core.processing.actions

class ActionRegistrationAction {
    def name
    def closure

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