package io.infrastructor.core.validation

import io.infrastructor.core.validation.ValidationException
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

import static io.infrastructor.cli.ConsoleLogger.error


public class ValidationHelper {

    public static def validate(def objectToValidate) throws ValidationException {
        def validator = Validation.buildDefaultValidatorFactory().getValidator()
        def validationResult = validator.validate(objectToValidate)
        
        if (!validationResult.isEmpty()) {
            StringBuffer message = new StringBuffer("\n") 
            
            validationResult.each { ConstraintViolation v ->
                message.append("${objectToValidate.class.simpleName} validation error: '${v.propertyPath}' ${v.message}\n")
            }
            
            error("validation failed: " + message.toString())
            throw new ValidationException("validation failed! " + message.toString())
        } 
        
        objectToValidate
    }
}