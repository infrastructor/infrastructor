package io.infrastructor.cli.validation

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.ParameterException

/**
 *
 * @author Stanislav Tyurikov (stanislav.tyurikov@gmail.com)
 */
public class ModeValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        if (value != 'FULL' && value != "PART") {
            throw new ParameterException("parameter '$name' should be 'FULL' or 'PART'")
        }
    }	
}

