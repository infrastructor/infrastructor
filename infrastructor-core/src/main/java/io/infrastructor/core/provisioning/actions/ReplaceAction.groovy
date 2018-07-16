package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

class ReplaceAction {
    
    @NotNull
    def target
    @NotNull
    def regexp
    @NotNull
    def content
    def owner
    def group
    def mode
    def all = false
    def user
    def sudopass

    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, user, sudopass)

        def original = stream.toString()
        def updated = all ? original.replaceAll(regexp, content) : original.replaceFirst(regexp, content)

        node.writeText(target, updated, user, sudopass)
        node.updateOwner(target, owner, user, sudopass)
        node.updateGroup(target, group, user, sudopass)
        node.updateMode(target, mode, user, sudopass)
        node.lastResult
    }
}
