package io.infrastructor.cli.handlers

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.Parameter
import io.infrastructor.cli.validation.FileValidator
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

public class RunHandler extends LoggingAwareHandler {
    
    @DynamicParameter(names = ["-v", "-V", "--variable"])
    Map variables = [:]

    @Parameter(names = ["-f", "--file"], required = true, validateWith = FileValidator)
    def file
    
    def description() {
        "Run specified file."
    }
    
    
    def options() {
        def options = super.options() 
        options << ["--file, -f" : "File to run."]
        options << ["--variable, -v" : "Define a runtime variable."]
    }
    
    
    def usage() {
        ["infrastructor run -f FILE -v PROPERTY_X=VALUE_X -v PROPERTY_Y=VALUE_Y -l LOGLEVEL", 
         "infrastructor run --file FILE -v PROPERTY_X=VALUE_X -v PROPERTY_Y=VALUE_Y --log LOGLEVEL"]
    }
    
    
    def execute() {
        super.execute()
        
        try {
            ImportCustomizer importCustomizer = new ImportCustomizer()
            importCustomizer.addStaticStars("io.infrastructor.cli.ConsoleLogger")
            importCustomizer.addStaticStars("io.infrastructor.core.actions.InputAction")
            importCustomizer.addStaticStars("io.infrastructor.core.inventory.InlineInventory")
            importCustomizer.addStaticStars("io.infrastructor.core.inventory.aws.AwsInventory")
            importCustomizer.addStaticStars("io.infrastructor.core.inventory.aws.ManagedAwsInventory")
            importCustomizer.addStaticStars("io.infrastructor.core.inventory.docker.InlineDockerInventory")
                
            CompilerConfiguration configuration = new CompilerConfiguration()
            configuration.addCompilationCustomizers(importCustomizer)
                
            file.each { 
                new GroovyShell(new Binding(variables), configuration).evaluate(new File(it))
            }
        } catch (CompilationFailedException | IOException ex) {
            throw new RuntimeException(ex)
        }
    }
}

