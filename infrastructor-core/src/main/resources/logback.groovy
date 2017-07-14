appender("CONSOLEGREEN", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        // %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
        pattern = "%d{HH:mm:ss.SSS} - %highlight(%msg%n)"
    }
}

root(OFF, ["CONSOLEGREEN"])
