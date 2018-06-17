package io.infrastructor.core.inventory

class CommandBuilder {
    
    def command = []
    
    def add(def part) {
        command << part
        this
    }
    
    def add(def condition, def part) {
        if (condition) { add part }
        this
    }
    
    def build() {
        command.join(' ') 
    }
    
    def static CMD(def closure) {
        def builder = new CommandBuilder()
        closure.delegate = builder
        closure()
        builder.build()
    }
}