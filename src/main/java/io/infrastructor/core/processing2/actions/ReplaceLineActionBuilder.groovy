package io.infrastructor.core.processing2.actions

public class ReplaceLineActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new ReplaceLineActionBuilder()
        closure()
    }

    def replaceLine(Map params) {
        replaceLine(params, {})
    }
    
    def replaceLine(Closure closure) {
        replaceLine([:], closure)
    }
    
    def replaceLine(Map params, Closure closure) {
        def action = new ReplaceLineAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

