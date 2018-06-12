package io.infrastructor.cli.validation

import com.beust.jcommander.ParameterException
import org.junit.Test

class ModeValidationTest {
    @Test
    void passCorrectMode() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "FULL")
        validator.validate("", "PART")
        validator.validate("", "full")
        validator.validate("", "fuLL")
        validator.validate("", "part")
        validator.validate("", "PArt")
    }
    
    @Test(expected = ParameterException)
    void passIncorrectMode_empty() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "")
    }
    
    @Test(expected = ParameterException)
    void passIncorrectMode_null() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", null)
    }
    
    @Test(expected = ParameterException)
    void passIncorrectMode_something() {
        ModeValidator validator = new ModeValidator()
        validator.validate("", "dummy")
    }
}

