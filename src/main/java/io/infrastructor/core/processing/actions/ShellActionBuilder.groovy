package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

class ShellActionBuilder extends AbstractNodeActionBuilder {
    
    def shell(String command) {
        shell(command: command)
    }
    
    def shell(Map params) {
        shell(params, {})
    }
    
    def shell(Closure closure) {
        shell([:], closure)
    }
    
    def shell(Map params, Closure closure) {
        def action = new ShellAction(params)
        action.with(closure)
        validate(action)
        action.execute(node)
    }
}

