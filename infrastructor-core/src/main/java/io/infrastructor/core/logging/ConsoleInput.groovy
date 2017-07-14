package io.infrastructor.core.logging

class ConsoleInput {
    
    def static input(Map params) {
        input(params, {})
    }
    
    def static input(Closure closure) {
        input([:], closure)
    }
    
    def static input(Map params, Closure closure) {
        InputAction action = new InputAction(params)
        action.with(closure)
        action.execute()
    }
}

