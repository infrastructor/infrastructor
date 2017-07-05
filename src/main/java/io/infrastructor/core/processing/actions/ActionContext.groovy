package io.infrastructor.core.processing.actions

class ActionContext {
    def static node() {
        throw new ActionProcessingException("Node context is not available.")
    }
}

