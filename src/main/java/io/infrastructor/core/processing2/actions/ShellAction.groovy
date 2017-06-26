package io.infrastructor.core.processing2.actions

class ShellAction extends AbstractNodeAction {
    def sudo
    def command
    
    def execute() {
        node.execute(command: command, sudo: sudo)
    }
}
