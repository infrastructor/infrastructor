package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class ReplaceLineActionBuilder extends AbstractNodeActionBuilder {

    def replaceLine(Map params) {
        replaceLine(params, {})
    }
    
    def replaceLine(Closure closure) {
        replaceLine([:], closure)
    }
    
    def replaceLine(Map params, Closure closure) {
        def action = new ReplaceLineAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
    }
}

