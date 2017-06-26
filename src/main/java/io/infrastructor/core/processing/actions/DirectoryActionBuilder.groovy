package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class DirectoryActionBuilder extends AbstractNodeActionBuilder {
	
    def directory(Map params) {
        directory(params, {})
    }
    
    def directory(Closure closure) {
        directory([:], closure)
    }
    
    def directory(Map params, Closure closure) {
        def action = new DirectoryAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
    }
}

