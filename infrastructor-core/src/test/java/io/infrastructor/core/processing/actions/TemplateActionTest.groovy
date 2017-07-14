package io.infrastructor.core.processing.actions

import org.junit.Test

public class TemplateActionTest extends ActionTestBase {

    @Test
    public void generateAFileOnRemoteServer() {
        inventory.provision {
            task(filter: {'as:root'}) { 
                // setup
                shell("groupadd infra")
            
                // execution
                template {
                    source = 'resources/test.tmpl'
                    target = '/test.txt'
                    bindings = [message: "simple!"]
                    owner = 'devops'
                    group = 'infra'
                    mode = '600'
                }
                
                // assertion
                def result = shell("ls -alh /test.txt")
                                
                assert result.output.contains("test.txt")
                assert result.output.contains("devops infra")
                assert result.output.contains("-rw-------")
                
                def catResult = shell("cat /test.txt")
                assert catResult.output.contains("simple")
            }
        }
    }
    
    @Test
    public void templateWithUnknownOwner() {
        inventory.provision {
            task(filter: {'as:devops'}) {
                def result = template {
                    source = 'resources/test.tmpl'
                    target = '/tmp/test.txt'
                    bindings = [message: "simple!"]
                    owner = 'unknown'
                }
                
                assert result.exitcode != 0
                assert result.error.find(/invalid spec/)
            }
        }
    }
    
    @Test
    public void templateWithUnknownGroup() {
        inventory.provision {
            task(filter: {'as:devops'}) {
                def result = template {
                    source = 'resources/test.tmpl'
                    target = '/tmp/test.txt'
                    bindings = [message: "simple!"]
                    group = 'unknown'
                }
                
                assert result.exitcode != 0
                assert result.error.find(/invalid group/)
            }
        }
    }
    
    @Test
    public void templateWithInvalidMode() {
        inventory.provision {
            task(filter: {'as:devops'}) {
                def result = template {
                    source = 'resources/test.tmpl'
                    target = '/tmp/test.txt'
                    bindings = [message: "simple!"]
                    mode = '888'
                }
                
                assert result.exitcode != 0
                assert result.error.find(/invalid mode/)
            }
        }
    }
    
    @Test
    public void templateWithEncryptedValues() {
        inventory.provision {
            task(filter: {'as:devops'}) {
                template {
                    source = 'resources/encrypted_part.tmpl'
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
    
    
    @Test
    public void templateWithFullyEncryptedContent() {
        inventory.provision {
            task(filter: {'as:devops'}) {
                template {
                    source = 'resources/encrypted_full.tmpl'
                    target = '/tmp/test.txt'
                    bindings = [message: "simple!"]
                    mode = '644'
                    decryptionKey = 'secret'
                    decryptionMode = FULL
                }
                
                def result = shell "cat /tmp/test.txt"
                
                assert result.exitcode == 0
                assert result.output.find(/secret message/)
                assert result.output.find(/simple!/)
            }
        }
    }
}

