package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeContext

class ShellActionMixin {
    def static shell(NodeContext context, String command) {
        shell(context, [command: command])
    }
    
    def static shell(NodeContext context, Map params) {
        shell(context, params, {})
    }
    
    def static shell(NodeContext context, Closure closure) {
        shell(context, [:], closure)
    }
    
    def static shell(NodeContext context, Map params, Closure closure) {
        def action = new ShellAction(params)
        action.with(closure)
        context.execute(action)
    }
}

