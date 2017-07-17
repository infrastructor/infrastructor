package io.infrastructor.core.processing.actions

class GroupActionMixin {
    def static group(NodeContext context, Map params) {
        group(context, params, {})
    }
    
    def static group(NodeContext context, Closure closure) {
        group(context, [:], closure)
    }
    
    def static group(NodeContext context, Map params, Closure closure) {
        def action = new GroupAction(params)
        action.with(closure)
        context.execute(action)
    }
}

