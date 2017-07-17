package io.infrastructor.core.processing.actions

class ReplaceActionMixin {
    def static replace(NodeContext context, Map params) {
        replace(context, params, {})
    }
    
    def static replace(NodeContext context, Closure closure) {
        replace(context, [:], closure)
    }
    
    def static replace(NodeContext context, Map params, Closure closure) {
        def action = new ReplaceAction(params)
        action.with(closure)
        context.execute(action)
    }
}