package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class TemplateActionBuilder extends AbstractNodeActionBuilder {
    
    def template(Map params) {
        template(params, {})
    }
    
    def template(Closure closure) {
        template([:], closure)
    }
    
    def template(Map params, Closure closure) {
        def action = new TemplateAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
    }
}

