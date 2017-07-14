package io.infrastructor.core

import static java.lang.Integer.parseInt
import static java.lang.String.valueOf
import static java.lang.System.getProperty


public class ApplicationProperties {
    
    public static final int ERROR = 1
    public static final int INFO = 2
    public static final int DEBUG = 3
    public static final String LOG_LEVEL = "LOG_LEVEL"
    
    private static final Properties properties = new Properties()

    static {
        properties.load(ApplicationProperties.class.getResourceAsStream("/build.properties"))
    }

    public static String buildDate() {
        properties.getProperty("build.date")
    }
    
    public static String buildRevision() {
        properties.getProperty("build.revision")
    }
    
    public static String applicationVersion() {
        properties.getProperty("application.version")
    }

    public static final int logLevel() {
        parseInt(getProperty(LOG_LEVEL, valueOf(INFO)))
    }
}

