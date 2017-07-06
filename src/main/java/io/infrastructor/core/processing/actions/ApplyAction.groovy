package io.infrastructor.core.processing.actions

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

class ApplyAction {
    String file
    def bindings = [:]
    
    def execute(def node) {
        //
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
        def shell = new GroovyShell(new Binding(bindings), configuration)
        //
        def script = new File(file)
        if (!script.exists()) { throw new ActionProcessingException("file not found: $file") }
        
        shell.evaluate(script)
    }
}

