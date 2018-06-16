package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

class DirectoryAction {
    
    @NotNull
    def target
    def owner
    def group
    def mode = '644'
    def user

    def execute(def node) {
        def cmd = CMD {
            add user, "sudo -s -u $user"
            add "mkdir"
            add mode, "-m $mode"
            add "-p $target"
        }
        
        node.execute command: cmd
        node.updateOwner(target, owner, user)
        node.updateGroup(target, group, user)
        node.lastResult
    }
}
