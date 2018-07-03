package io.infrastructor.core.inventory

import com.jcraft.jsch.KeyPair
import org.junit.Test

import static io.infrastructor.core.inventory.InlineDockerInventory.inlineDockerInventory
import static io.infrastructor.core.inventory.SshClient.sshClient

class SshClientTest {

    def static final DEVOPS = 'devops'
    def static final ROOT = "root"
    def static final IMAGE = 'infrastructor/ubuntu-sshd:0.0.2'
    def static final KEYPATH = "build/resources/test/itest.pem"
    def static final KEYPATH_PASS = "build/resources/test/itest.pem"
    def static final PASSPHRASE = "passphrase"

    @Test
    void connectUsingPassword() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: DEVOPS, password: DEVOPS
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: node.username, password: node.password)
            assert client.connect()
        }
        finally {
            dockerNodes.shutdown()
        }
    }

    @Test
    void connectUsingKeyfile() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: ROOT, keyfile: KEYPATH
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: node.username, keyfile: node.keyfile)
            assert client.connect()
        }
        finally {
            dockerNodes.shutdown()
        }
    }

    @Test
    void connectUsingKeyfileWithPassphrase() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: DEVOPS, keypass: PASSPHRASE, keyfile: KEYPATH_PASS
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: node.username, keypass: node.keypass, keyfile: node.keyfile)
            assert client.connect()

            def result = ""
            inventory.provision {
                task actions: {
                    result = shell "whoami"
                }
            }
            assert result.output.contains(DEVOPS)
        }
        finally {
            dockerNodes.shutdown()
        }
    }
}
