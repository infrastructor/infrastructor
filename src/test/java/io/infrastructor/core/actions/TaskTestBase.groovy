package io.infrastructor.core.actions

import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod

import io.infrastructor.core.inventory.docker.InlineDockerInventory


public abstract class TaskTestBase {
    
    def inventory
    
    @BeforeMethod
    public void setup() {
        inventory = InlineDockerInventory.inlineDockerInventory {
            node image: 'infrastructor/ubuntu-sshd', tags: ['as': 'root'],   username: 'root',   keyfile: 'resources/itest.pem'
            node image: 'infrastructor/ubuntu-sshd', tags: ['as': 'devops'], username: 'devops', password: 'devops'
        }
    }

    @AfterMethod
    public void teardown() {
        inventory.shutdown()
    }
}
