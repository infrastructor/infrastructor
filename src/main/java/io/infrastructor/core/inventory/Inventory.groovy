package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.processing.ActionPlanRunner

import static io.infrastructor.core.validation.ValidationHelper.validate

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class Inventory {
    
    def nodes = []
    
    public void leftShift(Node node) {
        nodes << validate(node) 
    }
    
    public Inventory setup(def closure) {
        ActionPlanRunner.setup(nodes, closure) 
        this
    }
}

