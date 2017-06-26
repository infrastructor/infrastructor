package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class InsertBlockActionBuilder extends AbstractNodeActionBuilder {
    
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
    }
}

