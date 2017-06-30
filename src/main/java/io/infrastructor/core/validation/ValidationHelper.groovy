package io.infrastructor.core.validation

import io.infrastructor.core.validation.ValidationException
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

public class ValidationHelper {

    public static def validate(def objectToValidate) throws ValidationException {
        def validationResult = Validation.buildDefaultValidatorFactory().getValidator().validate(objectToValidate)
        
        if (!validationResult.isEmpty()) {
            def result = [:]
            validationResult.each { ConstraintViolation v ->
                result << ["${objectToValidate.class.simpleName}" : "$v.propertyPath - $v.message"]
            }
            throw new ValidationException(result)
        } 
        
        objectToValidate
    }
}