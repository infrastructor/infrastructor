package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class WaitForPortTest extends InventoryAwareTestBase {
    @Test(expected = TaskExecutionException)
    void waitForUnlistenedPort() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    waitForPort port: 10000
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void waitForUnknownPort() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    waitForPort delay: 100, attempts: 3
                }
            }
        }
    }
}

