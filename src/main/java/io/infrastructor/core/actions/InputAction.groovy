package io.infrastructor.core.actions

import javax.validation.constraints.NotNull

public class InputAction {
    
    def message = 'enter a value: '
    def secret = false
    
    def execute() {
        def console  = System.console()
        if (secret) {
            return console.readPassword(message) 
        } else {
            return console.readLine(message)
        }
    }
    
    def static input(Map params) {
        input(params, {})
    }
    
    def static input(Closure closure) {
        input([:], closure)
    }
    
    def static input(Map params, Closure closure) {
        def action = new InputAction(params)
        action.with(closure)
        action.execute()
    }
}

