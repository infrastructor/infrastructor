package io.infrastructor.core.actions


public class TemplateActionBuilder {
    
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
        return action
    }
}

