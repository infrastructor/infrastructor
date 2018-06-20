package io.infrastructor.core.logging

import org.fusesource.jansi.Ansi

import static io.infrastructor.core.ApplicationProperties.logLevel
import static org.fusesource.jansi.Ansi.Color.*
import static org.fusesource.jansi.Ansi.ansi

class ConsoleLogger {

    static final int ERROR = 1
    static final int INFO  = 2
    static final int DEBUG = 3
    static final int TRACE = 4
    
    def static statusPrinted = 0
    def static statusLoggers = []
    
    def static enableDynamics = Boolean.parseBoolean(System.getProperty("ENABLE_DYNAMIC_LOGS", "true"))
    
    def static synchronized addStatusLogger(def logger) {
        eraseStatus()
        statusLoggers.add(logger)
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
        eraseStatus()
        updateStatus()
    }
    
    def static synchronized printLine(def message, Ansi.Color color = DEFAULT) {
        eraseStatus()
        println(Ansi.ansi().cursorToColumn(0).fg(color).a(message).reset())
        updateStatus()
    }

    
    static void trace(String message) {
        if (TRACE <= logLevel()) {
            printLine("[TRACE] " + defColor(message))
        }
    }
    
    static void debug(String message) {
        if (DEBUG <= logLevel()) {
            printLine "${yellow("[DEBUG]")} $message"
        }
    }

    static void info(String message) {
        if (INFO <= logLevel()) {
            printLine "${blue("[INFO]")} $message"
        }
    }

    static void error(String message) {
        if (ERROR <= logLevel()) {
            printLine red("[ERROR] $message")
        }
    }
    
    def static final red(String text) {
        ansi().fg(RED).a(text).reset()
    }
    
    def static final green(String text) {
        ansi().fg(GREEN).a(text).reset()
    }
    
    def static final yellow(String text) {
        ansi().fg(YELLOW).a(text).reset()
    }
    
    def static final blue(String text) {
        ansi().fg(BLUE).a(text).reset()
    }
    
    def static final magenta(String text) {
        ansi().fg(MAGENTA).a(text).reset()
    }
    
    def static final cyan(String text) {
        ansi().fg(CYAN).a(text).reset()
    }
    
    def static final defColor(String text) {
        ansi().fg(DEFAULT).a(text).reset()
    }
    
    def static final bold(String text) {
        ansi().bold().a(text).reset()
    }
    
    def static final bold(def text) {
        ansi().bold().a("$text").reset()
    }
}

