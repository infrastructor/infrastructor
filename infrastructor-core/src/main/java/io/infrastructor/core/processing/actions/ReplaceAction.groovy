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
    def sudo = false

    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, sudo)

        def original = stream.toString()
        def updated = all ? original.replaceAll(regexp, content) : original.replaceFirst(regexp, content)

        node.writeText(target, updated, sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
        node.lastResult
    }
}
