package io.infrastructor.core.validation

import javax.validation.ConstraintViolation
import javax.validation.Validation

class ValidationHelper {

    static def validate(def objectToValidate) throws ValidationException {
        def validationResult = Validation.buildDefaultValidatorFactory().getValidator().validate(objectToValidate)
        
        if (!validationResult.isEmpty()) {
            def result = [:]
            validationResult.each { ConstraintViolation v ->
                result << ["${objectToValidate.class.simpleName}" : "$v.propertyPath - $v.message"]
            }
            throw new ValidationException(result as String)
        } 
        
        objectToValidate
    }
}