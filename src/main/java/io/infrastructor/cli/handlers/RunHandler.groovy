package io.infrastructor.cli.handlers

import com.beust.jcommander.DynamicParameter
import com.beust.jcommander.Parameter
import io.infrastructor.cli.validation.FileValidator

import static io.infrastructor.core.utils.GroovyShellUtils.createDefaultShell

public class RunHandler extends LoggingAwareHandler {
    
    @DynamicParameter(names = ["-v", "-V", "--variable"])
    Map variables = [:]

    @Parameter(names = ["-f", "--file"], required = true, validateWith = FileValidator)
    List<String> files
    
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
        
        def shell = createDefaultShell(variables)
        
        files.each { shell.evaluate(new File(it)) }
    }
}

