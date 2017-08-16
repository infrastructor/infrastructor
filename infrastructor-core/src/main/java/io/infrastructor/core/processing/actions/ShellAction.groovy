package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

class ShellAction {
    @NotNull
    def command
    def sudo
    
    def execute(def node) {
        if (command.contains("\n")) {
            def result = node.execute(command: "mktemp")
            def temp = result.output
            node.writeText(temp, command.stripIndent())
            node.execute(command: "sh $temp", sudo: sudo)
        } else {
            node.execute(command: command, sudo: sudo)
        }
        
        node.lastResult
    }
}
