package io.infrastructor.core.logging

import static io.infrastructor.core.logging.ConsoleLogger.input

public class InputAction {
    
    def message = 'enter a value: '
    def secret = false
    
    def synchronized execute() {
        input(message, secret) 
    }
}