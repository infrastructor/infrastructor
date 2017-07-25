package io.infrastructor.core.utils

import static io.infrastructor.core.logging.ConsoleLogger.*

class ConfigUtils {
    
    def static config(String filepath) {
        config(new File(normalize(filepath)))
    }
    
    def static config(File file) {
        try {
            new ConfigSlurper().parse(file.toURI().toURL())
        } catch(Exception ex) {
            error "config :: unable to load configuration from file '${file.getCanonicalPath()}'"
            debug ex.toString()
            throw ex
        }
    }
    
    def static normalize(String path) {
        new URI(path).normalize().getPath()
    }
}