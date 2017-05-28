package io.infrastructor.core.actions

import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull

import static io.infrastructor.cli.ConsoleLogger.info
import io.infrastructor.core.inventory.CommandExecutionException


public class FileAction {
    
    @NotNull
    def target
    def content
    def owner
    def group
    def mode
    def sudo = false
    
    def execute(Node node) {
        node.writeText(target, content.stripMargin().stripIndent(), sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }
}

