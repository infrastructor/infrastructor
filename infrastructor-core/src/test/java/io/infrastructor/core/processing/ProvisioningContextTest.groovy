package io.infrastructor.core.processing

import org.junit.Test

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

class ProvisioningContextTest {
    @Test
    void simple() {
        def inventory = inlineDockerInventory {
            node image: "infrastructor/ubuntu-sshd", username: 'root', keyfile: 'build/resources/test/itest.pem'
        }
        
        try {
            ProvisioningContext.provision(inventory.launch()) {
                task name:'simple task', actions: {
                    def result = shell('ls /')
                    assert result.output.contains('etc')
                }
            }
        } finally {
            inventory.shutdown()
        }
    }
}

