package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.provisioning.TaskExecutionException
import io.infrastructor.core.utils.FlatUUID
import org.junit.Test

public class FetchActionTest extends ActionTestBase {
    
    @Test
    public void fetchFileFromRemoteHost() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        inventory.provisionAs('root') {
            task actions: {
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
        inventory.provisionAs('devops') {
            task actions: {
                file {
                    content = 'message'
                    target = '/test.txt'
                    owner = 'root'
                    mode = '0600'
                    user = 'root'
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
        inventory.provisionAs('devops') {
            task actions: {
                file {
                    content = 'message'
                    target = '/test.txt'
                    owner = 'root'
                    mode = '0600'
                    user = 'root'
                }
                def result = fetch { 
                    source = '/test.txt'
                    target = resultFile
                    user = 'root'
                }
                assert result.exitcode == 0
            }
            assert new File(resultFile).text == 'message'
        }
    }
        
    @Test(expected = TaskExecutionException)
    public void fetchFileWithEmptyArguments() {
        inventory.provisionAs('root') {
            task actions: {
                fetch { }
            }
        }
    }
}
