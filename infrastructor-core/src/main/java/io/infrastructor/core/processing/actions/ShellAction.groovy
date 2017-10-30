package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

class ShellAction {
    
    @NotNull
    def command
    def sudo
    
    def execute(def node) {
        if (command.contains("\n")) {
            def result = node.execute command: "mktemp"
            def temp = result.output.trim()
            try { 
                node.writeText(temp, command.stripIndent())
                node.execute command: CMD {
                    add sudo, "sudo"
                    add "sh $temp"
                }
                
                return node.lastResult
            } finally {
                node.execute command: "rm $temp"
            }
        } else {
            node.execute command: CMD {
                add sudo, "sudo"
                add command
            }
        }
        
        node.lastResult
    }
}
