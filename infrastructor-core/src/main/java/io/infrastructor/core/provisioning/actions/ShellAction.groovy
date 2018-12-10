package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

class ShellAction {
    @NotNull
    def command
    def user
    def sudopass

    def failfast = true

    def execute(def node) {
        if (command.contains("\n")) {

            def result = node.execute command: CMD {
                add sudopass, "echo $sudopass |"
                add sudopass || user, "sudo -S"
                add user, "-u $user"
                add "sh -c 'mktemp'"
            }, failfast
            
            def temp = result.output.trim()

            try {
                node.writeText(temp, command.stripIndent(), user, sudopass)
                node.execute command: CMD {
                    add sudopass, "echo $sudopass |"
                    add sudopass || user, "sudo -S"
                    add user, "-u $user"
                    add "sh $temp"
                }, failfast
                
                return node.lastResult
            } finally {
                node.execute command: CMD {
                    add sudopass, "echo $sudopass |"
                    add sudopass || user, "sudo -S"
                    add user, "-u $user"
                    add "rm $temp"
                }, failfast
            }
        } else {
            node.execute command: CMD {
                add sudopass, "echo $sudopass |"
                add sudopass || user, "sudo -S"
                add user, "-u $user"
                add command
            }, failfast
        }
        
        node.lastResult
    }
}
