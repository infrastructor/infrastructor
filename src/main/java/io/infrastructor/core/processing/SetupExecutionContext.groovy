package io.infrastructor.core.processing

import io.infrastructor.core.processing.actions.LogActionBuilder
import static io.infrastructor.cli.logging.ProgressLogger.*

class SetupExecutionContext {
    
    def nodes = []
    def closure
    
    def SetupExecutionContext(def nodes, def closure) {
        this.nodes = nodes
        this.closure = closure
    }
    
    def execute() {
        def cloned = closure.clone()
        def ctx = new ExecutionContext(parent: cloned.owner)
        ctx.handlers << ['nodes': new TaskBuilder(nodes)]
        closure.delegate = ctx
        closure()
    }
}


