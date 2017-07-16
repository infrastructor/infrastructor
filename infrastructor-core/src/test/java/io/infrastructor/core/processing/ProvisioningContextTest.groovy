package io.infrastructor.core.processing

import org.junit.Test

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

class ProvisioningContextTest {
    @Test
    public void simple() {
        def inventory = inlineDockerInventory {
            node image: "infrastructor/ubuntu-sshd", username: 'root', keyfile: 'resources/itest.pem'
        }
        
        try {
            ProvisioningContext.provision(inventory.launch()) {
                task('simple task') {
                    def result = shell('ls /')
                    assert result.output.contains('etc')
                }
            }
        } finally {
            inventory.shutdown()
        }
    }
}

