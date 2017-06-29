package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class FetchActionBuilder extends AbstractNodeActionBuilder {
    
    def fetch(Map params) {
        fetch(params, {})
    }
    
    def fetch(Closure closure) {
        fetch([:], closure)
    }
    
    def fetch(Map params, Closure closure) {
        def action = new FetchAction(params)
        action.with(closure)
        validate(action)
        action.execute(node)
    }
}

