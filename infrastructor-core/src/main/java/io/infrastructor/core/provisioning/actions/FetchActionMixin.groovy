package io.infrastructor.core.provisioning.actions

class FetchActionMixin {
    
    def static fetch(NodeContext context, Map params) {
        fetch(context, params, {})
    }
    
    def static fetch(NodeContext context, Closure closure) {
        fetch(context, [:], closure)
    }
    
    def static fetch(NodeContext context, Map params, Closure closure) {
        def action = new FetchAction(params)
        action.with(closure)
        context.execute(action)
    }
}

