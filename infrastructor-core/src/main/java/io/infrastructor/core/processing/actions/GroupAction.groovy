package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

public class GroupAction {
    
    @NotNull
    def name
    def gid
    def sudo = false

    def execute(def node) {
        def cmd = CMD {
            add sudo, "sudo"
            add "groupadd"
            add gid, "-g $gid"
            add name
        }
        
        node.execute command: cmd
        node.lastResult
    }
}
