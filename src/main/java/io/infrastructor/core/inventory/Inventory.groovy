package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.processing.SetupExecutionContext

import static io.infrastructor.core.validation.ValidationHelper.validate

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class Inventory {
    
    def nodes = []
    
    public void leftShift(Node node) {
        nodes << validate(node) 
    }
    
    def setup(Closure closure) {
        SetupExecutionContext context = new SetupExecutionContext(nodes, closure)
        context.execute()
        this
    }
}

