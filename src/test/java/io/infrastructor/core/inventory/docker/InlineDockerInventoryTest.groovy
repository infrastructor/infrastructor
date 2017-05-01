package io.infrastructor.core.inventory.docker

import org.testng.annotations.Test
import io.infrastructor.core.inventory.docker.InlineDockerInventory


public class InlineDockerInventoryTest {
    
    @Test
    public void createDockerNodes() {
        def inventory = InlineDockerInventory.inlineDockerInventory {
            node id: 'hostA', image: 'infrastructor/hostA', tags: [id: 'hostA'], username: 'root',   keyfile: 'resources/key.pem', password: 'hApass'
            node id: 'hostB', image: 'infrastructor/hostB', tags: [id: 'hostB'], username: 'devops', keyfile: 'resources/key.pem'
            node id: 'hostC', image: 'infrastructor/hostC', username: 'devops', password: 'hCpass'
        }
        
        assert inventory.nodes.size() == 3
        
        def hostA = inventory.nodes.find { it.id == 'hostA' }
        
        assert hostA
        hostA.with {
            assert image == 'infrastructor/hostA'
            assert username == 'root'
            assert keyfile == 'resources/key.pem'
            assert password == 'hApass'
            assert tags['id'] == 'hostA'
        }
        
        def hostB = inventory.nodes.find { it.id == 'hostB' }
        assert hostB
        hostB.with {
            assert image == 'infrastructor/hostB'
            assert username == 'devops'
            assert keyfile == 'resources/key.pem'
            assert password == null
            assert tags['id'] == 'hostB'
        }
        
        def hostC = inventory.nodes.find { it.id == 'hostC' }
        assert hostC
        hostC.with {
            assert id == 'hostC'
            assert image == 'infrastructor/hostC'
            assert username == 'devops'
            assert password == "hCpass"
        }
    }
    
    @Test
    public void launchDockerNodes() {
        def inventory = InlineDockerInventory.inlineDockerInventory {
            node image: 'infrastructor/sshd', username: 'root',   keyfile: 'resources/itest.pem'
            node image: 'infrastructor/sshd', username: 'devops', password: 'devops'
        }
        
        try {
            inventory.setup {
                nodes {
                    assert shell("ls /etc").output
                }
            }
        } finally {
            inventory.shutdown()
        }
    }
}

