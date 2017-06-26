package io.infrastructor.core.processing2.actions

public class GroupActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new GroupActionBuilder()
        closure()
    }

    def group(Map params) {
        group(params, {})
    }
    
    def group(Closure closure) {
        group([:], closure)
    }
    
    def group(Map params, Closure closure) {
        def action = new GroupAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

