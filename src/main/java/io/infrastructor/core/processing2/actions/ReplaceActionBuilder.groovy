package io.infrastructor.core.processing2.actions

public class ReplaceActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new ReplaceActionBuilder()
        closure()
    }

    def replace(Map params) {
        replace(params, {})
    }
    
    def replace(Closure closure) {
        replace([:], closure)
    }
    
    def replace(Map params, Closure closure) {
        def action = new ReplaceAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

