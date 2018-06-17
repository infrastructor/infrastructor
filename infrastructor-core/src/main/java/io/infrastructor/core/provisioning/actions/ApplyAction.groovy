package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

class ApplyAction {
    
    @NotNull
    def closure
    def params = [:]
    
    def execute(def context) {
        def clonned = closure.clone()
        clonned.delegate = context
        clonned(params)
    }
}

