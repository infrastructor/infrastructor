package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

public class GroupAction {
    
    @NotNull
    def name
    def gid
    def sudo = false
    
    def execute(def node, def logger) {
        node.execute(command: "groupadd ${gid ? '-g ' + gid : ''} $name", sudo: sudo)
        node.lastResult
    }
}

