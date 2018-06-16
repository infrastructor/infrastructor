package io.infrastructor.core.processing

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class ProvisioningContextTest extends InventoryAwareTestBase {
    @Test
    void provisionASingleNodeInventory() {
        withInventory { inventory ->
            ProvisioningContext.provision(inventory.nodes) {
                task name: 'test task', actions: {
                    def result = shell 'ls /'
                    assert result.output.contains('etc')
                }
            }
        }
    }
}

