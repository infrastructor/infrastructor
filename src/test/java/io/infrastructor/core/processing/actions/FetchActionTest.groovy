package io.infrastructor.core.processing.actions

import org.junit.Test
import io.infrastructor.core.processing.TaskExecutionException
import io.infrastructor.core.utils.FlatUUID

import static io.infrastructor.core.processing.actions.Actions.*

public class FetchActionTest extends ActionTestBase {
    
    @Test
    public void fetchFileFromRemoteHost() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        inventory.setup {
            nodes('as:root') {
                file {
                    content = 'message'
                    target = '/test.txt'
                }
                
                fetch { 
                    source = '/test.txt'
                    target = resultFile
                }
            }
        }
        assert new File(resultFile).text == 'message'
    }
    
    @Test
    public void fetchFileFromRemoteHostWithoutPermissions() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        inventory.setup {
            nodes('as:devops') {
                file {
                    content = 'message'
                    target = '/test.txt'
                    owner = 'root'
                    mode = '0600'
                    sudo = true
                }

                def result = fetch { 
                    source = '/test.txt'
                    target = resultFile
                }
                
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void fetchFileFromRemoteHostWithPermissions() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        inventory.setup {
            nodes('as:devops') {
                file {
                    content = 'message'
                    target = '/test.txt'
                    owner = 'root'
                    mode = '0600'
                    sudo = true
                }
                def result = fetch { 
                    source = '/test.txt'
                    target = resultFile
                    sudo = true
                }
                assert result.exitcode == 0
            }
            assert new File(resultFile).text == 'message'
        }
    }
        
    @Test(expected = TaskExecutionException)
    public void fetchFileWithEmptyArguments() {
        inventory.setup {
            nodes('as:root') {
                fetch { }
            }
        }
    }
}
