package io.infrastructor.cli

import com.beust.jcommander.JCommander
import groovy.time.TimeCategory
import io.infrastructor.cli.handlers.DecryptHandler
import io.infrastructor.cli.handlers.EncryptHandler
import io.infrastructor.cli.handlers.HelpHandler
import io.infrastructor.cli.handlers.RunHandler
import io.infrastructor.cli.handlers.VersionHandler

import static io.infrastructor.core.utils.ExceptionUtils.deepSanitize
import static io.infrastructor.core.logging.ConsoleLogger.*

public class Starter {
    
    def static HANDLERS = [:]
    
    static {
        HANDLERS << ['run':     new RunHandler()]
        HANDLERS << ['encrypt': new EncryptHandler()]
        HANDLERS << ['decrypt': new DecryptHandler()]
        HANDLERS << ['version': new VersionHandler()]
        HANDLERS << ['help':    new HelpHandler(handlers: HANDLERS)]
    }

    public static void main(String [] args) {
        
        // get rid of any SLF4J error output messages
        System.setErr(new PrintStream(
                new OutputStream() {
                    public void write(int b) {
                    }
                }));
        
        def timeStart = new Date()
        
        try {
            if (args.length == 0) {
                HANDLERS['help'].execute()
            } else {
                def handler = HANDLERS[args.head()]
                if (!handler) {
                    error "Unknown command '${args.head()}'"
                    HANDLERS['help'].execute()
                } else {
                    new JCommander(handler).parse(args.tail())
                    handler.execute()
                }
                
                def duration = TimeCategory.minus(new Date(), timeStart)
                info "\n${green('EXECUTION COMPLETE')} in $duration"
            }
        } catch (Exception ex) {
            def message = ex.toString()?.replaceAll("\n", "\n ")
            
            debug " ${bold('Uncaught exception:')}"
            debug " ${ex.class.name}: $message"
            debug " ${bold('stack trace:')}"
            debug (" - ${deepSanitize(ex)}".replaceAll("\n", "\n - "))
            
            def duration = TimeCategory.minus(new Date(), timeStart)
            
            error "\n${bold('EXECUTION FAILED')} in $duration"
            error "Application has stopped due to an error: ${bold(message)}"
            error "Please check the log output. Use '-l 3' command line argument to activate debug logs."
        }
    }
}

