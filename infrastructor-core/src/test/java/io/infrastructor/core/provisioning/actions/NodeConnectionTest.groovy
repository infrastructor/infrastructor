package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class NodeConnectionTest extends InventoryAwareTestBase {
    
    @Test
    void useSshKeyToConnectToNode() {
        def result = [:]
        withUser('devops') { inventory ->
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
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    result = shell("echo 'test!'")
                }
            }
        }
        assert result.output.contains('test!')
    }
}

