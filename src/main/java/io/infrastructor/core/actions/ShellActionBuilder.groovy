package io.infrastructor.core.actions


public class ShellActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new ShellActionBuilder()
        closure()
    }

    def shell(String command) {
        new ShellAction(command: command)
    }
    
    def shell(Map params) {
        shell(params, {})
    }
    
    def shell(Closure closure) {
        shell([:], closure)
    }
    
    def shell(Map params, Closure closure) {
        def action = new ShellAction(params)
        action.with(closure)
        return action
    }
}

