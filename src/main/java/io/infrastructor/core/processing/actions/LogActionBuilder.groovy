package io.infrastructor.core.processing.actions

class LogActionBuilder {
    def logger
    
    def LogActionBuilder(def logger) {
        this.logger = logger
    }
    
    def debug(def message) {
        logger.debug(message)
    }
    
    def info(def message) {
        logger.info(message)
    }
}

