package io.infrastructor.core.processing.actions

import org.junit.Test
import io.infrastructor.core.processing.NodeTaskExecutionException

import static io.infrastructor.core.utils.GroovyShellUtils.load

class ApplyActionTest extends ActionTestBase {
	
    @Test
    public void applyActionsDefinedInExternalClosure() {
        def the_closure = { params -> shell(params.command) }
        
        inventory.provision {
            task(filter: {'as:root'}) {
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
    public void loadActionFromExternalFileInsideNodesSection() {
        inventory.provision {
            task(filter: {'as:root'}) {
                def directory_action_set = load 'build/resources/test/apply_action/directory.groovy'
                apply(closure: directory_action_set, params: [target_name: '/var/simple'])
            }
        }
    }
    
    @Test(expected = Exception)
    public void loadActionFromUnxesistingFile() {
        inventory.provision {
            task(filter: {'as:root'}) {
                def closure = load 'build/resources/test/apply_action/missing.groovy'
                apply(closure: closure) {
                   params = [target_name: '/var/simple'] 
                }
            } 
        }
    }
}

