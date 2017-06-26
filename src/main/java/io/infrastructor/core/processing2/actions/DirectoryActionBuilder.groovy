package io.infrastructor.core.processing2.actions

public class DirectoryActionBuilder extends AbstractNodeActionBuilder {
	
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
        action.node = node
        action.logger = logger
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

