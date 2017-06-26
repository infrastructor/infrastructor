package io.infrastructor.core.processing2.actions

public class UserActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new UserActionBuilder()
        closure()
    }

    def user(Map params) {
        user(params, {})
    }
    
    def user(Closure closure) {
        user([:], closure)
    }
    
    def user(Map params, Closure closure) {
        def action = new UserAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}