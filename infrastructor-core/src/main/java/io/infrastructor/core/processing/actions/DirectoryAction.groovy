package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

public class DirectoryAction {
    @NotNull
    def target
    def owner
    def group
    def mode = '644'
    def sudo = false

    def execute(def node) {
        def cmd = CMD {
            add sudo, "sudo"
            add "mkdir"
            add mode, "-m $mode"
            add "-p $target"
        }
        
        node.execute command: cmd
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.lastResult
    }
}
