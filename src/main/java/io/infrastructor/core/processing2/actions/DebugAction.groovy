package io.infrastructor.core.processing2.actions

class DebugAction {
    def message
    
    def execute(def logger) {
        logger.print(message)
    }
}
