package io.infrastructor.core.processing2.actions

class DebugActionBuilder {
    def logger
    
    def DebugActionBuilder(def logger) {
        this.logger = logger
    }
    
    def debug(Map params) {
        debug(params, {})
    }
    
    def debug(Closure closure) {
        debug([:], closure)
    }
    
    def debug(Map params, Closure closure) {
        def action = new DebugAction(params)
        action.with(closure)
        action.execute(logger)  
    }
    
    def debug(def message) {
        def action = new DebugAction(message: message)
        validate(action)
        action.execute(logger)
    }
}

