package io.infrastructor.core.provisioning

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
}

