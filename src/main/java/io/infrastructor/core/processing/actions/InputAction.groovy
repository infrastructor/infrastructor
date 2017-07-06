package io.infrastructor.core.processing.actions

import static io.infrastructor.cli.logging.ConsoleLogger.input

public class InputAction {
    
    def message = 'enter a value: '
    def secret = false
    
    def synchronized execute() {
        input(message, secret) 
    }
}