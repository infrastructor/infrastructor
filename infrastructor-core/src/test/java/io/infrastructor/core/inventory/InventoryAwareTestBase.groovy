package io.infrastructor.core.inventory

import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static InlineDockerInventory.inlineDockerInventory

@RunWith(Parameterized.class)
abstract class InventoryAwareTestBase {

    def static USERNAME = 'devops'
    def static PASSWORD = 'devops'

    @Parameterized.Parameter
    public Closure withInventory = {}

    @Parameterized.Parameters
    def static inventory() {
        [{ closure ->
            def dockerNodes = inlineDockerInventory {
                node id: 'docker_test_node', image: 'infrastructor/ubuntu-sshd', username: USERNAME, password: PASSWORD
            }
            try {
                closure(dockerNodes.launch())
            } finally {
                dockerNodes.shutdown()
            }
         },
         { closure ->
             def dockerNodes = inlineDockerInventory {
                 node id: 'docker_test_node', image: 'infrastructor/centos-sshd', username: USERNAME, password: PASSWORD
             }

             try {
                 closure(dockerNodes.launch())
             } finally {
                 dockerNodes.shutdown()
             }
         }]
    }
}
