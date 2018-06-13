package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.provisioning.TaskExecutionException
import org.junit.Test

import static io.infrastructor.core.utils.GroovyShellUtils.load

class ApplyActionTest extends ActionTestBase {
	
    @Test
    void applyActionsDefinedInExternalClosure() {
        def the_closure = { params -> shell(params.command) }
        
        inventory.provisionAs('root') {
            task actions: {
                // load actions from an external file
                apply {
                    closure = the_closure
                    params = [command: "echo 'message' > /var/simple"]
                }
                
                // assert
                def result = shell("cat /var/simple")
                assert result.output.contains("message")
            }
        }
    }
    
    @Test
    void loadActionFromExternalFileInsideNodesSection() {
        inventory.provisionAs('root') {
            task actions: {
                def directory_action_set = load 'build/resources/test/apply_action/directory.groovy'
                apply(closure: directory_action_set, params: [target_name: '/var/simple'])
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void loadActionFromUnxesistingFile() {
        inventory.provisionAs('root') {
            task actions: {
                def closure = load 'build/resources/test/apply_action/missing.groovy'
                apply(closure: closure) {
                   params = [target_name: '/var/simple'] 
                }
            } 
        }
    }
}

