package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class UploadTest extends InventoryAwareTestBase {
    
    @Test
    void "upload a file"() {
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
    void "upload a file with sudo and a password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload {
                        user = 'root'
                        source = 'build/resources/test/fileupload.txt'
                        target = '/fileupload.txt'
                        sudopass = 'sudops'
                    }

                    assert shell("cat /fileupload.txt").output.find(/simple/)
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void "upload a file with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload {
                        user = 'root'
                        source = 'build/resources/test/fileupload.txt'
                        target = '/fileupload.txt'
                        sudopass = 'wrong'
                    }

                    assert shell("cat /fileupload.txt").output.find(/simple/)
                }
            }
        }
    }
    
    @Test
    void "upload a file to a deep folder"() {
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
    void "upload a file without permissions"() {
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
    void "upload a file with sudo"() {
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
    void "upload a file with an unknown owner"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', owner: 'doesnotexist'
                }
            }
        }
    }
 
    @Test(expected = TaskExecutionException)
    void "upload a file with an unknown group"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload user: 'root', source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', group: 'doesnotexist'
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "upload a file with an unknown mode"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    upload user: 'root', source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', mode: '8888'
                }
            }
        }
    }
    
    @Test
    void "decrypt and upload file"() {
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

