package io.infrastructor.core.processing.actions

import io.infrastructor.core.inventory.Node

class ActionContext {
    
    private static final ThreadLocal<Node> holder = new ThreadLocal<Node>();
    
    def static init(Node node) {
        holder.set(node)
    }
    
    def static node() {
        def node = holder.get()
        if (!node) { throw new ActionProcessingException('Node context is not available.') }
        return node
    }
}

