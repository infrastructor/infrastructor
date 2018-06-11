package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

class ReplaceLineAction {
    
    @NotNull
    def target
    @NotNull
    def regexp
    @NotNull
    def line
    def owner
    def group
    def mode
    def user = false
    
    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, user)
            
        def original = stream.toString()
        def updated = original.split('\n').collect { 
            (it ==~ regexp) ? line : it 
        }.join('\n')
        
        node.writeText(target, updated, user)
        node.updateOwner(target, owner, user)
        node.updateGroup(target, group, user)
        node.updateMode(target, mode, user)
        node.lastResult
    }
}

