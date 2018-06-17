package io.infrastructor.aws.inventory

class AwsInventoryMixin {
    def static awsInventory(Script script, Map params) {
        awsInventory(script, params, {})
    }

    def static awsInventory(Script script, Closure definition) {
        awsInventory(script, [:], definition)
    }

    def static awsInventory(Script script, Map params, Closure definition) {
        AwsInventory.awsInventory(params, definition)
    }

    def static awsInventory(Script script,
                            def awsAccessKeyId, def awsAccessSecretKey, def awsRegion, Closure definition) {
        awsInventory(script, [
                awsAccessKeyId    : awsAccessKeyId,
                awsAccessSecretKey: awsAccessSecretKey,
                awsRegion         : awsRegion
        ], definition)
    }
}

