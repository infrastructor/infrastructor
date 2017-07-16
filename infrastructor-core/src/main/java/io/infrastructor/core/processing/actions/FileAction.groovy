package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

public class FileAction {
    
    @NotNull
    def target
    def content
    def owner
    def group
    def mode
    def sudo = false
    
    def execute(def node) {
        node.writeText(target, content.stripMargin().stripIndent(), sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
        node.lastResult
    }
}

