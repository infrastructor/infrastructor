package io.infrastructor.core.processing.actions

import org.junit.Test

public class NodeConnectionTest extends ActionTestBase {
    
    @Test
    void useSshKeyToConnectToNode() {
        def result = [:]
        inventory.provisionAs('root') {
            task actions: {
                result = shell("echo 'test!'")
            }
        }
        assert result.output.contains('test!')
    }
    
    @Test
    void useSshPasswordToConnectToNode() {
        def result = [:]
        inventory.provisionAs('devops') {
            task actions: {
                result = shell("echo 'test!'")
            }
        }
        assert result.output.contains('test!')
    }
}

