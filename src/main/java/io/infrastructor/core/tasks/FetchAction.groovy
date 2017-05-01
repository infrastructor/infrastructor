package io.infrastructor.core.tasks

import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull


public class FetchAction {
    
    @NotNull
    def source
    @NotNull
    def target
    def sudo = false
    
    def execute(Node node) {
        new FileOutputStream(target).withCloseable {
            node.readFile(source, it, sudo) 
        }
    }
}

