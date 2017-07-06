package io.infrastructor.core.utils

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

class GroovyShellUtils {
    def static createDefaultShell(def bindings = [:]) {
        ImportCustomizer imports = new ImportCustomizer()
        imports.addStaticStars("io.infrastructor.cli.logging.ConsoleLogger")
        imports.addStaticStars("io.infrastructor.core.utils.ConfigUtils")
        imports.addStaticStars("io.infrastructor.core.inventory.InlineInventory")
        imports.addStaticStars("io.infrastructor.core.inventory.aws.AwsInventory")
        imports.addStaticStars("io.infrastructor.core.inventory.aws.managed.ManagedAwsInventory")
        imports.addStaticStars("io.infrastructor.core.inventory.docker.InlineDockerInventory")
        imports.addStaticStars("io.infrastructor.core.processing.actions.Actions")
        imports.addStaticStars("io.infrastructor.core.processing.actions.InputAction")
        //        
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(imports)
        //        
        return new GroovyShell(new Binding(bindings), configuration)
    }
}

