package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

class InsertBlockAction {
    
    @NotNull
    def target
    @NotNull
    def block
    def position = START
    def owner
    def group
    def mode
    def user
    def sudopass

    private static final int START = 0
    private static final int END   = 1

    def execute(def node) {
        def stream = new ByteArrayOutputStream()
        node.readFile(target, stream, user, sudopass)
        def content = stream.toString()

        switch(position) {
            case START:
                content = (block + content)
                break
            case END:
                content = (content + block)
                break
        }

        node.writeText(target, content, user, sudopass)
        node.updateOwner(target, owner, user, sudopass)
        node.updateGroup(target, group, user, sudopass)
        node.updateMode(target, mode, user, sudopass)
        node.lastResult
    }
}
