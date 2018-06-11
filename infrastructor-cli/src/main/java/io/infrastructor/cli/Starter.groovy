package io.infrastructor.cli

import com.beust.jcommander.JCommander
import groovy.time.TimeCategory
import io.infrastructor.cli.handlers.*

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.utils.ExceptionUtils.deepSanitize

public class Starter {
    
    def static HANDLERS = [:]
    
    final static int EXIT_BAD_PARAMS = 1
    final static int EXIT_EXECUTION_ERROR = 2
    
    static {
        HANDLERS << ['run':     new RunHandler()]
        HANDLERS << ['encrypt': new EncryptHandler()]
        HANDLERS << ['decrypt': new DecryptHandler()]
        HANDLERS << ['version': new VersionHandler()]
        HANDLERS << ['help':    new HelpHandler(handlers: HANDLERS)]
    }

    public static void main(String [] args) {
        
        // get rid of any SLF4J error output messages
        System.setErr(new PrintStream(new OutputStream() { public void write(int b) {} }))
        
        def timeStart = new Date()
        
        try {
            if (args.length == 0) {
                HANDLERS['help'].execute()
            } else {
                def handler = HANDLERS[args.head()]
                if (!handler) {
                    printLine ''
                    error "unknown command '${args.head()}'"
                    HANDLERS['help'].execute()
                    System.exit(EXIT_BAD_PARAMS)
                } else {
                    new JCommander(handler).parse(args.tail())
                    handler.execute()
                }
            }
        } catch (Exception ex) {
            def message = ex.toString()?.replaceAll("\n", "\n ")
            
            debug " ${bold('UNCAUGHT EXCEPTION:')}"
            debug " $message"
            debug " ${bold('STACK TRACE:\n')} - ${deepSanitize(ex).replaceAll('\n', '\n - ')}"
            
            def duration = TimeCategory.minus(new Date(), timeStart)
            
            printLine "\n${bold(red('EXECUTION FAILED'))} in $duration\n"
            printLine "${red('Application has stopped due to an error:')}"
            printLine "${bold(red(message))}\n"
            printLine "${red('Please check the log output. Use \'-l 3\' command line argument to activate debug logs.')}"
            
            System.exit(EXIT_EXECUTION_ERROR)
        }
    }
}

