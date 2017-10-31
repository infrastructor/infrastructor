package io.infrastructor.core.processing.actions

class ReplaceLineActionMixin {
    
    def static replaceLine(NodeContext context, Map params) {
        replaceLine(context, params, {})
    }
    
    def static replaceLine(NodeContext context, Closure closure) {
        replaceLine(context, [:], closure)
    }
    
    def static replaceLine(NodeContext context, Map params, Closure closure) {
        def action = new ReplaceLineAction(params)
        action.with(closure)
        context.execute(action)
    }
}

