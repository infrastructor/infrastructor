package io.infrastructor.core.provisioning.actions

class DirectoryActionMixin {
    
    def static directory(NodeContext context, Map params) {
        directory(context, params, {})
    }
    
    def static directory(NodeContext context, Closure closure) {
        directory(context, [:], closure)
    }
    
    def static directory(NodeContext context, Map params, Closure closure) {
        def action = new DirectoryAction(params)
        action.with(closure)
        context.execute(action)
    }
}

