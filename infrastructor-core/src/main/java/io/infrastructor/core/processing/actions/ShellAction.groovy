package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

class ShellAction {
    
    @NotNull
    def command
    def user
    
    def execute(def node) {
        if (command.contains("\n")) {

            def result = node.execute command: CMD {
                add user, "sudo -s -u $user"
                add "mktemp"
            }
            
            def temp = result.output.trim()

            try {
                node.writeText(temp, command.stripIndent(), user)
                node.execute command: CMD {
                    add user, "sudo -s -u $user"
                    add "sh $temp"
                }
                
                return node.lastResult
            } finally {
                node.execute command: CMD {
                    add user, "sudo -s -u $user"
                    add "rm $temp"
                }
            }
        } else {
            node.execute command: CMD {
                add user, "sudo -s -u $user"
                add command
            }
        }
        
        node.lastResult
    }
}
