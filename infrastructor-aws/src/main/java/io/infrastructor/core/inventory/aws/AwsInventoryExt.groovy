package io.infrastructor.core.inventory.aws

class AwsInventoryExt {
    def static awsInventory(Script script, def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        AwsInventory.awsInventory(awsAccessKey, awsSecretKey, awsRegion, definition)
    }
}

