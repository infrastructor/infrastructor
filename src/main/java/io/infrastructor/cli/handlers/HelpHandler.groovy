package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import org.fusesource.jansi.Ansi.Color

import static org.fusesource.jansi.Ansi.ansi


public class HelpHandler {
    
    def handlers = [:]
    
    @Parameter
    List<String> commands = []
    
    def execute() {
        if (commands.size() > 0) {
            def command = commands.head()
            if (handlers.containsKey(command)) {
                println compileCommandHelp(command)
            } else {
                throw new RuntimeException("Unknown command: $command. Use 'infrastructor help' to see a list of all available commands.")
            }
        } else {
            println compileApplicationUsage()
        }
    }
    
    def usage() {
        ["infrastructor help", "infrastructor help [COMMAND]"]
    }
    
    def description() {
        "Print usage information. Use 'help [command]' for a command specific usage information."
    }
    
    def options() {
        [:]
    }
    
    def yellow(def message) {
        ansi().fg(Color.YELLOW).a(message).reset()
    }
    
    def compileCommandHelp(def command) {
        def handler = handlers[command]
        def commandHelp = new StringBuffer()
                
        commandHelp << yellow("\nCommand: ") 
        commandHelp << "$command\n"
        
        commandHelp << yellow("\nDescription:\n")
        commandHelp << "  " << handler.description() << "\n"
        
        if (handler.usage()) {
            commandHelp << yellow("\nUsage:\n")
            handler.usage().each {
                commandHelp << "  " << it << "\n"
            }
        }
        
        if (handler.options()) {
            commandHelp << yellow("\nOptions:\n")
            handler.options().each { option, description ->
                commandHelp << "  " << option.padRight(20) + description << "\n"
            }
        }
        
        return commandHelp
    }
    
    def compileApplicationUsage() {
        def applicationUsage = new StringBuffer()
        
        applicationUsage << yellow("\nUsage: ") << "infrastructor COMMAND [OPTIONS]\n"
        applicationUsage << yellow("\nCommands:\n")
        handlers.each { name, handler ->
            applicationUsage << "  " << name.padRight(12) + handler.description() << "\n"
        }
        
        return applicationUsage
    }
}

