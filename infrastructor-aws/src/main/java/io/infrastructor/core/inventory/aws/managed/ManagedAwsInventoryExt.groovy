package io.infrastructor.core.inventory.aws.managed

class ManagedAwsInventoryExt {
    def static managedAwsInventory(Script script, def awsAccessKey, def awsSecretKey, def awsRegion, def closure) {
        ManagedAwsInventory.managedAwsInventory(awsAccessKey, awsSecretKey, awsRegion, closure)
    }
}

