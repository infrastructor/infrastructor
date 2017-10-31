package io.infrastructor.core.processing.actions

class ApplyActionMixin {
    
    def static apply(NodeContext context, Map params) {
        apply(context, params, {})
    }
    
    def static apply(NodeContext context, Closure closure) {
        apply(context, [:], closure)
    }
    
    def static apply(NodeContext context, Map params, Closure closure) {
        def action = new ApplyAction(params)
        action.with(closure)
        action.execute(context)
    }
}

