package io.infrastructor.core.actions


public class UserActionBuilder {
    
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
        return action
    }
}