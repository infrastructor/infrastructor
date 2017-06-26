package io.infrastructor.core.processing2.actions

public class TemplateActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new TemplateActionBuilder()
        closure()
    }
    
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
        node.lastResult
    }
}

