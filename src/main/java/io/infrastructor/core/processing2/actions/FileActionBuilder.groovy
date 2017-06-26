package io.infrastructor.core.processing2.actions

public class FileActionBuilder extends AbstractNodeActionBuilder {
    
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
        action.node = node
        action
    }
}

