package io.infrastructor.core.tasks

import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.CommandExecutionException
import io.infrastructor.core.inventory.Node


public class UserAction {
    
    @NotNull
    def name
    def uid
    def shell
    def home
    def sudo = false
    
    def execute(Node node) {
        node.execute(
            command: "useradd${uid ? ' -u ' + uid : ''}${shell ? ' -s ' + shell : ''}${home ? ' -d ' + home  : ''} $name", 
            sudo: sudo)
    }
}

