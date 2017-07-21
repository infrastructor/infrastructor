package io.infrastructor.core.inventory.docker

import org.junit.Test
import io.infrastructor.core.inventory.docker.InlineDockerInventory
import io.infrastructor.core.validation.ValidationException

public class InlineDockerInventoryValidationTest {
    
    @Test(expected = ValidationException)
    public void validateDockerNodesImageMayNotBeNull() {
        InlineDockerInventory.inlineDockerInventory {
            node tags: [tag: 'hostA'], username: 'root', password: 'infra', keyfile: 'build/resources/test/itest.pem' 
        }
    }
    
    @Test(expected = ValidationException)
    public void validateDockerNodesUserMayNotBeNull() {
        InlineDockerInventory.inlineDockerInventory {
            node image: 'infrastructor/sshd', tags: [tag: 'hostA'], password: 'infra', keyfile: 'build/resources/test/itest.pem' 
        }
    }
}

