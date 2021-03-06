package io.infrastructor.cli.validation

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

public class LogLevelValidator implements IParameterValidator {
    @Override
    void validate(String name, String value) throws ParameterException {
        int parseInt = Integer.parseInt(value)
        if (parseInt < 0 || parseInt > 4) {
            throw new ParameterException("parameter " + name + " should be one of 0,1,2,3,4")
        }
    }
}
