package io.infrastructor.core.tasks

import org.testng.annotations.Test
import io.infrastructor.core.validation.ValidationException
import io.infrastructor.core.utils.FlatUUID


public class FetchActionTest extends TaskTestBase {
    
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
    
    @Test(expectedExceptions = [ValidationException])
    public void fetchFileWithEmptyArguments() {
        inventory.setup {
            nodes('as:root') {
                fetch { }
            }
        }
    }
}
