package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

public class InsertBlockAction {
    
    @NotNull
    def target
    @NotNull
    def block
    def position = START
    def owner
    def group
    def mode
    def sudo = false
    
    private static final int START = 0
    private static final int END   = 1
    
    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, sudo)
        def content = stream.toString()

        switch(position) {
            case START:
                content = (block + content)
                break
            case END: 
                content = (content + block)
                break
        }
            
        node.writeText(target, content, sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
        node.lastResult
    }
}

