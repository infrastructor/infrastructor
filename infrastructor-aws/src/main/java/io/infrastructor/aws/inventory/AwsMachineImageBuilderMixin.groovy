package io.infrastructor.aws.inventory

import static io.infrastructor.core.validation.ValidationHelper.validate

class AwsMachineImageBuilderMixin {
    
    def static awsMachineImage(Script script, Closure closure) {
        awsMachineImage(script, [:], closure)
    }
    
    def static awsMachineImage(Script script, Map params) {
        awsMachineImage(script, params, {})
    }
    
    def static awsMachineImage(Script script, Map params, Closure closure) {
        AwsMachineImageBuilder awsMachineImageBuilder = new AwsMachineImageBuilder(params)
        awsMachineImageBuilder.with(closure)
        validate(awsMachineImageBuilder)
    }
}

