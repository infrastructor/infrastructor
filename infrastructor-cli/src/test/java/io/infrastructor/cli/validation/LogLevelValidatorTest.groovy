package io.infrastructor.cli.validation

import com.beust.jcommander.ParameterException
import org.junit.Test

class LogLevelValidatorTest {
    @Test
    void passCorrectLogLevelAsInteger() {
        LogLevelValidator validator = new LogLevelValidator()
        validator.validate("", "0")
        validator.validate("", "1")
        validator.validate("", "2")
        validator.validate("", "3")
        validator.validate("", "4")
    }
    
    @Test(expected = ParameterException)
    void passIncorrectLogLevelAsInteger_minus_one() {
        LogLevelValidator validator = new LogLevelValidator()
        validator.validate("", "-1")
    }
    
    @Test(expected = ParameterException)
    void passIncorrectLogLevelAsInteger_four() {
        LogLevelValidator validator = new LogLevelValidator()
        validator.validate("", "5")
    }
}

