package io.infrastructor.core.tasks

import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.Node

import static io.infrastructor.core.inventory.Node.withSudo
import static io.infrastructor.cli.ConsoleLogger.info
import io.infrastructor.core.inventory.CommandExecutionException


public class FileUploadAction {
    
    @NotNull
    def target
    @NotNull
    def source
    def group
    def owner
    def mode
    def sudo = false
    
    def execute(Node node) {
        node.writeFile(target, new FileInputStream(source), sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }
}

