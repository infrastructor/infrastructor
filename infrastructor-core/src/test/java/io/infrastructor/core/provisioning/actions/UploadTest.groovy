package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class UploadTest extends InventoryAwareTestBase {
    
    @Test
    void uploadAFileToRemoteHost() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    user user: 'root', name: 'test'
                    group user: 'root', name: 'testgroup'

                    upload {
                        user = 'root'
                        source = 'build/resources/test/fileupload.txt'
                        target = '/fileupload.txt'
                        owner = 'test'
                        group = 'testgroup'
                    }

                    assert shell("ls -alh /fileupload.txt").output.find(/test testgroup/)
                    assert shell("cat /fileupload.txt").output.find(/simple/)
                }
            }
        }
    } 
    
    @Test
    void uploadAFileToADeepFolder() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    user user: 'root', name: 'test'
                    group user: 'root', name: 'testgroup'

                    upload {
                        user = 'root'
                        source = 'build/resources/test/fileupload.txt'
                        target = '/etc/deep/deep/unknown/folder/fileupload.txt'
                        owner = 'test'
                        group = 'testgroup'
                    }

                    assert shell("ls -alh /etc/deep/deep/unknown/folder/fileupload.txt").output.find(/test testgroup/)
                    assert shell("cat /etc/deep/deep/unknown/folder/fileupload.txt").output.find(/simple/)
                }
            }
        }
    } 
    
    @Test(expected = TaskExecutionException)
    void uploadAFileToRemoteHostWithoutPermissions() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload {
                        source = 'build/resources/test/fileupload.txt'
                        target = '/fileupload.txt'
                    }
                }
            }
        }
    }
    
    @Test
    void uploadAFileToRemoteHostWithSudo() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def result = upload {
                        user = 'root'
                        source = 'build/resources/test/fileupload.txt'
                        target = '/fileupload.txt'
                    }

                    assert result.exitcode == 0
                    assert shell("cat /fileupload.txt").output.find(/simple/)
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void uploadFileWithUnknownOwner() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', owner: 'doesnotexist'
                }
            }
        }
    }
 
    @Test(expected = TaskExecutionException)
    void uploadFileWithUnknownGroup() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload user: 'root', source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', group: 'doesnotexist'
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void uploadFileWithInvalidMode() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload user: 'root', source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', mode: '8888'
                }
            }
        }
    }
    
    @Test
    void decryptAndUploadFileToRemoteHost() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def result = upload {
                        user = 'root'
                        source = 'build/resources/test/encrypted_full.tmpl'
                        target = '/fileupload.txt'
                        decryptionKey = 'secret'
                    }

                    assert result.exitcode == 0
                    assert shell("cat /fileupload.txt").output.find(/secret message/)
                }
            }
        }
    }
}

