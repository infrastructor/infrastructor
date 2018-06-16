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

    def execute(def node) {
        node.writeText(target, content.stripMargin().stripIndent(), user)
        node.updateOwner(target, owner, user)
        node.updateGroup(target, group, user)
        node.updateMode(target, mode, user)
        node.lastResult
    }
}
