package io.infrastructor.core.provisioning.actions

class TemplateActionMixin {
    
    def static template(NodeContext context, Map params) {
        template(context, params, {})
    }
    
    def static template(NodeContext context, Closure closure) {
        template(context, [:], closure)
    }
    
    def static template(NodeContext context, Map params, Closure closure) {
        def action = new TemplateAction(params)
        action.with(closure)
        context.execute(action)
    }
}