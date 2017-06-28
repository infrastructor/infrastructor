package io.infrastructor.core.processing

import io.infrastructor.core.processing.actions.LogActionBuilder
import io.infrastructor.core.utils.ProgressLogger

class SetupExecutionContext {
    
    def nodes = []
    def closure
    def logger = new ProgressLogger()
    
    def SetupExecutionContext(def nodes, def closure) {
        this.nodes = nodes
        this.closure = closure
    }
    
    def buildExecutionContext() {
         def executionContext = new ExecutionContext()
         executionContext.handlers << ['debug': new LogActionBuilder(logger)]
         executionContext.handlers << ['info':  new LogActionBuilder(logger)]
         executionContext.handlers << ['nodes': new TaskBuilder(nodes)]
         executionContext
    }
    
    def execute() {
        closure.delegate = buildExecutionContext()
        closure()
    }
}


