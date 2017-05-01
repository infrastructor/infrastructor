package io.infrastructor.cli.validation

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException


public class FileValidator implements IParameterValidator {
    
    @Override
    public void validate(String name, String value) throws ParameterException {
        def file = new File(value)
        if (!file.exists()) {
            throw new ParameterException("parameter '$name' should reference to an existing file")
        }
    }	
}

