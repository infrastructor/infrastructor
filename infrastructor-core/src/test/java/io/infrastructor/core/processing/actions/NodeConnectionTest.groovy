package io.infrastructor.core.processing.actions

import org.junit.Test

public class NodeConnectionTest extends ActionTestBase {
    
    @Test
    public void useSshKeyToConnectToNode() {
        inventory.provision {
            task filter: {"sshkey"}, actions: {
                def result = shell("echo 'test!'")
                assert result.output.contains('test!')
            }
        }
    }
    
    @Test
    public void useSshPasswordToConnectToNode() {
        inventory.provision {
            task filter: {"test"}, actions: {
                def result = shell("echo 'test!'")
                assert result.output.contains('test!')
            }
        }
    }
}

