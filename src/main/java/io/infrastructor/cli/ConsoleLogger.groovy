package io.infrastructor.cli

import java.text.SimpleDateFormat
import java.util.Date
import org.fusesource.jansi.Ansi.Color

import static org.fusesource.jansi.Ansi.ansi
import static io.infrastructor.cli.ApplicationProperties.logLevel


public class ConsoleLogger {

    public static final int ERROR = 1
    public static final int INFO = 2
    public static final int DEBUG = 3

    public static void debug(String message) {
        if (DEBUG <= logLevel()) {
            doPrint(yellow(message))
        }
    }

    public static void info(String message) {
        if (INFO <= logLevel()) {
            doPrint(defColor(message))
        }
    }

    public static void error(String message) {
        if (ERROR <= logLevel()) {
            doPrint(red(message))
        }
    }

    private static void doPrint(def message) {
        println(message)
    }
    
    public static final def red(String text) {
        ansi().fg(Color.RED).a(text).reset()
    }
    
    public static final def green(String text) {
        ansi().fg(Color.GREEN).a(text).reset()
    }
    
    public static final def yellow(String text) {
        ansi().fg(Color.YELLOW).a(text).reset()
    }
    
    public static final def blue(String text) {
        ansi().fg(Color.BLUE).a(text).reset()
    }
    
    public static final def magenta(String text) {
        ansi().fg(Color.MAGENTA).a(text).reset()
    }
    
    public static final def cyan(String text) {
        ansi().fg(Color.CYAN).a(text).reset()
    }
    
    public static final def defColor(String text) {
        ansi().fg(Color.DEFAULT).a(text).reset()
    }
}
