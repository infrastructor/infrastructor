package io.infrastructor.core.processing.actions

class ActionRegistrationAction {
    def name
    def action

    def execute() {
        NodeContext.metaClass[name] = { "$name"([:], {}) }
        NodeContext.metaClass[name] = { Map params -> "$name"(params, {}) }
        NodeContext.metaClass[name] = { Closure closure -> "$name"([:], closure) }
        NodeContext.metaClass[name] = { Map params, Closure closure ->
            new ApplyAction(closure: action, params: params).execute(delegate)
        }
    }
}