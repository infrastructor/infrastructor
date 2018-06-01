package io.infrastructor.core.utils

import io.infrastructor.core.processing.actions.NodeContext
import io.infrastructor.core.processing.actions.ApplyAction
import io.infrastructor.core.processing.actions.ActionRegistrationAction

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.utils.GroovyShellUtils.load

class ActionRegistrationUtils {

    def static register(Map params) {
        register(params, {})
    }
    
    def static register(Closure closure) {
        register([:], closure)
    }
    
    def static register(Map params, Closure closure) {
        def action = new ActionRegistrationAction(params)
        action.with(closure)
        action.execute()
    }
}

