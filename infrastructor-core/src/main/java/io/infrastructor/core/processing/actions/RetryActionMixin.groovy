package io.infrastructor.core.processing.actions

class RetryActionMixin {
    
    def static retry(NodeContext context, Map params) {
        retry(context, params, {})
    }
    
    def static retry(NodeContext context, Closure closure) {
        retry(context, [:], closure)
    }
    
    def static retry(NodeContext context, Map params, Closure closure) {
        def action = new RetryAction(params)
        action.with(closure)
        context.execute(action)
    }
}

