package io.infrastructor.core.processing.actions

import org.junit.Test

public class TemplateActionTest extends ActionTestBase {

    @Test
    public void generateAFileOnRemoteServer() {
        inventory.provisionAs('root') {
            task actions: {
                // setup
                shell("groupadd infra")
            
                // execution
                template {
                    source = 'build/resources/test/test.tmpl'
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
    public void generateAFileOnRemoteServerWithEmptyBindings() {
        inventory.provisionAs('root') {
            task actions: {
                template {
                    source = 'build/resources/test/test.tmpl'
                    target = '/test.txt'
                    bindings = [message: "simple!"]
                }
                
                // assertion
                def result = shell("ls -alh /test.txt")
                
                assert shell("cat /test.txt").output.contains("simple")
            }
        }
    }

    @Test
    public void createADeepFolderBeforeTemplateUpload() {
        inventory.provisionAs('devops') {
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
    
    @Test
    public void templateWithUnknownOwner() {
        inventory.provisionAs('devops') {
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
    
    @Test
    public void templateWithUnknownGroup() {
        inventory.provisionAs('devops') {
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
    
    @Test
    public void templateWithInvalidMode() {
        inventory.provisionAs('devops') {
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
    
    @Test
    public void templateWithEncryptedValues() {
        inventory.provisionAs('devops') {
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
    
    
    @Test
    public void templateWithFullyEncryptedContent() {
        inventory.provisionAs('devops') {
            task actions: {
                template {
                    source = 'build/resources/test/encrypted_full.tmpl'
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

