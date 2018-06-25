package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class TemplateActionTest extends InventoryAwareTestBase {

    @Test
    void generateAFileOnRemoteServer() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // setup
                    shell(user: 'root', command: "groupadd infra")

                    // execution
                    template {
                        user = 'root'
                        source = 'build/resources/test/test.tmpl'
                        target = '/test.txt'
                        bindings = [message: "simple!"]
                        owner = 'devops'
                        group = 'infra'
                        mode = '600'
                    }

                    // assertion
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
    void generateAFileOnRemoteServerWithEmptyBindings() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    template {
                        user = 'root'
                        source = 'build/resources/test/test.tmpl'
                        target = '/test.txt'
                        bindings = [message: "simple!"]
                    }

                    // assertion
                    def result = shell user: 'root', command: "ls -alh /test.txt"

                    assert shell(user: 'root', command: "cat /test.txt").output.contains("simple")
                }
            }
        }
    }

    @Test
    void createADeepFolderBeforeTemplateUpload() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    // execution
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
    
    @Test
    void templateWithUnknownOwner() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    def result = template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        owner = 'unknown'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid spec/)
                }
            }
        }
    }
    
    @Test
    void templateWithUnknownGroup() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    def result = template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        group = 'unknown'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid group/)
                }
            }
        }
    }
    
    @Test
    void templateWithInvalidMode() {
        withInventory { inventory ->
            inventory.provision {
                task actions: {
                    def result = template {
                        source = 'build/resources/test/test.tmpl'
                        target = '/tmp/test.txt'
                        bindings = [message: "simple!"]
                        mode = '888'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid mode/)
                }
            }
        }
    }
    
    @Test
    void templateWithEncryptedValues() {
        withInventory { inventory ->
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
    void templateWithFullyEncryptedContent() {
        withInventory { inventory ->
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