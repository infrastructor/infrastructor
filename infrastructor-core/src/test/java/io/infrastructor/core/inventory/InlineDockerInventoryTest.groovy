package io.infrastructor.core.inventory

import org.junit.Test

import static InlineDockerInventory.inlineDockerInventory

class InlineDockerInventoryTest {
    
    @Test
    void createDockerNodes() {
        def inventory = inlineDockerInventory {
            node id: 'hostA', image: 'infrastructor/hostA', tags: [id: 'hostA'], username: 'root',   keyfile: 'resources/key.pem', password: 'hApass'
            node id: 'hostB', image: 'infrastructor/hostB', tags: [id: 'hostB'], username: 'devops', keyfile: 'resources/key.pem'
            node id: 'hostC', image: 'infrastructor/hostC',                      username: 'devops', password: 'hCpass'
        }
        
        assert inventory.size() == 3

        def hostA = inventory.nodes.values().find { it.id == 'hostA' }
        
        assert hostA
        hostA.with {
            assert image == 'infrastructor/hostA'
            assert username == 'root'
            assert keyfile == 'resources/key.pem'
            assert password == 'hApass'
            assert tags['id'] == 'hostA'
        }
        
        def hostB = inventory.nodes.values().find { it.id == 'hostB' }
        assert hostB
        hostB.with {
            assert image == 'infrastructor/hostB'
            assert username == 'devops'
            assert keyfile == 'resources/key.pem'
            assert password == null
            assert tags['id'] == 'hostB'
        }
        
        def hostC = inventory.nodes.values().find { it.id == 'hostC' }
        assert hostC
        hostC.with {
            assert id == 'hostC'
            assert image == 'infrastructor/hostC'
            assert username == 'devops'
            assert password == "hCpass"
        }
    }
    
    @Test
    void launchDockerNodes() {
        def inventory = inlineDockerInventory {
            node image: 'infrastructor/ubuntu-sshd', username: 'root',   keyfile: 'build/resources/test/itest.pem'
            node image: 'infrastructor/ubuntu-sshd', username: 'devops', password: 'devops'
        }
        
        try {
            def result = ''
            inventory.launch().provision {
                task actions: {
                    result = shell("ls /home")
                }
            }
            assert result.output.contains("devops")
        } finally {
            inventory.shutdown()
        }
    }
}

