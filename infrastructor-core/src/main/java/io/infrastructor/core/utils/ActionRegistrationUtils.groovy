package io.infrastructor.core.utils

import io.infrastructor.core.processing.actions.NodeContext
import io.infrastructor.core.processing.actions.ApplyAction
import io.infrastructor.core.processing.actions.ActionRegistrationAction

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.utils.GroovyShellUtils.load

class ActionRegistrationUtils {

    def static action(String name, Closure closure) {
        action name: name, closure: closure
    }

    def static action(Map params) {
        action(params, {})
    }
    
    def static action(Closure closure) {
        action([:], closure)
    }
    
    def static action(Map params, Closure closure) {
        def action = new ActionRegistrationAction(params)
        action.with(closure)
        action.execute()
    }
}

