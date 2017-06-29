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
    
    def buildExecutionContext() {
         def executionContext = new ExecutionContext()
         executionContext.handlers << ['debug': new LogActionBuilder()]
         executionContext.handlers << ['info':  new LogActionBuilder()]
         executionContext.handlers << ['nodes': new TaskBuilder(nodes)]
         executionContext
    }
    
    def execute() {
        closure.delegate = buildExecutionContext()
        closure()
    }
}


