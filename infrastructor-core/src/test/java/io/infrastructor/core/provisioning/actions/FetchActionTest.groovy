package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import io.infrastructor.core.utils.FlatUUID
import org.junit.Test

class FetchActionTest extends InventoryAwareTestBase {
    
    @Test
    void fetchFileFromRemoteHost() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        content = 'message'
                        target = '/test.txt'
                    }

                    fetch {
                        user = 'root'
                        source = '/test.txt'
                        target = resultFile
                    }
                }
            }
        }

        assert new File(resultFile).text == 'message'
    }
    
    @Test(expected = TaskExecutionException)
    void fetchFileFromRemoteHostWithoutPermissions() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        withUser('devops') { inventory ->
            inventory.provision {
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
                }
            }
        }
    }
    
    @Test
    void fetchFileFromRemoteHostWithPermissions() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        withUser('devops') { inventory ->
            inventory.provision {
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
    }
        
    @Test(expected = TaskExecutionException)
    void fetchFileWithEmptyArguments() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    fetch { user = 'root' }
                }
            }
        }
    }

//    @Test
//    void fetchFileFromRemoteHostWithSudopass() {
//        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
//        withUser('sudops') { inventory ->
//            inventory.provision {
//                task actions: {
//                    file {
//                        content = 'message'
//                        target = '/test.txt'
//                        owner = 'root'
//                        mode = '0600'
//                        user = 'sudops'
//                        sudopass = 'sudops'
//                    }
//                    def result = fetch {
//                        source = '/test.txt'
//                        target = resultFile
//                        user = 'sudops'
//                        sudopass = 'sudops'
//                    }
//                    assert result.exitcode == 0
//                }
//                assert new File(resultFile).text == 'message'
//            }
//        }
//    }
}
