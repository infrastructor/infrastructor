package io.infrastructor.core.inventory.docker

import io.infrastructor.core.validation.ValidationException
import org.junit.Test

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

class InlineDockerInventoryValidationTest {
    
    @Test(expected = ValidationException)
    void validateDockerNodesImageMayNotBeNull() {
        inlineDockerInventory {
            node tags: [tag: 'hostA'], username: 'root', password: 'infra', keyfile: 'build/resources/test/itest.pem' 
        }
    }
    
    @Test(expected = ValidationException)
    void validateDockerNodesUserMayNotBeNull() {
        inlineDockerInventory {
            node image: 'infrastructor/sshd', tags: [tag: 'hostA'], password: 'infra', keyfile: 'build/resources/test/itest.pem' 
        }
    }
}

