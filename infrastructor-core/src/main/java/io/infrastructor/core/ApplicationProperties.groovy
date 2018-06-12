package io.infrastructor.core

import static java.lang.Integer.parseInt
import static java.lang.String.valueOf
import static java.lang.System.getProperty
import static io.infrastructor.core.logging.ConsoleLogger.INFO

class ApplicationProperties {
    
    public static final String LOG_LEVEL = "LOG_LEVEL"
    
    private static final Properties properties = new Properties()

    static {
        properties.load(ApplicationProperties.class.getResourceAsStream("/build.properties"))
    }

    def static buildDate() {
        properties.getProperty("build.date")
    }
    
    def static buildRevision() {
        properties.getProperty("build.revision")
    }
    
    def static applicationVersion() {
        properties.getProperty("application.version")
    }

    def static logLevel() {
        parseInt(getProperty(LOG_LEVEL, valueOf(INFO)))
    }
}

