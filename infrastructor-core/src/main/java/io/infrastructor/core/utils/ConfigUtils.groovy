package io.infrastructor.core.utils

import static io.infrastructor.core.logging.ConsoleLogger.*

class ConfigUtils {
    
    public static def config(String filepath) {
        try {
            new ConfigSlurper().parse(new File(filepath).toURI().toURL())
        } catch(Exception ex) {
            error "config :: unable to load configuration from file '$filepath'"
            debug ex.toString()
            throw ex
        }
    }
}