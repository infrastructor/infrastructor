package io.infrastructor.core.provisioning

import io.infrastructor.core.inventory.Inventory
import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class ProvisioningContextTest extends InventoryAwareTestBase {
    @Test
    void provisionASingleNodeInventory() {
        withInventory { inventory ->
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
        withInventory { inventory ->
            def size = 0
            ProvisioningContext.provision(inventory) { Inventory provisioningInventory ->
                size = provisioningInventory.size()
            }
            assert size == 1
        }
    }
}

