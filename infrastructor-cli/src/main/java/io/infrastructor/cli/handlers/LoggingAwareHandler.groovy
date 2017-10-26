package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.validation.LogLevelValidator
import io.infrastructor.core.logging.ConsoleLogger

import static io.infrastructor.core.ApplicationProperties.LOG_LEVEL
import static java.lang.String.valueOf


public class LoggingAwareHandler {
    
    @Parameter(names = ["-l", "--log"], validateWith = LogLevelValidator)
    int logLevel = ConsoleLogger.INFO
    
    def options() {
        ["--log, -l" : "Specify a log level: 0 - OFF, 1 - ERROR, 2 - INFO, 3 - DEBUG, 4 - TRACE."]
    }
    
    def execute() {
        System.setProperty(LOG_LEVEL, valueOf(logLevel))
    }
}

