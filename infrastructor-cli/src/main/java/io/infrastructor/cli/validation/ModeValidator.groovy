package io.infrastructor.cli.validation

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

public class ModeValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        if (value?.toUpperCase() != 'FULL' && value?.toUpperCase() != "PART") {
            throw new ParameterException("parameter '$name' should be 'FULL' or 'PART'")
        }
    }	
}

