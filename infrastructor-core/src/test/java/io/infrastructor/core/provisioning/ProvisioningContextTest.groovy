package io.infrastructor.core.provisioning

import io.infrastructor.core.inventory.BasicInventory
import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class ProvisioningContextTest extends InventoryAwareTestBase {
    @Test
    void provisionASingleNodeInventory() {
        withUser(DEVOPS) { inventory ->
            ProvisioningContext.provision(inventory) {
                task name: 'test task', actions: {
                    def result = shell 'ls /'
                    assert result.output.contains('etc')
                }
            }
        }
    }

    @Test
    void checkInventoryIsAvailableInContext() {
        withUser(DEVOPS) { inventory ->
            def size = 0
            ProvisioningContext.provision(inventory) { BasicInventory provisioningInventory ->
                size = provisioningInventory.size()
            }
            assert size == 1
        }
    }
}

