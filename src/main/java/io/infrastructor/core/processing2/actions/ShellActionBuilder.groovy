package io.infrastructor.core.processing2.actions

class ShellActionBuilder extends AbstractNodeActionBuilder {
    
    def shell(Map params) {
        shell(params, {})
    }
    
    def shell(Closure closure) {
        shell([:], closure)
    }
    
    def shell(Map params, Closure closure) {
        def action = new ShellAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
    
    def shell(String command) {
        def action = new ShellAction(command: command)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

