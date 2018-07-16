package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD

class UserAction {
    
    @NotNull
    def name
    def uid
    def shell
    def home
    def user
    def sudopass

    def execute(def node) {
        node.execute command: CMD {
            add sudopass, "echo $sudopass |"
            add sudopass || user, "sudo -S"
            add user, "-u $user"
            add "useradd"
            add uid,   "-u $uid"
            add shell, "-s $shell"
            add home,  "-m -d $home"
            add name
        }
        
        node.lastResult
    }
}
