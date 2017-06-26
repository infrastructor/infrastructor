package io.infrastructor.core.processing2

class Inventory {
    def nodes = []
    
    def setup(Closure closure) {
        SetupExecutionContext context = new SetupExecutionContext(nodes, closure)
        context.execute()
    }
}

