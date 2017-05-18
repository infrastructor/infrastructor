package io.infrastructor.core.tasks

import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull

public class ReplaceLineAction {
    @NotNull
    def target
    @NotNull
    def regexp
    @NotNull
    def line
    def owner
    def group
    def mode
    def sudo = false
    
    def execute(Node node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, sudo)
            
        def original = stream.toString()
        def updated = original.split('\n').collect { 
            (it ==~ regexp) ? line : it 
        }.join('\n')
        
        node.writeText(target, updated, sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }
}

