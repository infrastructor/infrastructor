package io.infrastructor.core.utils

import io.infrastructor.core.validation.ValidationException
import static io.infrastructor.core.validation.ValidationHelper.validate

class ActionRegistrationUtils {

    def static action(String name, Closure closure) {
        action name: name, closure: closure
    }

    def static action(Map params) {
        action(params, {})
    }
    
    def static action(Closure closure) {
        action([:], closure)
    }
    
    def static action(Map params, Closure closure) {
        try {
            def registration = new ActionRegistration(params)
            registration.with(closure)
            validate(registration)
            registration.execute()
        } catch (ValidationException ex) {
            throw new ActionRegistrationException(ex.getMessage(), ex)
        }
    }
}

