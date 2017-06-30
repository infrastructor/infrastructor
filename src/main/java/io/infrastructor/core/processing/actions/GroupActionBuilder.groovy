package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class GroupActionBuilder extends AbstractNodeActionBuilder {
    
    def group(Map params) {
        group(params, {})
    }
    
    def group(Closure closure) {
        group([:], closure)
    }
    
    def group(Map params, Closure closure) {
        def action = new GroupAction(params)
        action.with(closure)
        validate(action)
        action.execute(node)
    }
}

