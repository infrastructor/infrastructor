package io.infrastructor.core.tasks

import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.inventory.Node


public class GroupAction {
    
    @NotNull
    def name
    def gid
    def sudo = false
    
    def execute(Node node) {
        node.execute(command: "groupadd ${gid ? '-g ' + gid : ''} $name", sudo: sudo)
    }
}

