package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.processing.ExecutionContext
import io.infrastructor.core.processing.TaskBuilder

import static io.infrastructor.core.validation.ValidationHelper.validate

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class Inventory {
    
    def nodes = []
    
    public void leftShift(Node node) {
        nodes << validate(node) 
    }
    
    def setup(Closure closure) {
        def cloned = closure.clone()
        def ctx = new ExecutionContext(parent: cloned.owner)
        ctx.handlers << ['nodes': new TaskBuilder(nodes)]
        cloned.delegate = ctx
        cloned()
        this
    }
}

