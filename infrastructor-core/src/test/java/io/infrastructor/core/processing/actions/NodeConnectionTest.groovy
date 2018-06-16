package io.infrastructor.core.processing.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class NodeConnectionTest extends InventoryAwareTestBase {
    
    @Test
    void useSshKeyToConnectToNode() {
        def result = [:]
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    result = shell(user: 'root', command: "echo 'test!'")
                }
            }
        }
        assert result.output.contains('test!')
    }
    
    @Test
    void useSshPasswordToConnectToNode() {
        def result = [:]
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    result = shell("echo 'test!'")
                }
            }
        }
        assert result.output.contains('test!')
    }
}

