package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class FileUploadActionBuilder extends AbstractNodeActionBuilder {
    
    def upload(Map params) {
        upload(params, {})
    }
    
    def upload(Closure closure) {
        upload([:], closure)
    }
    
    def upload(Map params, Closure closure) {
        def action = new FileUploadAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
    }
}

