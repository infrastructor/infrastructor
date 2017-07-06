package io.infrastructor.core.processing.actions

import org.fusesource.jansi.Ansi

public class InputAction {
    
    def message = 'enter a value: '
    def secret = false
    
    def synchronized execute() {
        def console  = System.console()
        
        def inputMessage = (Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).a(message).reset()).toString()
        
        if (secret) {
            return console.readPassword(inputMessage) 
        } else {
            return console.readLine(inputMessage)
        }
    }
}