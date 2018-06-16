package io.infrastructor.core.processing.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class UploadTest extends InventoryAwareTestBase {
    
    @Test
    void uploadAFileToRemoteHost() {
        withInventory { inventory ->
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
        withInventory { inventory ->
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
    
    @Test
    void uploadAFileToRemoteHostWithoutPermissions() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    def result = upload {
                        source = 'build/resources/test/fileupload.txt'
                        target = '/fileupload.txt'
                    }

                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void uploadAFileToRemoteHostWithSudo() {
        withInventory { inventory ->
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
    
    @Test
    void uploadFileWithUnknownOwner() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    def result = upload source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', owner: 'doesnotexist'

                    // assert
                    assert result.exitcode != 0
                }
            }
        }
    }
 
    @Test
    void uploadFileWithUnknownGroup() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    def result = upload user: 'root', source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', group: 'doesnotexist'

                    // assert
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void uploadFileWithInvalidMode() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    def result = upload user: 'root', source: 'build/resources/test/fileupload.txt', target: '/tmp/simple.txt', mode: '8888'

                    // assert
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    
    @Test
    void decryptAndUploadFileToRemoteHost() {
        withInventory { inventory ->
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

