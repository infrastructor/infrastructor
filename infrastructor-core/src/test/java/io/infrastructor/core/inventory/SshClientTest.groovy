package io.infrastructor.core.inventory

import com.jcraft.jsch.JSchException
import io.infrastructor.core.utils.RetryUtils
import org.junit.Test

import static io.infrastructor.core.inventory.InlineDockerInventory.inlineDockerInventory
import static io.infrastructor.core.inventory.SshClient.sshClient

class SshClientTest {

    def static final DEVOPS = 'devops'
    def static final ROOT = "root"
    def static final IMAGE = 'infrastructor/ubuntu-sshd:0.0.2'
    def static final KEYPATH = "build/resources/test/itest.pem"
    def static final KEYPATH_PASS = "build/resources/test/itest_pass.pem"
    def static final PASSPHRASE = "passphrase"

    @Test
    void connectUsingPassword() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: DEVOPS
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: node.username, password: DEVOPS)
            sleep 2000

            def result = false
            RetryUtils.retry(5, 500, {
                result = client.connect()
            })
            assert result
        }
        finally {
            dockerNodes.shutdown()
        }
    }

    @Test
    void connectUsingKeyfile() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: ROOT
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: node.username, keyfile: KEYPATH)
            def result = false
            RetryUtils.retry(5, 500, {
                result = client.connect()
            })
            assert result
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
            def result = false
            RetryUtils.retry(5, 500, {
                result = client.connect()
            })
            assert result
        }
        finally {
            dockerNodes.shutdown()
        }
    }

    @Test(expected = JSchException)
    void connectUsingKeyfileWithWrongPassphrase() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: DEVOPS
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: node.username, keypass: 'wrong', keyfile: node.keyfile)
            assert client.connect()
        } finally {
            dockerNodes.shutdown()
        }
    }

    @Test(expected = JSchException)
    void connectUsingKeyfileWithWrongUsername() {

        def dockerNodes = inlineDockerInventory {
            node id: 'docker_test_node', image: IMAGE, username: DEVOPS
        }

        try {
            def inventory = dockerNodes.launch()
            def node = inventory['docker_test_node']

            def client = sshClient(host: node.host, port: node.port, username: 'wrong', keypass: node.keypass, keyfile: node.keyfile)
            assert client.connect()
        } finally {
            dockerNodes.shutdown()
        }
    }
}
