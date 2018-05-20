package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

public class FetchAction {
    
    @NotNull
    def source
    @NotNull
    def target
    def user

    def execute(def node) {
        new FileOutputStream(target).withCloseable {
            node.readFile(source, it, user)
        }
        node.lastResult
    }
}
