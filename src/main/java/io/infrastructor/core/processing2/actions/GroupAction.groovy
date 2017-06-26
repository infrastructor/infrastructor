package io.infrastructor.core.processing2.actions

import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.inventory.Node


public class GroupAction extends AbstractNodeAction {
    
    @NotNull
    def name
    def gid
    def sudo = false
    
    def execute() {
        node.execute(command: "groupadd ${gid ? '-g ' + gid : ''} $name", sudo: sudo)
    }
}

