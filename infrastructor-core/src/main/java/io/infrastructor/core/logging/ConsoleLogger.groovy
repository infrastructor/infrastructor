package io.infrastructor.core.logging

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Color

import static io.infrastructor.core.ApplicationProperties.logLevel
import static org.fusesource.jansi.Ansi.ansi
import static org.fusesource.jansi.Ansi.Color.*

class ConsoleLogger {

    public static final int ERROR = 1
    public static final int INFO = 2
    public static final int DEBUG = 3
    
    def static statusPrinted = 0
    def static statusLoggers = []
    
    def static enableDynamics = Boolean.parseBoolean(System.getProperty("ENABLE_DYNAMIC_LOGS", "true"))
    
    def static synchronized addStatusLogger(def logger) {
        eraseStatus()
        statusLoggers.add(0, logger)
        logger.listener = { eraseAndUpdate() }
        updateStatus()
        logger
    }
    
    def static synchronized removeStatusLogger(def logger) {
        eraseStatus()
        statusLoggers.remove(logger)
        logger.listener = {}
        updateStatus()
    }
    
    def static synchronized printLine(def message, Ansi.Color color = DEFAULT) {
        eraseStatus()
        println(Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).fg(color).a(message).reset())
        updateStatus()
    }

    def static synchronized input(String message, boolean secret) {
        def console  = System.console()
        
        def inputMessage = (Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).a(message).reset()).toString()
        
        eraseStatus()
        
        def result = secret ? console.readPassword(inputMessage) : console.readLine(inputMessage)
        
        updateStatus()
        
        return (result as String)
    }
    
    def static synchronized eraseStatus() {
        if (enableDynamics) {
            while (statusPrinted > 0) {
                print(Ansi.ansi().cursorUpLine().eraseLine().reset())
                statusPrinted--
            }
        }
    }
    
    def static synchronized updateStatus() {
        if (enableDynamics) { 
            statusLoggers.each { logger ->
                logger.statusLine().eachLine { line ->
                    statusPrinted++
                    println(Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).bold().fg(DEFAULT).a(line).reset())
                }
            }
        }
    }

    def static synchronized eraseAndUpdate() {
        eraseStatus(); 
        updateStatus();
    }
    
    public static void debug(String message) {
        if (DEBUG <= logLevel()) {
            printLine(yellow(message))
        }
    }

    public static void info(String message) {
        if (INFO <= logLevel()) {
            printLine(defColor(message))
        }
    }

    public static void error(String message) {
        if (ERROR <= logLevel()) {
            printLine(red(message))
        }
    }
    
    public static final def red(String text) {
        ansi().fg(RED).a(text).reset()
    }
    
    public static final def green(String text) {
        ansi().fg(GREEN).a(text).reset()
    }
    
    public static final def yellow(String text) {
        ansi().fg(YELLOW).a(text).reset()
    }
    
    public static final def blue(String text) {
        ansi().fg(BLUE).a(text).reset()
    }
    
    public static final def magenta(String text) {
        ansi().fg(MAGENTA).a(text).reset()
    }
    
    public static final def cyan(String text) {
        ansi().fg(CYAN).a(text).reset()
    }
    
    public static final def defColor(String text) {
        ansi().fg(DEFAULT).a(text).reset()
    }
    
    public static final def bold(String text) {
        ansi().bold().a(text).reset()
    }
}

