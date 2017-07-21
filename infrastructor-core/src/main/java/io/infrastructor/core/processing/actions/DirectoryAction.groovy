package io.infrastructor.core.processing.actions

import io.infrastructor.core.inventory.CommandExecutionException
import javax.validation.constraints.NotNull

public class DirectoryAction {
    
    @NotNull
    def target
    def owner
    def group
    def mode
    def sudo = false
    
    def execute(def node) {
        node.execute(command: "mkdir -m $mode -p $target", sudo: sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.lastResult
    }	
}

