package io.infrastructor.cli.validation

import com.beust.jcommander.ParameterException
import org.junit.Test

class ModeValidationTest {
    @Test
    public void passCorrectMode() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "FULL")
        validator.validate("", "PART")
        validator.validate("", "full")
        validator.validate("", "fuLL")
        validator.validate("", "part")
        validator.validate("", "PArt")
    }
    
    @Test(expected = ParameterException)
    public void passIncorrectMode_empty() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "")
    }
    
    @Test(expected = ParameterException)
    public void passIncorrectMode_null() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "")
    }
    
    @Test(expected = ParameterException)
    public void passIncorrectMode_something() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "dummy")
    }
}

