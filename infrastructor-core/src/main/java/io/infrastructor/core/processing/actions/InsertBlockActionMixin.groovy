package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeContext

class InsertBlockActionMixin {
    def static insertBlock(NodeContext context, Map params) {
        insertBlock(context, params, {})
    }
    
    def static insertBlock(NodeContext context, Closure closure) {
        insertBlock(context, [:], closure)
    }
    
    def static insertBlock(NodeContext context, Map params, Closure closure) {
        def action = new InsertBlockAction(params)
        action.with(closure)
        context.execute(action)
    }
}

