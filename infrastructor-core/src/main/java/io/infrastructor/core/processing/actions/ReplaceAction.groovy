package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

public class ReplaceAction {
    
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
    def user = false

    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, user)

        def original = stream.toString()
        def updated = all ? original.replaceAll(regexp, content) : original.replaceFirst(regexp, content)

        node.writeText(target, updated, user)
        node.updateOwner(target, owner, user)
        node.updateGroup(target, group, user)
        node.updateMode(target, mode, user)
        node.lastResult
    }
}
