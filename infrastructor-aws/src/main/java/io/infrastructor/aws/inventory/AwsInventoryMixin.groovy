package io.infrastructor.aws.inventory

class AwsInventoryMixin {
    def static awsInventory(Script script, def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        AwsInventory.awsInventory(awsAccessKey, awsSecretKey, awsRegion, definition)
    }
}

