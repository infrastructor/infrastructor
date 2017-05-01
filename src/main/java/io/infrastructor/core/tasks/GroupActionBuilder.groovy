package io.infrastructor.core.tasks


public class GroupActionBuilder {
    
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
        return action
    }
}

