package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

import static io.infrastructor.core.utils.RetryUtils.retry

class RetryAction {
    
    @Min(1l)
    int count = 2
    @Min(0l)
    int delay = 0
    @NotNull
    def actions = {}
    
    def execute(def node) {
        retry(count, delay, actions)
    }
}