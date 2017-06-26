package io.infrastructor.core.processing2.actions

import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull

import static io.infrastructor.cli.ConsoleLogger.info

public class FileAction extends AbstractNodeAction {
    
    @NotNull
    def target
    def content
    def owner
    def group
    def mode
    def sudo = false
    
    def execute() {
        node.writeText(target, content.stripMargin().stripIndent(), sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
        node.lastResult
    }
}

