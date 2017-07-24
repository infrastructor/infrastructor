package io.infrastructor.core.inventory.docker

import org.junit.Test
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

public class InlineDockerInventoryValidationTest {
    
    @Test(expected = ValidationException)
    public void validateDockerNodesImageMayNotBeNull() {
        inlineDockerInventory {
            node tags: [tag: 'hostA'], username: 'root', password: 'infra', keyfile: 'build/resources/test/itest.pem' 
        }
    }
    
    @Test(expected = ValidationException)
    public void validateDockerNodesUserMayNotBeNull() {
        inlineDockerInventory {
            node image: 'infrastructor/sshd', tags: [tag: 'hostA'], password: 'infra', keyfile: 'build/resources/test/itest.pem' 
        }
    }
}

