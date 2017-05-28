package io.infrastructor.core.actions


public class FileActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new FileActionBuilder()
        closure()
    }

    def file(Map params) {
        file(params, {})
    }
    
    def file(Closure closure) {
        file([:], closure)
    }
    
    def file(Map params, Closure closure) {
        def action = new FileAction(params)
        action.with(closure)
        return action
    }
}

