package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import io.infrastructor.core.utils.FlatUUID
import org.junit.Test

class FetchActionTest extends InventoryAwareTestBase {
    
    @Test
    void "fetch a file"() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        withUser(DEVOPS) { inventory ->
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

    @Test
    void "fetch a file to a deep directory"() {
        def resultFile = "/tmp/deep/deep/INFRATEST" + FlatUUID.flatUUID()
        withUser(DEVOPS) { inventory ->
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
    void "fetch a file without permissions"() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        withUser(DEVOPS) { inventory ->
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
    void "fetch a file with root permissions"() {
        def resultFile = "/tmp/INFRATEST" + FlatUUID.flatUUID()
        withUser(DEVOPS) { inventory ->
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
    void "fetch a file: no file path specified"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    fetch { user = 'root' }
                }
            }
        }
    }

    @Test
    void "fetch a file with sudo and password"() {
        def result_file = "/tmp/INFRATEST" + FlatUUID.flatUUID()

        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        content = 'message'
                        target = '/test.txt'
                        owner = SUDOPS
                        mode = '0600'
                        user = 'root'
                        sudopass = SUDOPS
                    }
                    def result = fetch {
                        source = '/test.txt'
                        target = result_file
                        user = 'root'
                        sudopass = SUDOPS
                    }
                    assert result.exitcode == 0
                }
                assert new File(result_file).text == 'message'
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void "fetch a file with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        content = 'message'
                        target = '/test.txt'
                        owner = SUDOPS
                        mode = '0600'
                        user = 'root'
                        sudopass = 'wrong'
                    }
                }
            }
        }
    }
}
