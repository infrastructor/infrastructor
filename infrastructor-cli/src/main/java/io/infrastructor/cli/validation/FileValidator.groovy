package io.infrastructor.cli.validation

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

public class FileValidator implements IParameterValidator {
    @Override
    void validate(String name, String value) throws ParameterException {
        if (!new File(value).exists()) {
            throw new ParameterException("message: file does not exists, parameter: '$name', value: $value")
        }
    }	
}

