package io.infrastructor.core.processing

import io.infrastructor.core.processing.actions.DebugActionBuilder
import io.infrastructor.core.utils.ProgressLogger

class SetupExecutionContext {
    
    def nodes = []
    def closure
    
    def SetupExecutionContext(def nodes, def closure) {
        this.nodes = nodes
        this.closure = closure
    }
    
    def buildExecutionContext() {
         def executionContext = new ExecutionContext()
         executionContext.handlers << ['debug': new DebugActionBuilder(new ProgressLogger())]
         executionContext.handlers << ['nodes': new TaskBuilder(nodes)]
         executionContext
    }
    
    def execute() {
        closure.delegate = buildExecutionContext()
        closure()
    }
}


