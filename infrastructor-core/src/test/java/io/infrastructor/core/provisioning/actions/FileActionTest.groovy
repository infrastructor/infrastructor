package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class FileActionTest extends InventoryAwareTestBase {
   
    @Test
    void "create a file with sudo"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    shell user: 'root', command: "groupadd infra"

                    file {
                        content = 'message'
                        target = '/test.txt'
                        owner = DEVOPS
                        group = 'infra'
                        mode = '600'
                        user = 'root'
                    }

                    def result = shell("ls -alh /test.txt")
                    assert result.output.contains("test.txt")
                    assert result.output.contains("devops infra")
                    assert result.output.contains("-rw-------")
                    assert shell("cat /test.txt").output.contains("message")
                }
            }
        }
    }


    @Test
    void "create a file with sudo and a password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        content  = 'message'
                        target   = '/test.txt'
                        owner    = SUDOPS
                        group    = SUDOPS
                        mode     = '600'
                        user     = 'root'
                        sudopass = "sudops"
                    }

                    def result = shell("ls -alh /test.txt")
                    assert result.output.contains("test.txt")
                    assert result.output.contains("sudops sudops")
                    assert result.output.contains("-rw-------")
                    assert shell("cat /test.txt").output.contains("message")
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void "create a file with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        content  = 'message'
                        target   = '/test.txt'
                        owner    = SUDOPS
                        group    = SUDOPS
                        mode     = '600'
                        user     = 'root'
                        sudopass = "wrong"
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "create a file without sudo"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        content = 'message'
                        target = '/test.txt'
                    }
                }
            }
        }
    }
    
    @Test
    void "create a file as root"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def result = file {
                        user    = 'root'
                        content = 'another message'
                        target  = '/test.txt'
                    }
                    assert result.exitcode == 0
                    assert shell(user: 'root', command: "cat /test.txt").output.contains("another message")
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "create a file with an unknown owner"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file user: 'root', target: '/etc/simple', content: "simple", owner: 'doesnotexist'
                }
            }
        }
    }
 
    @Test(expected = TaskExecutionException)
    void "create a file with an unexisting group"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file user: 'root', target: '/etc/simple', content: "simple", group: 'doesnotexist'
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "create a file with an invalid group"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file user: 'root', target: '/etc/simple', content: "simple", mode: '8888'
                }
            }
        }
    }
}

