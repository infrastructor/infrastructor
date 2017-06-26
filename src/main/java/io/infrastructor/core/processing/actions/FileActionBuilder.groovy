package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class FileActionBuilder extends AbstractNodeActionBuilder {
    
    def file(Map params) {
        file(params, {})
    }
    
    def file(Closure closure) {
        file([:], closure)
    }
    
    def file(Map params, Closure closure) {
        def action = new FileAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
    }
}

