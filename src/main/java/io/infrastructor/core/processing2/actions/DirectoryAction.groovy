package io.infrastructor.core.processing2.actions

import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull
import static io.infrastructor.cli.ConsoleLogger.info
import io.infrastructor.core.inventory.CommandExecutionException


public class DirectoryAction extends AbstractNodeAction {
    
    @NotNull
    def target
    def owner
    def group
    def mode
    def sudo = false
    
    def execute() {
        node.execute(command: "mkdir $target", sudo: sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }	
}

