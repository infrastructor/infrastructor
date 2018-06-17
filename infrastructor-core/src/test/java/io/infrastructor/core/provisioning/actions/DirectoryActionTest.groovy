package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class DirectoryActionTest extends InventoryAwareTestBase {
    
    @Test
    void createDirectoryAsRoot() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    user  user: 'root', name: "testuser"
                    group user: 'root', name: "testgroup"
                    directory user: 'root', target: '/var/simple', owner: 'testuser', group: 'testgroup', mode: '0600'
                    // assert
                    def result = shell user: 'root', command: "ls -dalh /var/simple"
                    assert result.output.contains("simple")
                    assert result.output.contains("testuser testgroup")
                    assert result.output.contains("drw------")
                }
            }
        }
    }
    
    @Test
    void createDirectoryAsDevopsWithSudo() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    directory user: 'root', target: '/etc/simple', owner: 'devops', group: 'devops', mode: '0600'
                    // assert
                    def result = shell("ls -dalh /etc/simple")
                    assert result.output.contains("simple")
                    assert result.output.contains("devops devops")
                    assert result.output.contains("drw------")
                }
            }
        }
    }
    
    @Test
    void createNestedDirectories() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    directory user: 'root', target: '/etc/simple/deep', mode: '600'

                    def resultDeep = shell user: 'root', command: 'ls -ldah /etc/simple/deep'
                    // assert
                    assert resultDeep.exitcode == 0
                    assert resultDeep.output.contains('deep')
                    assert resultDeep.output.contains("root root")
                    assert resultDeep.output.contains("drw-------")

                    def resultSimple = shell user: 'root', command: 'ls -ldah /etc/simple'
                    // assert
                    assert resultSimple.exitcode == 0
                    assert resultSimple.output.contains('simple')
                    assert resultSimple.output.contains("root root")
                    assert resultSimple.output.contains("drwxr-xr-x")

                    def resultEtc = shell user: 'root', command: 'ls -ldah /etc'
                    // assert
                    assert resultEtc.exitcode == 0
                    assert resultEtc.output.contains('etc')
                    assert resultEtc.output.contains("root root")
                    assert resultEtc.output.contains("drwxr-xr-x")
                }
            }
        }
    }

    @Test
    void createDirectoryAsDevopsWithoutSudo() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    def result = directory target: '/etc/simple'
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void createDirectoryWithUnknownOwner() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    def result = directory target: '/etc/simple', owner: 'doesnotexist'

                    // assert
                    assert result.exitcode != 0
                }
            }
        }
    }
 
    @Test
    void createDirectoryWithUnknownGroup() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execute
                    def result = directory target: '/etc/simple', group: 'doesnotexist'

                    // assert
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void createDirectoryWithInvalidMode() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    def result = directory user: 'root', target: '/etc/simple', mode: '8888'
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void createDirectoryWithEmptyMode() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    assert directory(user: 'root', target: '/etc/simple/test1', mode: '').exitcode == 0
                    assert shell("ls -alhd /etc/simple/test1").output.contains("drwxr-xr-x")

                    assert directory(user: 'root', target: '/etc/simple/test2', mode: null).exitcode == 0
                    assert shell("ls -alhd /etc/simple/test2").output.contains("drwxr-xr-x")

                    assert directory(user: 'root', target: '/etc/simple/test3', mode: 0).exitcode == 0
                    assert shell("ls -alhd /etc/simple/test3").output.contains("drwxr-xr-x")
                }
            }
        }
    }
}

