package io.infrastructor.core.actions


public class DirectoryActionBuilder {
	
    def static execute(closure) {
        closure.delegate = new DirectoryActionBuilder()
        closure()
    }

    def directory(Map params) {
        directory(params, {})
    }
    
    def directory(Closure closure) {
        directory([:], closure)
    }
    
    def directory(Map params, Closure closure) {
        def action = new DirectoryAction(params)
        action.with(closure)
        return action
    }
}

