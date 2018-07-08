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
    def sudopass

    def execute(def node) {
        def cmd = CMD {
            add sudopass, "echo $sudopass |"
            add sudopass || user, "sudo -S"
            add user, "-u $user"
            add "mkdir"
            add mode, "-m $mode"
            add "-p $target"
        }
        
        node.execute command: cmd
        node.updateOwner(target, owner, user, sudopass)
        node.updateGroup(target, group, user, sudopass)
        node.lastResult
    }
}
