package io.infrastructor.core.actions


public class InsertBlockActionBuilder {
    
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
        return action
    }
}

