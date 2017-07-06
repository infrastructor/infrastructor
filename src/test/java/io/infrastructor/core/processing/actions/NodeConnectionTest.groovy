package io.infrastructor.core.processing.actions

import org.junit.Test

import static io.infrastructor.core.processing.actions.Actions.*

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

