package io.infrastructor.core.inventory

import static io.infrastructor.core.processing.ActionPlanRunner.setup
import static io.infrastructor.core.validation.ValidationHelper.validate

public class Inventory {
    
    def nodes = []
    
    public void leftShift(Node node) {
        nodes << validate(node) 
    }
    
    public Inventory setup(def closure) {
        setup(nodes, closure) 
        this
    }
}

