package io.infrastructor.aws.inventory

class AwsMachineImageBuilderMixin {

    def static awsMachineImage(Script script, Closure closure) {
        awsMachineImage(script, [:], closure)
    }

    def static awsMachineImage(Script script, Map params) {
        awsMachineImage(script, params, {})
    }

    def static awsMachineImage(Script script, Map params, Closure closure) {
        AwsMachineImageBuilder.awsMachineImage(params, closure)
    }
}

