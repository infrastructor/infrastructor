package io.infrastructor.core.provisioning.actions

class UploadActionMixin {
    
    def static upload(NodeContext context, Map params) {
        upload(context, params, {})
    }
    
    def static upload(NodeContext context, Closure closure) {
        upload(context, [:], closure)
    }
    
    def static upload(NodeContext context, Map params, Closure closure) {
        def action = new UploadAction(params)
        action.with(closure)
        context.execute(action)
    }
}

