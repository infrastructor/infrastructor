package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

class FileAction {
    
    @NotNull
    def target
    def content
    def owner
    def group
    def mode
    def user
    def sudopass

    def execute(def node) {
        node.writeText(target, content.stripMargin().stripIndent(), user, sudopass)
        node.updateOwner(target, owner, user, sudopass)
        node.updateGroup(target, group, user, sudopass)
        node.updateMode(target, mode, user, sudopass)
        node.lastResult
    }
}
