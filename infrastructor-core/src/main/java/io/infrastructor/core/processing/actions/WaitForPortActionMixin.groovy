package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeContext

class WaitForPortActionMixin {
    def static waitForPort(NodeContext context, Map params) {
        waitForPort(context, params, {})
    }
    
    def static waitForPort(NodeContext context, Closure closure) {
        waitForPort(context, [:], closure)
    }
    
    def static waitForPort(NodeContext context, Map params, Closure closure) {
        def action = new WaitForPortAction(params)
        action.with(closure)
        context.execute(action)
    }
}

