package io.infrastructor.core.processing.actions

class DebugActionBuilder {
    def logger
    
    def DebugActionBuilder(def logger) {
        this.logger = logger
    }
    
    def debug(def message) {
        logger.println "DEBUG: $message"
    }
}

