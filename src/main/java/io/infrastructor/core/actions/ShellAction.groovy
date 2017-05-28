package io.infrastructor.core.actions

import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.inventory.Node


public class ShellAction {
    
    @NotNull
    def command
    boolean sudo = false
    
    def execute(Node node) {
        node.execute(command: command, sudo: sudo)
    }
}

