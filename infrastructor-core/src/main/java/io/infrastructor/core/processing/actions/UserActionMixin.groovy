package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeContext

class UserActionMixin {
    def static user(NodeContext context, Map params) {
        user(context, params, {})
    }
    
    def static user(NodeContext context, Closure closure) {
        user(context, [:], closure)
    }
    
    def static user(NodeContext context, Map params, Closure closure) {
        def action = new UserAction(params)
        action.with(closure)
        context.execute(action)
    }
}