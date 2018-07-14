package io.infrastructor.core.provisioning.actions

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
    def user
    def sudopass

    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, user, sudopass)
            
        def original = stream.toString()
        def updated = original.split('\n').collect { 
            (it ==~ regexp) ? line : it 
        }.join('\n')
        
        node.writeText(target, updated, user, sudopass)
        node.updateOwner(target, owner, user, sudopass)
        node.updateGroup(target, group, user, sudopass)
        node.updateMode(target, mode, user, sudopass)
        node.lastResult
    }
}

