package io.infrastructor.core.processing.actions

import io.infrastructor.core.utils.RetryUtils
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

import static io.infrastructor.core.utils.RetryUtils.retry

class RetryAction {
    @Min(1l)
    int count = 2
    @Min(1l)
    int delay = 1000
    @NotNull
    def actions = {}
    
    def execute(def node) {
        retry(count, delay, actions)
    }
}