package io.infrastructor.cli.validation

import com.beust.jcommander.ParameterException
import org.junit.Test

class FileValidatorTest {
    @Test
    void passExistingFile() {
        FileValidator validator = new FileValidator()
        validator.validate("", "build/resources/test/validation/existing.file")
    }
    
    @Test(expected = ParameterException)
    void passMissingFile() {
        FileValidator validator = new FileValidator()
        validator.validate("", "missing.file")
    }
}

