package io.infrastructor.core.inventory

import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

@RunWith(Parameterized.class)
abstract class InventoryAwareTestBase {

    def static USERNAME = 'devops'
    def static PASSWORD = 'devops'
    def static TIMEOUT  = 500

    @Parameterized.Parameter
    public Closure withInventory = { }

    @Parameterized.Parameters
    def static inventory() {
        [{ closure ->
            def dockerNodes = inlineDockerInventory {
                node id: 'docker_test_node', image: 'infrastructor/ubuntu-sshd', username: USERNAME, password: PASSWORD
            }

            closure(dockerNodes.launch(TIMEOUT))
            dockerNodes.shutdown()
         },
         { closure ->
            def dockerNodes = inlineDockerInventory {
                node id: 'docker_test_node', image: 'infrastructor/centos-sshd', username: USERNAME, password: PASSWORD
            }

            closure(dockerNodes.launch(TIMEOUT))
            dockerNodes.shutdown()
         }]
    }
}
