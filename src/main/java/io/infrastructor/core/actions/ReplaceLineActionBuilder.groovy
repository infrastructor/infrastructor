package io.infrastructor.core.actions

public class ReplaceLineActionBuilder {
    
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
        return action
    }
}

