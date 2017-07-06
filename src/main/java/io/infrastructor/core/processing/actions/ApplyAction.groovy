package io.infrastructor.core.processing.actions

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import static io.infrastructor.core.utils.GroovyShellUtils.createDefaultShell

class ApplyAction {
    String file
    def bindings = [:]
    
    def execute(def node) {
        def script = new File(file)
        if (!script.exists()) { throw new ActionProcessingException("file not found: $file") }
        
        def shell = createDefaultShell(bindings)
        shell.evaluate(script)
    }
}

