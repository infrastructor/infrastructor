package io.infrastructor.core.processing.actions

import org.junit.Test
import io.infrastructor.core.processing.NodeTaskExecutionException

import static io.infrastructor.core.processing.actions.Actions.*

class ApplyActionTest extends ActionTestBase {
	
    @Test
    public void loadActionFromExternalFileInsideNodesSection() {
        inventory.setup {
            nodes('as:root') {
                // load actions from an external file
                apply file: 'build/resources/test/apply_action/directory.groovy', bindings: [target_name: '/var/simple']
                
                // assert
                def result = shell("ls -dalh /var/simple")
                assert result.output.contains("simple")
                assert result.output.contains("testuser testgroup")
                assert result.output.contains("drw------")
            }
        }
    }
    
    @Test(expected = NodeTaskExecutionException)
    public void loadActionFromExternalFileOutsideNodesSection() {
        inventory.setup {
            // directory.groovy contains actions which require node in the context
            // since there is no node available in the context - exception must be thrown
            apply file: 'build/resources/test/apply_action/directory.groovy', bindings: [target_name: '/var/simple']
        }
    }
}

