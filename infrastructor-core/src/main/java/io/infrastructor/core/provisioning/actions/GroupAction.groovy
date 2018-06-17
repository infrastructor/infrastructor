package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

class GroupAction {
    
    @NotNull
    def name
    def gid
    def user

    def execute(def node) {
        def cmd = CMD {
            add user, "sudo -s -u $user"
            add "groupadd"
            add gid, "-g $gid"
            add name
        }
        
        node.execute command: cmd
        node.lastResult
    }
}
