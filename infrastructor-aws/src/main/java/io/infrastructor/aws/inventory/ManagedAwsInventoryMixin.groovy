package io.infrastructor.aws.inventory

class ManagedAwsInventoryMixin {
    def static managedAwsInventory(Script script, def awsAccessKey, def awsSecretKey, def awsRegion, def closure) {
        ManagedAwsInventory.managedAwsInventory(awsAccessKey, awsSecretKey, awsRegion, closure)
    }
}

