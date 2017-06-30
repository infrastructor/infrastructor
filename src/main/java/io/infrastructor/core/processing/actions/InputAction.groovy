package io.infrastructor.core.processing.actions

import org.fusesource.jansi.Ansi

public class InputAction {
    
    def message = 'enter a value: '
    def secret = false
    
    synchronized def execute() {
        def console  = System.console()
        
        def inputMessage = (Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).a(message).reset()).toString()
        
        if (secret) {
            return console.readPassword(inputMessage) 
        } else {
            return console.readLine(inputMessage)
        }
    }
    
    def static input(Map params) {
        input(params, {})
    }
    
    def static input(Closure closure) {
        input([:], closure)
    }
    
    def static input(Map params, Closure closure) {
        def action = new InputAction(params)
        action.with(closure)
        action.execute()
    }
}

