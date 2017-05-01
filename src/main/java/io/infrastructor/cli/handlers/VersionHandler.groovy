package io.infrastructor.cli.handlers


public class VersionHandler {
    
    def options() {
        [:]
    }
    
    def usage() { 
        ["infrastructor version"] 
    }
    
    def description() { 
        "Prints version information."
    }

    def execute() { 
        println "Infrastructor ${ApplicationProperties.fullVersion()}" 
    }
}

