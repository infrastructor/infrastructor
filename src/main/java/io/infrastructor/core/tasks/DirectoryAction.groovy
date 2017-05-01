package io.infrastructor.core.tasks

import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull
import static io.infrastructor.cli.ConsoleLogger.info
import io.infrastructor.core.inventory.CommandExecutionException


public class DirectoryAction {
    
    @NotNull
    def target
    def owner
    def group
    def mode
    def sudo = false
    
    def execute(Node node) {
        node.execute(command: "mkdir $target", sudo: sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }	
}

