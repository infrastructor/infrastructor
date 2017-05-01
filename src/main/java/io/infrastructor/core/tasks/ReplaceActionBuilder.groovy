package io.infrastructor.core.tasks


public class ReplaceActionBuilder {
    
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
        return action
    }
}

