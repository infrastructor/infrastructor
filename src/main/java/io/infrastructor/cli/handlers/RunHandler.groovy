package io.infrastructor.cli.handlers

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.LogLevelValidator
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import static io.infrastructor.cli.ApplicationProperties.LOG_LEVEL
import static java.lang.String.valueOf


public class RunHandler {
    
    @DynamicParameter(names = ["-v", "-V", "--variables"])
    Map variables = [:]

    @Parameter(names = ["-f", "--file"], required = true, validateWith = FileValidator)
    def file

    @Parameter(names = ["-l", "--log"], validateWith = LogLevelValidator)
    int logLevel = ConsoleLogger.INFO
    
    def usage() {
        ["infrastructor run -f FILE -v PROPERTY_X=VALUE_X -v PROPERTY_Y=VALUE_Y -l LOGLEVEL", 
        "infrastructor run --file FILE -v PROPERTY_X=VALUE_X -v PROPERTY_Y=VALUE_Y --log LOGLEVEL"]
    }
    
    def options() {
        ["--file, -f" : "File to run.",
         "--variable, -v" : "Define a runtime variable.",
         "--log, -l" : "Specify a log level: 0 - OFF, 1 - ERROR, 2 - INFO, 3 - DEBUG"]
    }
    
    def description() {
        "Run specified file."
    }
    
    def execute() {
        try {
            System.setProperty(LOG_LEVEL, valueOf(logLevel))
                
            ImportCustomizer importCustomizer = new ImportCustomizer()
            importCustomizer.addStaticStars("io.infrastructor.cli.ConsoleLogger")
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

