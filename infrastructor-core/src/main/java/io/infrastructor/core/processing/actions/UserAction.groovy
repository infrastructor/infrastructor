package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

public class UserAction {
    @NotNull
    def name
    def uid
    def shell
    def home
    def sudo = false

    def execute(def node) {
        node.execute(
            command: "useradd${uid ? ' -u ' + uid : ''}${shell ? ' -s ' + shell : ''}${home ? ' -d ' + home  : ''} $name",
            sudo: sudo)
        node.lastResult
    }
}
