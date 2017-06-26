package io.infrastructor.core.processing2.actions

public class InsertBlockActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new ReplaceActionBuilder()
        closure()
    }

    def insertBlock(Map params) {
        insertBlock(params, {})
    }
    
    def insertBlock(Closure closure) {
        insertBlock([:], closure)
    }
    
    def insertBlock(Map params, Closure closure) {
        def action = new InsertBlockAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

