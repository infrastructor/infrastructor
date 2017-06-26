package io.infrastructor.core.processing2.actions

import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.inventory.Node

public class UserAction extends AbstractNodeAction {
    
    @NotNull
    def name
    def uid
    def shell
    def home
    def sudo = false
    
    def execute() {
        node.execute(
            command: "useradd${uid ? ' -u ' + uid : ''}${shell ? ' -s ' + shell : ''}${home ? ' -d ' + home  : ''} $name", 
            sudo: sudo)
    }
}

