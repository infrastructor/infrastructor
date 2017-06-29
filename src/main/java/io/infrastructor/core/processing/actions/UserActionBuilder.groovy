package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class UserActionBuilder extends AbstractNodeActionBuilder {
    
    def user(Map params) {
        user(params, {})
    }
    
    def user(Closure closure) {
        user([:], closure)
    }
    
    def user(Map params, Closure closure) {
        def action = new UserAction(params)
        action.with(closure)
        validate(action)
        action.execute(node)
    }
}