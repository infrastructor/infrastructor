package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class TemplateActionTest extends InventoryAwareTestBase {

    @Test
    void "generate a file on remote server"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    shell user: 'root', command: "groupadd infra"

                    template {
                        user = 'root'
                        source = 'build/resources/test/test.tmpl'
                        target = '/test.txt'
                        bindings = [message: "simple!"]
                        owner = DEVOPS
                        group = 'infra'
                        mode = '600'
                    }

                    def result = shell(user: 'root', command: "ls -alh /test.txt")
                    assert result.output.contains("test.txt")
                    assert result.output.contains("devops infra")
                    assert result.output.contains("-rw-------")

                    def catResult = shell(user: 'root', command: "cat /test.txt")
                    assert catResult.output.contains("simple")
                }
            }
        }
    }
    
    @Test
    void "generate a file on remote server with empty bindings"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        user = 'root'
                        source = 'build/resources/test/test.tmpl'
                        target = '/test.txt'
                        bindings = [message: "simple!"]
                    }

                    assert shell(user: 'root', command: "cat /test.txt").output.contains("simple")
                }
            }
        }
    }

    @Test
    void "generate a file on remote server with sudo and a password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        user = 'root'
                        source = 'build/resources/test/test.tmpl'
                        target = '/test.txt'
                        bindings = [message: "simple!"]
                        sudopass = 'sudops'
                    }

                    assert shell(user: 'root', sudopass: 'sudops', command: "cat /test.txt").output.contains("simple")
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void "generate a file on remote server with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        user = 'root'
                        source = 'build/resources/test/test.tmpl'
                        target = '/test.txt'
                        bindings = [message: "simple!"]
                        sudopass = 'wrong'
                    }
                }
            }
        }
    }

    @Test
    void "create a deep folder before template upload"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/etc/deep/deep/folder/test.txt'
                        bindings = [message: "simple!"]
                        user = 'root'
                    }

                    def result = shell("cat /etc/deep/deep/folder/test.txt")
                    assert result.output.contains("simple")
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "template with an unknown owner"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        owner = 'unknown'
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "template with an unknown group"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        group = 'unknown'
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void "template with an invalid mode"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        mode = '888'
                    }
                }
            }
        }
    }
    
    @Test
    void "template with encrypted values"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        source = 'build/resources/test/encrypted_part.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        mode = '644'
                        decryptionKey = 'secret'
                    }

                    def result = shell "cat /tmp/test.txt"

                    assert result.exitcode == 0
                    assert result.output.find(/secret message/)
                }
            }
        }
    }
    
    
    @Test
    void "template with fully encrypted content"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        source = 'build/resources/test/encrypted_full.tmpl'
                        target = '/tmp/test.txt'
                        mode = '644'
                        decryptionKey = 'secret'
                        decryptionMode = FULL
                    }

                    def result = shell "cat /tmp/test.txt"

                    assert result.exitcode == 0
                    assert result.output.find(/secret message/)
                }
            }
        }
    }
}