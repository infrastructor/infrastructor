package io.infrastructor.core.utils

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

class GroovyShellUtils {
    
    def static groovyShell(def bindings = [:]) {
        ImportCustomizer imports = new ImportCustomizer()
        imports.addStaticStars("io.infrastructor.core.logging.ConsoleLogger")
        imports.addStaticStars("io.infrastructor.core.logging.ConsoleInput")
        imports.addStaticStars("io.infrastructor.core.logging.ConsoleInput")
        imports.addStaticStars("io.infrastructor.core.utils.ConfigUtils")
        imports.addStaticStars("io.infrastructor.core.utils.GroovyShellUtils")
        imports.addStaticStars("io.infrastructor.core.inventory.InlineInventory")
        imports.addStaticStars("io.infrastructor.core.inventory.aws.AwsInventory")
        imports.addStaticStars("io.infrastructor.core.inventory.aws.managed.ManagedAwsInventory")
        imports.addStaticStars("io.infrastructor.core.inventory.docker.InlineDockerInventory")
        //        
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(imports)
        //        
        return new GroovyShell(new Binding(bindings), configuration)
    }
    
    def static load(String filename, def bindings = [:]) {
        groovyShell(bindings).evaluate(new File(filename))
    }
}

