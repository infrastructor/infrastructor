package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class ReplaceActionBuilder extends AbstractNodeActionBuilder {
    
    def replace(Map params) {
        replace(params, {})
    }
    
    def replace(Closure closure) {
        replace([:], closure)
    }
    
    def replace(Map params, Closure closure) {
        def action = new ReplaceAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
    }
}

