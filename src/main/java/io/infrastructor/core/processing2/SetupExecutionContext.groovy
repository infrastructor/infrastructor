package io.infrastructor.core.processing2

import io.infrastructor.core.processing2.actions.DebugActionBuilder

class SetupExecutionContext {
    
    def nodes = []
    def logger = new Logger()
    def closure
    
    def SetupExecutionContext(def nodes, def closure) {
        this.nodes = nodes
        this.closure = closure
    }
    
    def buildExecutionContext() {
         def executionContext = new ExecutionContext()
         executionContext.handlers << ['debug': new DebugActionBuilder(logger)]
         executionContext.handlers << ['nodes':  new TaskBuilder(nodes, logger)]
         executionContext
    }
    
    def execute() {
        closure.delegate = buildExecutionContext()
        closure()
    }
}


