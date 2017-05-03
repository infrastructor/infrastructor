package io.infrastructor.cli

import com.beust.jcommander.JCommander
import io.infrastructor.cli.handlers.HelpHandler
import io.infrastructor.cli.handlers.RunHandler
import io.infrastructor.cli.handlers.VersionHandler
import static io.infrastructor.cli.ConsoleLogger.error


public class Starter {
    
    def static HANDLERS = [:]
    
    static {
        HANDLERS << ['run': new RunHandler()]
        HANDLERS << ['version': new VersionHandler()]
        HANDLERS << ['help': new HelpHandler(handlers: HANDLERS)]
    }

    public static void main(String [] args) {
        if (args.length == 0) {
            HANDLERS['help'].execute()
        } else {
            def handler = HANDLERS[args.head()]
            if (handler) {
                error "Uknown command '${args.head()}'"
                HANDLERS['help'].execute()
            } else {
                try {
                    new JCommander(handler).parse(args.tail())
                    handler.execute()
                } catch (Exception ex) {
                    error ex.getMessage()
                }
            }
        }
    }
}

