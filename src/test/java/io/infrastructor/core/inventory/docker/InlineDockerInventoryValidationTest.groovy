package io.infrastructor.core.inventory.docker

import org.testng.annotations.Test
import io.infrastructor.core.inventory.docker.InlineDockerInventory
import io.infrastructor.core.validation.ValidationException


public class InlineDockerInventoryValidationTest {
    
    @Test(expectedExceptions = [ValidationException])
    public void validateDockerNodesImageMayNotBeNull() {
        InlineDockerInventory.inlineDockerInventory {
            node tags: [tag: 'hostA'], username: 'root', password: 'infra', keyfile: 'resources/itest.pem' 
        }
    }
    
    @Test(expectedExceptions = [ValidationException])
    public void validateDockerNodesUserMayNotBeNull() {
        InlineDockerInventory.inlineDockerInventory {
            node image: 'infrastructor/sshd', tags: [tag: 'hostA'], password: 'infra', keyfile: 'resources/itest.pem' 
        }
    }
}

