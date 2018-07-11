package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class FileActionTest extends InventoryAwareTestBase {
   
    @Test
    void writeAContentToAFileOnRemoteServerWithSudo() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    // setup
                    shell user: 'root', command: "groupadd infra"

                    // execution
                    file {
                        content = 'message'
                        target = '/test.txt'
                        owner = 'devops'
                        group = 'infra'
                        mode = '600'
                        user = 'root'
                    }

                    // assertion
                    def result = shell("ls -alh /test.txt")
                    assert result.output.contains("test.txt")
                    assert result.output.contains("devops infra")
                    assert result.output.contains("-rw-------")

                    // check file content
                    assert shell("cat /test.txt").output.contains("message")
                }
            }
        }
    }
    
    @Test
    void writeAFileOnRemoteServerWithoutSudo() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    // execution
                    def result = file {
                        content = 'message'
                        target = '/test.txt'
                    }
                    // assertion
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void writeAFileOnRemoteServerAsRoot() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    // execution
                    def result = file {
                        user    = 'root'
                        content = 'another message'
                        target  = '/test.txt'
                    }
                    // assertion
                    assert result.exitcode == 0
                    assert shell(user: 'root', command: "cat /test.txt").output.contains("another message")
                }
            }
        }
    }
    
    @Test
    void createFileWithUnknownOwner() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    def result = file user: 'root', target: '/etc/simple', content: "simple", owner: 'doesnotexist'
                    assert result.exitcode != 0
                }
            }
        }
    }
 
    @Test
    void createFileWithUnknownGroup() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    def result = file user: 'root', target: '/etc/simple', content: "simple", group: 'doesnotexist'
                    assert result.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void createFileWithInvalidMode() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    def result = file user: 'root', target: '/etc/simple', content: "simple", mode: '8888'
                    assert result.exitcode != 0
                }
            }
        }
    }
}

