package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeContext

class FileActionMixin {
    def static file(NodeContext context, Map params) {
        file(context, params, {})
    }
    
    def static file(NodeContext context, Closure closure) {
        file(context, [:], closure)
    }
    
    def static file(NodeContext context, Map params, Closure closure) {
        def action = new FileAction(params)
        action.with(closure)
        context.execute(action)
    }
}

