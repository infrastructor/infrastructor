package io.infrastructor.core.actions


public class FileUploadActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new FileUploadActionBuilder()
        closure()
    }

    def upload(Map params) {
        upload(params, {})
    }
    
    def upload(Closure closure) {
        upload([:], closure)
    }
    
    def upload(Map params, Closure closure) {
        def action = new FileUploadAction(params)
        action.with(closure)
        return action
    }
}

