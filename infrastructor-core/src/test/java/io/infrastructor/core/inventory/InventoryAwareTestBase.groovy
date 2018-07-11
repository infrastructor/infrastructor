package io.infrastructor.core.inventory

import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static InlineDockerInventory.inlineDockerInventory

@RunWith(Parameterized.class)
abstract class InventoryAwareTestBase {

    def static USERNAME = 'devops'
    def static PASSWORD = 'devops'
    def static UBUNTU_IMAGE = 'infrastructor/ubuntu-sshd:0.0.3'
    def static CENTOS_IMAGE = 'infrastructor/centos-sshd:0.0.3'

    @Parameterized.Parameter
    public Closure withUser = { user, closure -> }

    @Parameterized.Parameters
    def static inventory() {
        [{ user, closure ->
            def inventory = inlineDockerInventory {
                node id: 'test_node', image: UBUNTU_IMAGE, username: user, password: user
            }
            try {
                closure(inventory.launch())
            } finally {
                inventory.shutdown()
            }
         },
         { user, closure ->
             def inventory = inlineDockerInventory {
                 node id: 'test_node', image: CENTOS_IMAGE, username: user, password: user
             }
             try {
                 closure(inventory.launch())
             } finally {
                 inventory.shutdown()
             }
         }]
    }
}
