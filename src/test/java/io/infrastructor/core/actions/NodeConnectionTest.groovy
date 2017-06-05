package io.infrastructor.core.actions

import org.junit.Test

public class NodeConnectionTest extends ActionTestBase {
    
    @Test
    public void useSshKeyToConnectToNode() {
        inventory.setup {
            nodes("sshkey") {
                def result = shell("echo 'test!'")
                assert result.output.contains('test!')
            }
        }
    }
    
    @Test
    public void useSshPasswordToConnectToNode() {
        inventory.setup {
            nodes("test") {
                def result = shell("echo 'test!'")
                assert result.output.contains('test!')
            }
        }
    }
}

