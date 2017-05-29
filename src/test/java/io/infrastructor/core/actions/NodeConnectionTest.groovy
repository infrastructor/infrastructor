package io.infrastructor.core.inventory

import io.infrastructor.core.actions.TaskTestBase
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


public class NodeConnectionTest extends TaskTestBase {
    
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

